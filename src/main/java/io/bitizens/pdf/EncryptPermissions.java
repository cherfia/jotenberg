package io.bitizens.pdf;

import org.json.JSONObject;

/**
 * EncryptPermissions sets what a reader without the owner password may do. Each permission is granted unless you
 * deny it. Viewers enforce them; only the user password keeps the content itself secret.
 */
public final class EncryptPermissions {
    private final JSONObject json = new JSONObject();

    private EncryptPermissions() {
    }

    /**
     * @return Permissions that grant everything until denied.
     */
    public static EncryptPermissions create() {
        return new EncryptPermissions();
    }

    public EncryptPermissions print(boolean allowed) {
        json.put("print", allowed);
        return this;
    }

    public EncryptPermissions printHighQuality(boolean allowed) {
        json.put("printHighQuality", allowed);
        return this;
    }

    public EncryptPermissions modifyContent(boolean allowed) {
        json.put("modifyContent", allowed);
        return this;
    }

    public EncryptPermissions extractText(boolean allowed) {
        json.put("extractText", allowed);
        return this;
    }

    public EncryptPermissions modifyAnnotations(boolean allowed) {
        json.put("modifyAnnotations", allowed);
        return this;
    }

    public EncryptPermissions fillForms(boolean allowed) {
        json.put("fillForms", allowed);
        return this;
    }

    public EncryptPermissions assemble(boolean allowed) {
        json.put("assemble", allowed);
        return this;
    }

    JSONObject toJson() {
        return new JSONObject(json.toMap());
    }
}
