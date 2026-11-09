/**
 * Jotenberg, a Java client for Gotenberg's HTTP API.
 */
module io.bitizens.jotenberg {
    requires java.net.http;
    requires org.json;

    exports io.bitizens;
    exports io.bitizens.core;
    exports io.bitizens.common;
    exports io.bitizens.chromium;
    exports io.bitizens.libreoffice;
    exports io.bitizens.pdf;
    exports io.bitizens.system;
}
