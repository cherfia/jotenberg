package io.bitizens.libreoffice;

import io.bitizens.core.GotenbergResult;
import io.bitizens.internal.Multipart;
import io.bitizens.internal.Transport;

/**
 * LibreOfficeClient wraps Gotenberg's LibreOffice route. A non-2xx answer throws a
 * {@link io.bitizens.core.GotenbergException}.
 */
public final class LibreOfficeClient {
    private final Transport transport;

    /**
     * @param transport The transport to send with.
     */
    public LibreOfficeClient(Transport transport) {
        this.transport = transport;
    }

    /**
     * Converts office documents to PDF.
     *
     * @param conversion The documents and the options.
     * @return One PDF, or a ZIP for several documents unless a merge stage joins them.
     */
    public GotenbergResult convert(LibreOfficeConversion conversion) {
        Multipart form = new Multipart();
        conversion.addTo(form);
        return transport.post("forms/libreoffice/convert", form, conversion.headers());
    }
}
