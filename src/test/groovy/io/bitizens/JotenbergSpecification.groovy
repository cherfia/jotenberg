package io.bitizens

import io.bitizens.chromium.UrlConversion
import io.bitizens.core.GotenbergException
import io.bitizens.core.JotenbergException
import io.bitizens.pdf.PdfRequest
import io.bitizens.pdf.Stage
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.nio.file.Files

class JotenbergSpecification extends Specification {
    @AutoCleanup
    GotenbergStub stub = new GotenbergStub()

    def "should reject an endpoint that isn't an absolute http URL"() {
        when:
        new Jotenberg(endpoint)

        then:
        thrown(IllegalArgumentException)

        where:
        endpoint << ['htt://gotenberg.dev/', 'localhost:3000', 'ftp://gotenberg.dev', 'not a url']
    }

    def "should send each request on its own, without the previous request's parts"() {
        given:
        def client = new Jotenberg(stub.endpoint)

        when:
        client.chromium().convert(UrlConversion.of('https://first.example.com'))
        client.chromium().convert(UrlConversion.of('https://second.example.com'))

        then:
        stub.requests.size() == 2
        stub.requests[0].fields() == [url: 'https://first.example.com']
        stub.requests[1].fields() == [url: 'https://second.example.com']
    }

    def "should send basic auth credentials"() {
        given:
        def client = Jotenberg.builder().endpoint(stub.endpoint).basicAuth('user', 'pass').build()

        when:
        client.system().version()

        then:
        stub.last.header('Authorization') == 'Basic ' + 'user:pass'.bytes.encodeBase64().toString()
    }

    def "should prefer the API key over basic auth"() {
        given:
        def client = Jotenberg.builder().endpoint(stub.endpoint).basicAuth('user', 'pass').apiKey('secret').build()

        when:
        client.system().version()

        then:
        stub.last.header('X-Api-Key') == 'secret'
        stub.last.header('Authorization') == null
    }

    def "should use the hosted API when only an API key is set"() {
        expect:
        Jotenberg.builder().apiKey('secret').build().endpoint() == 'https://api.chromiumly.dev/'
    }

    def "should refuse to build without an endpoint or an API key"() {
        when:
        Jotenberg.builder().build()

        then:
        thrown(IllegalStateException)
    }

    def "should send custom headers with every request"() {
        given:
        def client = Jotenberg.builder().endpoint(stub.endpoint).header('X-Proxy-Token', 'abc').build()

        when:
        client.chromium().convert(UrlConversion.of('https://example.com'))

        then:
        stub.last.header('X-Proxy-Token') == 'abc'
    }

    def "should configure from the environment"() {
        given:
        def env = [
                GOTENBERG_ENDPOINT               : stub.endpoint,
                GOTENBERG_API_BASIC_AUTH_USERNAME: 'user',
                GOTENBERG_API_BASIC_AUTH_PASSWORD: 'pass'
        ]

        when:
        def client = Jotenberg.fromEnvironment(env.&get)
        client.system().version()

        then:
        client.endpoint() == stub.endpoint + '/'
        stub.last.header('Authorization').startsWith('Basic ')
    }

    def "should throw a GotenbergException with the status, trace and message of an error"() {
        given:
        stub.answer(400, "Invalid form data: form field 'nope' is unknown to this route", ['Gotenberg-Trace': 'abc-123'])
        def client = new Jotenberg(stub.endpoint)

        when:
        client.chromium().convert(UrlConversion.of('https://example.com'))

        then:
        def e = thrown(GotenbergException)
        e.status() == 400
        e.trace() == 'abc-123'
        e.body() == "Invalid form data: form field 'nope' is unknown to this route"
        e.message.contains('forms/chromium/convert/url')
    }

    def "should throw a JotenbergException when Gotenberg can't be reached"() {
        given:
        def port = stub.server.address.port
        stub.close()
        def client = new Jotenberg("http://127.0.0.1:${port}")

        when:
        client.system().version()

        then:
        thrown(JotenbergException)
    }

    def "should stream files from disk"() {
        given:
        def dir = Files.createTempDirectory('jotenberg')
        def pdf = dir.resolve('report.pdf')
        Files.write(pdf, '%PDF-1.7 report'.bytes)
        def client = new Jotenberg(stub.endpoint)

        when:
        client.pdf().pipe(PdfRequest.of(pdf), Stage.optimize())

        then:
        stub.last.files().find { it.filename == 'report.pdf' }.text == '%PDF-1.7 report'

        cleanup:
        dir.toFile().deleteDir()
    }
}
