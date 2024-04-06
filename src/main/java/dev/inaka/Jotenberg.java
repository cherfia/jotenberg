package dev.inaka;

import dev.inaka.chromium.ChromiumOptions;
import dev.inaka.chromium.ChromiumPageProperties;
import dev.inaka.common.CommonUtils;
import dev.inaka.common.exceptions.EmptyFileListException;
import dev.inaka.common.exceptions.IndexFileNotFoundExceptions;
import dev.inaka.core.ConversionHelper;
import dev.inaka.core.HTTPRequestManager;
import dev.inaka.libreoffice.LibreOfficeOptions;
import dev.inaka.libreoffice.LibreOfficePageProperties;
import dev.inaka.pdfengines.PDFEnginesConversionOptions;
import dev.inaka.pdfengines.PDFEnginesMergeOptions;
import dev.inaka.screenshots.ImageProperties;
import dev.inaka.screenshots.ScreenshotOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Jotenberg is a class that provides functionality for interacting with the Gotenberg API
 * to convert and process various types of documents.
 */
public class Jotenberg implements AutoCloseable {
    private static final String CHROMIUM_HTML_ROUTE = "forms/chromium/convert/html";
    private static final String CHROMIUM_MARKDOWN_ROUTE = "forms/chromium/convert/markdown";
    private static final String CHROMIUM_URL_ROUTE = "forms/chromium/convert/url";
    private static final String LIBRE_OFFICE_ROUTE = "forms/libreoffice/convert";
    private static final String PDF_ENGINES_CONVERT_ROUTE = "forms/pdfengines/convert";
    private static final String PDF_ENGINES_MERGE_ROUTE = "forms/pdfengines/merge";

    private static final String SCREENSHOTS_HTML_ROUTE = "forms/chromium/screenshot/html";
    private static final String SCREENSHOTS_MARKDOWN_ROUTE = "forms/chromium/screenshot/markdown";
    private static final String SCREENSHOTS_URL_ROUTE = "forms/chromium/screenshot/url";
    private final MultipartEntityBuilder builder;
    private final CloseableHttpClient client;
    private final String endpoint;
    private final ConversionHelper conversionHelper = new ConversionHelper(this);
    private final dev.inaka.core.HTTPRequestManager HTTPRequestManager = new HTTPRequestManager(this);

    /**
     * Constructs a Jotenberg object with the specified endpoint URL.
     *
     * @param endpoint The URL of the Gotenberg API endpoint.
     * @throws MalformedURLException If the provided endpoint URL is not a valid URL.
     */
    public Jotenberg(String endpoint) throws MalformedURLException {
        if (!CommonUtils.isValidURL(endpoint)) {
            throw new MalformedURLException();
        }
        this.endpoint = endpoint;
        this.builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        this.client = HttpClients.createDefault();
    }

    public ConversionHelper getConversionHelper() {
        return conversionHelper;
    }

    public CloseableHttpClient getClient() {
        return client;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public MultipartEntityBuilder getBuilder() {
        return builder;
    }

    /**
     * Converts a document from a URL using the Chromium URL conversion route.
     *
     * @param url            The URL of the document to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convert(String url, ChromiumPageProperties pageProperties, ChromiumOptions options) throws IOException {
        if (!CommonUtils.isValidURL(url)) {
            throw new MalformedURLException();
        }

        this.builder.addTextBody("url", url);

        return HTTPRequestManager.executeHttpPostRequest(endpoint.concat(CHROMIUM_URL_ROUTE), pageProperties, options);
    }

    /**
     * Converts a document from a local file using the Chromium HTML conversion route.
     *
     * @param file           The local file to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convert(File file, ChromiumPageProperties pageProperties, ChromiumOptions options) throws IOException {
        if (!CommonUtils.isIndex(file)) {
            throw new IndexFileNotFoundExceptions();
        }

        this.builder.addBinaryBody(file.getName(), file);

        return HTTPRequestManager.executeHttpPostRequest(endpoint.concat(CHROMIUM_HTML_ROUTE), pageProperties, options);
    }

    /**
     * Converts a list of Markdown files using the Chromium Markdown conversion route.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convert(List<File> files, ChromiumPageProperties pageProperties, ChromiumOptions options) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        if (!CommonUtils.containsIndex(files)) {
            throw new IndexFileNotFoundExceptions();
        }

        List<File> markdowns = files.stream().filter(CommonUtils::isMarkdown).toList();

        if (markdowns.isEmpty()) {
            throw new FileNotFoundException("Chromium's markdown route accepts a single index.html and markdown files.");
        }

        File indexFile = files.stream().filter(CommonUtils::isIndex).findFirst().orElseThrow();

        this.builder.addBinaryBody(indexFile.getName(), indexFile);

        markdowns.forEach(file -> this.builder.addBinaryBody(file.getName(), file));

        return HTTPRequestManager.executeHttpPostRequest(endpoint.concat(CHROMIUM_MARKDOWN_ROUTE), pageProperties, options);
    }


    /**
     * Screenshots a URL using the Chromium URL screenshot route.
     *
     * @param url             The URL to screenshot.
     * @param imageProperties Image properties for the screenshot.
     * @return A CloseableHttpResponse containing the result of the screenshot.
     * @throws IOException If an I/O error occurs during the screenshot process.
     */
    public CloseableHttpResponse capture(String url, ImageProperties imageProperties, ScreenshotOptions options) throws IOException {
        if (!CommonUtils.isValidURL(url)) {
            throw new MalformedURLException();
        }

        this.builder.addTextBody("url", url);

        return HTTPRequestManager.executeHttpPostRequest(endpoint.concat(SCREENSHOTS_URL_ROUTE), imageProperties, options);
    }

    /**
     * Screenshots a local file using the Chromium HTML screenshot route.
     *
     * @param file            The local file to screenshot.
     * @param imageProperties image properties for the screenshot.
     * @return A CloseableHttpResponse containing the result of the screenshot.
     * @throws IOException If an I/O error occurs during the screenshot process.
     */
    public CloseableHttpResponse capture(File file, ImageProperties imageProperties, ScreenshotOptions options) throws IOException {
        if (!CommonUtils.isIndex(file)) {
            throw new IndexFileNotFoundExceptions();
        }

        this.builder.addBinaryBody(file.getName(), file);

        return HTTPRequestManager.executeHttpPostRequest(endpoint.concat(SCREENSHOTS_HTML_ROUTE), imageProperties, options);
    }

    /**
     * Screenshots a list of Markdown files using the Chromium Markdown screenshot route.
     *
     * @param files           The list of files to screenshot.
     * @param imageProperties Image properties for the screenshot.
     * @param options         Screenshot options.
     * @return A CloseableHttpResponse containing the result of the screenshot.
     * @throws IOException If an I/O error occurs during the screenshot process.
     */
    public CloseableHttpResponse capture(List<File> files, ImageProperties imageProperties, ScreenshotOptions options) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        if (!CommonUtils.containsIndex(files)) {
            throw new IndexFileNotFoundExceptions();
        }

        List<File> markdowns = files.stream().filter(CommonUtils::isMarkdown).toList();

        if (markdowns.isEmpty()) {
            throw new FileNotFoundException("Chromium's screenshots markdown route accepts a single index.html and markdown files.");
        }

        File indexFile = files.stream().filter(CommonUtils::isIndex).findFirst().orElseThrow();

        this.builder.addBinaryBody(indexFile.getName(), indexFile);

        markdowns.forEach(file -> this.builder.addBinaryBody(file.getName(), file));

        return HTTPRequestManager.executeHttpPostRequest(endpoint.concat(SCREENSHOTS_MARKDOWN_ROUTE), imageProperties, options);
    }

    /**
     * Converts a list of files using LibreOffice.
     * Please refer to https://gotenberg.dev/docs/modules/libreoffice for more details.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @param options        LibreOffice conversion options.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convertWithLibreOffice(List<File> files, LibreOfficePageProperties pageProperties, LibreOfficeOptions options) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        List<File> supportedFiles = files.stream().filter(CommonUtils::isSupported).toList();

        if (supportedFiles.isEmpty()) {
            throw new FileNotFoundException("File extensions are not supported by Libre Office. Please refer to https://gotenberg.dev/docs/modules/libreoffice for more details.");
        }

        supportedFiles.forEach(file -> this.builder.addBinaryBody(file.getName(), file));

        return HTTPRequestManager.executeHttpPostRequest(endpoint.concat(LIBRE_OFFICE_ROUTE), pageProperties, options);
    }

    /**
     * Converts a list of documents using PDF Engines.
     *
     * @param files   The list of files to convert.
     * @param options PDF Engines conversion options.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convertWithPdfEngines(List<File> files, PDFEnginesConversionOptions options) throws IOException {
        return HTTPRequestManager.getPdfEnginesHttpResponse(files, options, PDF_ENGINES_CONVERT_ROUTE);
    }

    /**
     * Merges a list of PDF documents using PDF Engines.
     *
     * @param files   The list of PDF files to merge.
     * @param options PDF Engines merge options.
     * @return A CloseableHttpResponse containing the result of the merge.
     * @throws IOException If an I/O error occurs during the merge process.
     */
    public CloseableHttpResponse mergeWithPdfEngines(List<File> files, PDFEnginesMergeOptions options) throws IOException {
        return HTTPRequestManager.getPdfEnginesHttpResponse(files, options, PDF_ENGINES_MERGE_ROUTE);
    }


    @Override
    public void close() throws Exception {
        this.client.close();
    }
}