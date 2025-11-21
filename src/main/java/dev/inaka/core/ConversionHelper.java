package dev.inaka.core;

import dev.inaka.Jotenberg;
import dev.inaka.chromium.ChromiumPageProperties;
import dev.inaka.common.AbstractOptions;
import dev.inaka.libreoffice.LibreOfficeOptions;
import dev.inaka.libreoffice.LibreOfficePageProperties;
import dev.inaka.pdfengines.PDFEnginesOptions;
import dev.inaka.screenshots.ImageProperties;

import java.io.File;
import java.lang.reflect.Field;

/**
 * ConversionHelper is a class that provides helper methods for converting between different formats.
 */
public class ConversionHelper {
    private final Jotenberg jotenberg;

    public ConversionHelper(Jotenberg jotenberg) {
        this.jotenberg = jotenberg;
    }

    /**
     * Builds Chromium page properties using reflection and adds them to the request entity.
     *
     * @param pageProperties Chromium page properties to add to the request entity.
     */
    void buildPageProperties(ChromiumPageProperties pageProperties) {
        Field[] fields = ChromiumPageProperties.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(pageProperties);
                if (value != null) {
                    jotenberg.getBuilder().addTextBody(field.getName(), (String) value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds Chromium options using reflection and adds them to the request entity.
     *
     * @param options Chromium options to add to the request entity.
     */
    void buildChromiumOptions(AbstractOptions options) {
        Field[] fields = AbstractOptions.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(options);
                if (value != null) {
                    if (value instanceof File) {
                        jotenberg.getBuilder().addBinaryBody(field.getName(), (File) value);
                    } else if (value instanceof java.util.List) {
                        @SuppressWarnings("unchecked")
                        java.util.List<File> fileList = (java.util.List<File>) value;
                        for (File file : fileList) {
                            jotenberg.getBuilder().addBinaryBody("embeds", file);
                        }
                    } else {
                        jotenberg.getBuilder().addTextBody(field.getName(), (String) value);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds LibreOffice page properties using reflection and adds them to the request entity.
     *
     * @param pageProperties LibreOffice page properties to add to the request entity.
     */
    void buildPageProperties(LibreOfficePageProperties pageProperties) {
        Field[] fields = LibreOfficePageProperties.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(pageProperties);
                if (value != null) {
                    jotenberg.getBuilder().addTextBody(field.getName(), (String) value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds LibreOffice options using reflection and adds them to the request entity.
     *
     * @param options LibreOffice options to add to the request entity.
     */
    void buildPageOptions(LibreOfficeOptions options) {
        Field[] fields = LibreOfficeOptions.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(options);
                if (value != null) {
                    jotenberg.getBuilder().addTextBody(field.getName(), (String) value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds image properties using reflection and adds them to the request entity.
     *
     * @param imageProperties image properties to add to the request entity.
     */
    void buildImageProperties(ImageProperties imageProperties) {
        Field[] fields = ImageProperties.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(imageProperties);
                if (value != null) {
                    jotenberg.getBuilder().addTextBody(field.getName(), (String) value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * Builds PDF engines options using reflection and adds them to the request entity.
     *
     * @param options PDF engines options to add to the request entity.
     */
    public void buildPdfEngineOptions(PDFEnginesOptions options) {
        if (options == null) {
            return;
        }

        Field[] fields = options.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(options);
                if (value != null) {
                    if (value instanceof java.util.List) {
                        @SuppressWarnings("unchecked")
                        java.util.List<File> fileList = (java.util.List<File>) value;
                        for (File file : fileList) {
                            jotenberg.getBuilder().addBinaryBody("embeds", file);
                        }
                    } else {
                        jotenberg.getBuilder().addTextBody(field.getName(), (String) value);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}