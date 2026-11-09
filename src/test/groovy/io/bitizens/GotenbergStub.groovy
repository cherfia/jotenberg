package io.bitizens

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer

import java.nio.charset.StandardCharsets

/**
 * GotenbergStub is an in-process HTTP server that records the requests it receives and answers with a canned
 * response, so specs can check exactly what Jotenberg sends.
 */
class GotenbergStub implements AutoCloseable {
    static class Part {
        String name
        String filename
        byte[] content

        String getText() {
            new String(content, StandardCharsets.UTF_8)
        }
    }

    static class Recorded {
        String method
        String path
        Map<String, String> headers = [:]
        List<Part> parts = []

        List<Part> files() {
            parts.findAll { it.name == 'files' }
        }

        List<String> fileNames() {
            files()*.filename
        }

        Map<String, String> fields() {
            parts.findAll { it.filename == null }.collectEntries { [(it.name): it.text] }
        }

        String header(String name) {
            headers.find { it.key.equalsIgnoreCase(name) }?.value
        }
    }

    final HttpServer server
    final List<Recorded> requests = []
    int status = 200
    byte[] body = '%PDF-1.7 stub'.getBytes(StandardCharsets.UTF_8)
    Map<String, String> responseHeaders = [:]

    GotenbergStub() {
        server = HttpServer.create(new InetSocketAddress('127.0.0.1', 0), 0)
        server.createContext('/') { HttpExchange exchange -> handle(exchange) }
        server.start()
    }

    String getEndpoint() {
        "http://127.0.0.1:${server.address.port}"
    }

    Recorded getLast() {
        requests.last()
    }

    void answer(int status, String body, Map<String, String> headers = [:]) {
        this.status = status
        this.body = body.getBytes(StandardCharsets.UTF_8)
        this.responseHeaders = headers
    }

    private void handle(HttpExchange exchange) {
        def recorded = new Recorded(method: exchange.requestMethod, path: exchange.requestURI.path)
        exchange.requestHeaders.each { name, values -> recorded.headers[name] = values.join(',') }
        byte[] raw = exchange.requestBody.readAllBytes()
        def contentType = exchange.requestHeaders.getFirst('Content-Type')
        if (contentType?.startsWith('multipart/form-data')) {
            recorded.parts = parse(raw, contentType.split('boundary=')[1])
        }
        synchronized (requests) {
            requests << recorded
        }
        responseHeaders.each { name, value -> exchange.responseHeaders.add(name, value) }
        if (exchange.requestMethod == 'HEAD') {
            exchange.sendResponseHeaders(status, -1)
        } else {
            exchange.sendResponseHeaders(status, body.length == 0 ? -1 : body.length)
            exchange.responseBody.withStream { it.write(body) }
        }
        exchange.close()
    }

    private static List<Part> parse(byte[] raw, String boundary) {
        def text = new String(raw, StandardCharsets.ISO_8859_1)
        def parts = []
        text.split(java.util.regex.Pattern.quote("--${boundary}")).each { chunk ->
            if (chunk.startsWith('--') || chunk.trim().isEmpty()) {
                return
            }
            def body = chunk.substring(2) // leading CRLF
            int headerEnd = body.indexOf('\r\n\r\n')
            // headers are UTF-8, with quotes and backslashes escaped
            def headers = new String(body.substring(0, headerEnd).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
            def content = body.substring(headerEnd + 4, body.length() - 2) // trailing CRLF
            def quoted = /"((?:[^"\\]|\\.)*)"/
            def name = unescape((headers =~ /; name=${quoted}/)[0][1])
            def filenameMatch = headers =~ /filename=${quoted}/
            parts << new Part(
                    name: name,
                    filename: filenameMatch.find() ? unescape(filenameMatch.group(1)) : null,
                    content: content.getBytes(StandardCharsets.ISO_8859_1))
        }
        parts
    }

    private static String unescape(String value) {
        value.replaceAll(/\\(.)/, '\$1')
    }

    @Override
    void close() {
        server.stop(0)
    }
}
