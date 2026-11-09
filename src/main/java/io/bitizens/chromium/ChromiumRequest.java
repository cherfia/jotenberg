package io.bitizens.chromium;

import io.bitizens.common.Cookie;
import io.bitizens.common.DeliveredRequest;
import io.bitizens.common.EmulatedMediaType;
import io.bitizens.core.Multipart;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ChromiumRequest holds the fields every Chromium route takes, conversions and screenshots alike: waits, network
 * checks, media emulation and cookies.
 *
 * @param <T> The concrete request type, returned by the fluent setters.
 */
public abstract class ChromiumRequest<T extends ChromiumRequest<T>> extends DeliveredRequest<T> {
    private final Map<String, String> fields = new LinkedHashMap<>();
    private final Map<String, String> extraHttpHeaders = new LinkedHashMap<>();
    private final Map<String, String> emulatedMediaFeatures = new LinkedHashMap<>();
    private final JSONArray cookies = new JSONArray();

    /**
     * Sets a form field; {@code null} removes it.
     */
    final T set(String name, Object value) {
        if (value == null) {
            fields.remove(name);
        } else {
            fields.put(name, String.valueOf(value));
        }
        return self();
    }

    /** @param userAgent Replaces Chromium's {@code User-Agent} header. @return This request. */
    public T userAgent(String userAgent) {
        return set("userAgent", userAgent);
    }

    /** @param mediaType The CSS media type the page sees; print by default for PDFs. @return This request. */
    public T emulatedMediaType(EmulatedMediaType mediaType) {
        return set("emulatedMediaType", mediaType.mediaType());
    }

    /**
     * @param name  A CSS media feature, such as {@code prefers-color-scheme}.
     * @param value Its value, such as {@code dark}.
     * @return This request.
     */
    public T emulatedMediaFeature(String name, String value) {
        emulatedMediaFeatures.put(name, value);
        return self();
    }

    /** @param waitDelay How long to wait once the page loads, shorter than Gotenberg's timeout. @return This request. */
    public T waitDelay(Duration waitDelay) {
        return set("waitDelay", waitDelay.toMillis() + "ms");
    }

    /** @param selector Waits until an element matching this CSS selector is visible. @return This request. */
    public T waitForSelector(String selector) {
        return set("waitForSelector", selector);
    }

    /** @param expression Waits until this JavaScript expression returns true. @return This request. */
    public T waitForExpression(String expression) {
        return set("waitForExpression", expression);
    }

    /** @param status Waits until {@code window.status} equals this value. @return This request. */
    public T waitForWindowStatus(String status) {
        return set("waitForWindowStatus", status);
    }

    /**
     * Sends a header with the requests the page makes. End the value with {@code ;scope=<regex>} to send it only to
     * the URLs the regex matches.
     *
     * @param name  The header name.
     * @param value The header value.
     * @return This request.
     */
    public T extraHttpHeader(String name, String value) {
        extraHttpHeaders.put(name, value);
        return self();
    }

    /**
     * @param codes Answers 409 when the page answers one of these; {@code 499} stands for 400 to 499. Default
     *              {@code [499, 599]}.
     * @return This request.
     */
    public T failOnHttpStatusCodes(int... codes) {
        return set("failOnHttpStatusCodes", new JSONArray(codes));
    }

    /** @param codes Answers 409 when a resource the page loads answers one of these. @return This request. */
    public T failOnResourceHttpStatusCodes(int... codes) {
        return set("failOnResourceHttpStatusCodes", new JSONArray(codes));
    }

    /** @param domains Domains, subdomains included, the resource check leaves out. @return This request. */
    public T ignoreResourceHttpStatusDomains(String... domains) {
        return set("ignoreResourceHttpStatusDomains", new JSONArray(Arrays.asList(domains)));
    }

    /** @param fail Answers 409 when a resource fails to load on the network. @return This request. */
    public T failOnResourceLoadingFailed(boolean fail) {
        return set("failOnResourceLoadingFailed", fail);
    }

    /** @param fail Answers 409 when the page throws a JavaScript exception. @return This request. */
    public T failOnConsoleExceptions(boolean fail) {
        return set("failOnConsoleExceptions", fail);
    }

    /** @param types Answers 409 when the page writes one of these console types. @return This request. */
    public T failOnConsoleMessageTypes(ConsoleMessageType... types) {
        return set("failOnConsoleMessageTypes",
                new JSONArray(Arrays.stream(types).map(ConsoleMessageType::value).toList()));
    }

    /** @param skip {@code false} waits for the network to go idle; true by default. @return This request. */
    public T skipNetworkIdleEvent(boolean skip) {
        return set("skipNetworkIdleEvent", skip);
    }

    /** @param skip {@code false} waits for the network to go almost idle; true by default. @return This request. */
    public T skipNetworkAlmostIdleEvent(boolean skip) {
        return set("skipNetworkAlmostIdleEvent", skip);
    }

    /** @param omit Drops the default white background, for transparency. @return This request. */
    public T omitBackground(boolean omit) {
        return set("omitBackground", omit);
    }

    /** @param cookie A cookie to set before loading. @return This request. */
    public T cookie(Cookie cookie) {
        cookies.put(cookie.toJson());
        return self();
    }

    /**
     * Adds the Chromium fields and the request inputs to a form.
     *
     * @param form The form.
     */
    void addFieldsTo(Multipart form) {
        Map<String, String> all = new LinkedHashMap<>(fields);
        if (!emulatedMediaFeatures.isEmpty()) {
            all.put("emulatedMediaFeatures", new JSONObject(emulatedMediaFeatures).toString());
        }
        if (!extraHttpHeaders.isEmpty()) {
            all.put("extraHttpHeaders", new JSONObject(extraHttpHeaders).toString());
        }
        if (!cookies.isEmpty()) {
            all.put("cookies", cookies.toString());
        }
        all.forEach(form::field);
        addInputsTo(form);
    }
}
