package io.bitizens.common;

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
    private static final String[] extensions = {
            "123",
            "602",
            "abw",
            "bib",
            "bmp",
            "cdr",
            "cgm",
            "cmx",
            "csv",
            "cwk",
            "dbf",
            "dif",
            "doc",
            "docm",
            "docx",
            "dot",
            "dotm",
            "dotx",
            "dxf",
            "emf",
            "eps",
            "epub",
            "fodg",
            "fodp",
            "fods",
            "fodt",
            "fopd",
            "gif",
            "htm",
            "html",
            "hwp",
            "jpeg",
            "jpg",
            "key",
            "ltx",
            "lwp",
            "mcw",
            "met",
            "mml",
            "mw",
            "numbers",
            "odd",
            "odg",
            "odm",
            "odp",
            "ods",
            "odt",
            "otg",
            "oth",
            "otp",
            "ots",
            "ott",
            "pages",
            "pbm",
            "pcd",
            "pct",
            "pcx",
            "pdb",
            "pdf",
            "pgm",
            "png",
            "pot",
            "potm",
            "potx",
            "ppm",
            "pps",
            "ppt",
            "pptm",
            "pptx",
            "psd",
            "psw",
            "pub",
            "pwp",
            "pxl",
            "ras",
            "rtf",
            "sda",
            "sdc",
            "sdd",
            "sdp",
            "sdw",
            "sgl",
            "slk",
            "smf",
            "stc",
            "std",
            "sti",
            "stw",
            "svg",
            "svm",
            "swf",
            "sxc",
            "sxd",
            "sxg",
            "sxi",
            "sxm",
            "sxw",
            "tga",
            "tif",
            "tiff",
            "txt",
            "uof",
            "uop",
            "uos",
            "uot",
            "vdx",
            "vor",
            "vsd",
            "vsdm",
            "vsdx",
            "wb2",
            "wk1",
            "wks",
            "wmf",
            "wpd",
            "wpg",
            "wps",
            "xbm",
            "xhtml",
            "xls",
            "xlsb",
            "xlsm",
            "xlsx",
            "xlt",
            "xltm",
            "xltx",
            "xlw",
            "xml",
            "xpm",
            "zabw"
    };
    private static final String INDEX_HTML = "index.html";
    private static final String HEADER_HTML = "header.html";
    private static final String FOOTER_HTML = "footer.html";

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
     * Checks if a file is a header HTML file.
     *
     * @param file The file to check.
     * @return `true` if the file is a header HTML file, `false` otherwise.
     */
    public static boolean isHeader(File file) {
        String filename = FilenameUtils.getName(file.getName());
        return file.isFile() && filename.equals(HEADER_HTML);
    }

    /**
     * Checks if a file is a footer HTML file.
     *
     * @param file The file to check.
     * @return `true` if the file is a footer HTML file, `false` otherwise.
     */
    public static boolean isFooter(File file) {
        String filename = FilenameUtils.getName(file.getName());
        return file.isFile() && filename.equals(FOOTER_HTML);
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
