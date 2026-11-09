package io.bitizens.chromium;

import io.bitizens.common.FileInput;
import io.bitizens.internal.Multipart;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PageSource is what Chromium loads: a URL, or an {@code index.html} with its markdown files and assets.
 */
final class PageSource {
    private final String url;
    private final FileInput index;
    private final List<FileInput> markdowns = new ArrayList<>();
    private final List<FileInput> assets = new ArrayList<>();

    private PageSource(String url, FileInput index) {
        this.url = url;
        this.index = index;
    }

    static PageSource url(String url) {
        Objects.requireNonNull(url, "url");
        URI uri = URI.create(url);
        if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme())) {
            throw new IllegalArgumentException("Chromium loads http and https URLs, got " + url);
        }
        return new PageSource(url, null);
    }

    static PageSource html(FileInput index) {
        return new PageSource(null, Objects.requireNonNull(index, "index").withName("index.html"));
    }

    void markdown(FileInput markdown) {
        markdowns.add(markdown);
    }

    void asset(FileInput asset) {
        assets.add(asset);
    }

    void checkMarkdown() {
        if (markdowns.isEmpty()) {
            throw new IllegalArgumentException("The markdown route takes at least one markdown file");
        }
    }

    void addTo(Multipart form) {
        if (url != null) {
            form.field("url", url);
            return;
        }
        index.addTo(form, "files");
        markdowns.forEach(markdown -> markdown.addTo(form, "files"));
        assets.forEach(asset -> asset.addTo(form, "files"));
    }
}
