package io.bitizens.pdf;

import io.bitizens.common.FileInput;
import io.bitizens.core.Multipart;
import org.json.JSONArray;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * StageUploads collects the files the stages of a request name, so each is uploaded once.
 */
public final class StageUploads {
    private final Map<String, FileInput> uploads = new LinkedHashMap<>();

    /**
     * Registers a stage file for upload, refusing a different file under a name already taken.
     *
     * @param file The stage file.
     * @return The name the stage refers to it by.
     */
    String register(StageFile file) {
        FileInput upload = file.upload();
        if (upload != null) {
            FileInput existing = uploads.putIfAbsent(file.name(), upload);
            if (existing != null && !existing.equals(upload)) {
                throw new IllegalArgumentException("Two different stage files are named " + file.name()
                        + "; give one of them another name");
            }
        }
        return file.name();
    }

    /**
     * Adds the {@code stages} field to a form, and the files the stages name.
     *
     * @param form   The form.
     * @param stages The stages, in order; none adds nothing.
     */
    public static void addStagesTo(Multipart form, List<Stage> stages) {
        if (stages.isEmpty()) {
            return;
        }
        StageUploads uploads = new StageUploads();
        JSONArray array = new JSONArray();
        stages.forEach(stage -> array.put(stage.toJson(uploads)));
        form.field("stages", array.toString());
        uploads.addTo(form);
    }

    /**
     * Adds one stage's options to a form as its own route takes them, and the files it names.
     *
     * @param form  The form.
     * @param stage The stage.
     */
    public static void addStageFieldsTo(Multipart form, Stage stage) {
        StageUploads uploads = new StageUploads();
        stage.addFieldsTo(form, uploads);
        uploads.addTo(form);
    }

    private void addTo(Multipart form) {
        uploads.values().forEach(upload -> upload.addTo(form, "files"));
    }
}
