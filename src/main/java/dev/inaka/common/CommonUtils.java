package dev.inaka.common;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class CommonUtils {
    private static final String[] extensions = {"bib", "doc", "xml", "docx", "fodt", "html", "ltx", "txt", "odt", "ott", "pdb", "pdf", "psw", "rtf", "sdw", "stw", "sxw", "uot", "vor", "wps", "epub", "png", "bmp", "emf", "eps", "fodg", "gif", "jpg", "met", "odd", "otg", "pbm", "pct", "pgm", "ppm", "ras", "std", "svg", "svm", "swf", "sxd", "sxw", "tiff", "xhtml", "xpm", "fodp", "potm", "pot", "pptx", "pps", "ppt", "pwp", "sda", "sdd", "sti", "sxi", "uop", "wmf", "csv", "dbf", "dif", "fods", "ods", "ots", "pxl", "sdc", "slk", "stc", "sxc", "uos", "xls", "xlt", "xlsx", "tif", "jpeg", "odp",};
    private static final String INDEX_HTML = "index.html";

    private CommonUtils() {
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public static boolean isIndex(File file) {
        String filename = FilenameUtils.getName(file.getName());
        return file.isFile() && filename.equals(INDEX_HTML);
    }

    public static boolean isSupported(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        return file.isFile() && Arrays.asList(extensions).contains(extension);
    }

    public static boolean isMarkdown(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        return file.isFile() && extension.equals("md");
    }

    public static boolean isPDF(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        return file.isFile() && extension.equals("pdf");
    }

    public static boolean containsIndex(List<File> files) {
        return files.stream().anyMatch(CommonUtils::isIndex);
    }
}
