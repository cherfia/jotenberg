package io.bitizens.chromium;

/**
 * ConsoleMessageType is a console message type that makes a conversion answer 409 when the page writes one, with
 * {@code failOnConsoleMessageTypes}.
 */
public enum ConsoleMessageType {
    LOG("log"),
    DEBUG("debug"),
    INFO("info"),
    WARNING("warning"),
    ERROR("error"),
    ASSERT("assert");

    private final String value;

    ConsoleMessageType(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
