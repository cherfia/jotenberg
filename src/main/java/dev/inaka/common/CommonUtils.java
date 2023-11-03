package dev.inaka.common;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * CommonUtils is a utility class that provides various common utility methods for handling files and URLs.
 */
public class CommonUtils {
    private static final String[] extensions = {"bib", "doc", "xml", "docx", "fodt", "html", "ltx", "txt", "odt", "ott", "pdb", "pdf", "psw", "rtf", "sdw", "stw", "sxw", "uot", "vor", "wps", "epub", "png", "bmp", "emf", "eps", "fodg", "gif", "jpg", "met", "odd", "otg", "pbm", "pct", "pgm", "ppm", "ras", "std", "svg", "svm", "swf", "sxd", "sxw", "tiff", "xhtml", "xpm", "fodp", "potm", "pot", "pptx", "pps", "ppt", "pwp", "sda", "sdd", "sti", "sxi", "uop", "wmf", "csv", "dbf", "dif", "fods", "ods", "ots", "pxl", "sdc", "slk", "stc", "sxc", "uos", "xls", "xlt", "xlsx", "tif", "jpeg", "odp",};
    private static final String INDEX_HTML = "index.html";

    private CommonUtils() {
    }

    /**
     * Checks if a given string is a valid URL.
     *
     * @param url The URL to validate.
     * @return `true` if the URL is valid, `false` otherwise.
     */
    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    /**
     * Checks if a file is an index HTML file.
     *
     * @param file The file to check.
     * @return `true` if the file is an index HTML file, `false` otherwise.
     */
    public static boolean isIndex(File file) {
        String filename = FilenameUtils.getName(file.getName());
        return file.isFile() && filename.equals(INDEX_HTML);
    }

    /**
     * Checks if a file has a supported file extension.
     *
     * @param file The file to check.
     * @return `true` if the file has a supported extension, `false` otherwise.
     */
    public static boolean isSupported(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        return file.isFile() && Arrays.asList(extensions).contains(extension);
    }

    /**
     * Checks if a file is in Markdown format (extension: .md).
     *
     * @param file The file to check.
     * @return `true` if the file is a Markdown file, `false` otherwise.
     */
    public static boolean isMarkdown(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        return file.isFile() && extension.equals("md");
    }

    /**
     * Checks if a file is a PDF file (extension: .pdf).
     *
     * @param file The file to check.
     * @return `true` if the file is a PDF file, `false` otherwise.
     */
    public static boolean isPDF(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        return file.isFile() && extension.equals("pdf");
    }

    /**
     * Checks if a list of files contains an index HTML file.
     *
     * @param files The list of files to check.
     * @return `true` if the list contains an index HTML file, `false` otherwise.
     */
    public static boolean containsIndex(List<File> files) {
        return files.stream().anyMatch(CommonUtils::isIndex);
    }
}
