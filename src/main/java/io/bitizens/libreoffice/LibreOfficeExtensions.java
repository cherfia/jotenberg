package io.bitizens.libreoffice;

import java.util.Set;

/**
 * The extensions Gotenberg's LibreOffice route converts.
 *
 * @see <a href="https://gotenberg.dev/docs/libreoffice/convert#formats">Gotenberg – LibreOffice formats</a>
 */
final class LibreOfficeExtensions {
    static final Set<String> SUPPORTED = Set.of(
            "123", "602", "abw", "bib", "bmp", "cdr", "cgm", "cmx", "csv", "cwk", "dbf", "dif",
            "doc", "docm", "docx", "dot", "dotm", "dotx", "dxf", "emf", "eps", "fodg", "fodp",
            "fods", "fodt", "fopd", "gif", "htm", "html", "hwp", "jpeg", "jpg", "key", "ltx", "lwp",
            "mcw", "met", "mml", "mw", "numbers", "odd", "odg", "odm", "odp", "ods", "odt", "otg",
            "oth", "otp", "ots", "ott", "pages", "pbm", "pcd", "pct", "pcx", "pdb", "pdf", "pgm",
            "png", "pot", "potm", "potx", "ppm", "pps", "ppsm", "ppsx", "ppt", "pptm", "pptx",
            "psd", "psw", "pub", "pwp", "pxl", "ras", "rtf", "sda", "sdc", "sdd", "sdp", "sdw",
            "sgl", "slk", "smf", "stc", "std", "sti", "stw", "svg", "svm", "swf", "sxc", "sxd",
            "sxg", "sxi", "sxm", "sxw", "tga", "tif", "tiff", "txt", "uof", "uop", "uos", "uot",
            "vdx", "vor", "vsd", "vsdm", "vsdx", "wb2", "wk1", "wks", "wmf", "wpd", "wpg", "wps",
            "xbm", "xhtml", "xls", "xlsb", "xlsm", "xlsx", "xlt", "xltm", "xltx", "xlw", "xml",
            "xpm", "zabw");

    private LibreOfficeExtensions() {
    }
}
