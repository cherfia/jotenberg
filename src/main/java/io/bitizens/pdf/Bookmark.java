package io.bitizens.pdf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Bookmark is an entry of a PDF's outline, the bookmarks viewers show beside the pages.
 *
 * @param title      The bookmark's title.
 * @param pageNumber The page it points at, counting from 1.
 * @param children   The bookmarks nested under it.
 */
public record Bookmark(String title, int pageNumber, List<Bookmark> children) {
    public Bookmark {
        children = List.copyOf(children);
    }

    /**
     * @param title      The bookmark's title.
     * @param pageNumber The page it points at, counting from 1.
     * @param children   The bookmarks nested under it.
     * @return The bookmark.
     */
    public static Bookmark of(String title, int pageNumber, Bookmark... children) {
        return new Bookmark(title, pageNumber, List.of(children));
    }

    JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("pageNumber", pageNumber);
        if (!children.isEmpty()) {
            JSONArray nested = new JSONArray();
            children.forEach(child -> nested.put(child.toJson()));
            json.put("children", nested);
        }
        return json;
    }

    static List<Bookmark> fromJson(JSONArray array) {
        List<Bookmark> bookmarks = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            JSONArray children = json.optJSONArray("children");
            bookmarks.add(new Bookmark(
                    json.getString("title"),
                    json.getInt("pageNumber"),
                    children == null ? List.of() : fromJson(children)));
        }
        return bookmarks;
    }
}
