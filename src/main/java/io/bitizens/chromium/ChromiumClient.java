package io.bitizens.chromium;

import io.bitizens.core.Multipart;
import io.bitizens.core.Transport;

/**
 * ChromiumClient wraps Gotenberg's Chromium routes: it prints URLs, HTML and Markdown to PDF, and captures them as
 * images. A non-2xx answer throws a {@link io.bitizens.core.GotenbergException}.
 *
 * @see <a href="https://gotenberg.dev/docs/chromium/convert-url">Gotenberg – Chromium</a>
 */
public final class ChromiumClient {
    private static final String CONVERT = "forms/chromium/convert/";
    private static final String SCREENSHOT = "forms/chromium/screenshot/";

    private final Transport transport;

    /**
     * @param transport The transport to send with.
     */
    public ChromiumClient(Transport transport) {
        this.transport = transport;
    }

    /** @return The PDF, or a ZIP when a stage makes several files. */
    public byte[] convert(UrlConversion conversion) {
        Multipart form = new Multipart();
        conversion.addTo(form);
        return transport.post(CONVERT + "url", form, conversion.headers());
    }

    /** @return The PDF, or a ZIP when a stage makes several files. */
    public byte[] convert(HtmlConversion conversion) {
        Multipart form = new Multipart();
        conversion.addTo(form);
        return transport.post(CONVERT + "html", form, conversion.headers());
    }

    /** @return The PDF, or a ZIP when a stage makes several files. */
    public byte[] convert(MarkdownConversion conversion) {
        Multipart form = new Multipart();
        conversion.addTo(form);
        return transport.post(CONVERT + "markdown", form, conversion.headers());
    }

    /** @return The image, as PNG unless the screenshot sets another format. */
    public byte[] screenshot(UrlScreenshot screenshot) {
        Multipart form = new Multipart();
        screenshot.addTo(form);
        return transport.post(SCREENSHOT + "url", form, screenshot.headers());
    }

    /** @return The image, as PNG unless the screenshot sets another format. */
    public byte[] screenshot(HtmlScreenshot screenshot) {
        Multipart form = new Multipart();
        screenshot.addTo(form);
        return transport.post(SCREENSHOT + "html", form, screenshot.headers());
    }

    /** @return The image, as PNG unless the screenshot sets another format. */
    public byte[] screenshot(MarkdownScreenshot screenshot) {
        Multipart form = new Multipart();
        screenshot.addTo(form);
        return transport.post(SCREENSHOT + "markdown", form, screenshot.headers());
    }
}
