package dev.inaka;

import dev.inaka.common.CommonUtils;
import dev.inaka.common.PageProperties;
import dev.inaka.common.exceptions.EmptyFileListException;
import dev.inaka.common.exceptions.IndexFileNotFoundExceptions;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
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
    private final MultipartEntityBuilder builder;
    private final CloseableHttpClient client;
    private final String endpoint;

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

    /**
     * Converts a document from a URL using the Chromium HTML conversion route.
     *
     * @param url            The URL of the document to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convert(String url, PageProperties pageProperties) throws IOException {
        if (!CommonUtils.isValidURL(url)) {
            throw new MalformedURLException();
        }

        this.builder.addTextBody("url", url);

        return executeHttpPostRequest(endpoint.concat(CHROMIUM_URL_ROUTE), pageProperties);
    }
    /**
     * Converts a document from a local file using the Chromium HTML conversion route.
     *
     * @param file           The local file to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convert(File file, PageProperties pageProperties) throws IOException {
        if (!CommonUtils.isIndex(file)) {
            throw new IndexFileNotFoundExceptions();
        }

        this.builder.addBinaryBody(file.getName(), file);

        return executeHttpPostRequest(endpoint.concat(CHROMIUM_HTML_ROUTE), pageProperties);
    }

    /**
     * Converts a list of Markdown files using the Chromium Markdown conversion route.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convert(List<File> files, PageProperties pageProperties) throws IOException {
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

        return executeHttpPostRequest(endpoint.concat(CHROMIUM_MARKDOWN_ROUTE), pageProperties);
    }

    /**
     * Converts a list of files using LibreOffice.
     * Please refer to https://gotenberg.dev/docs/modules/libreoffice for more details.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convertWithLibreOffice(List<File> files, PageProperties pageProperties) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        List<File> supportedFiles = files.stream().filter(CommonUtils::isSupported).toList();

        if (supportedFiles.isEmpty()) {
            throw new FileNotFoundException("File extensions are not supported by Libre Office. Please refer to https://gotenberg.dev/docs/modules/libreoffice for more details.");
        }

        supportedFiles.forEach(file -> this.builder.addBinaryBody(file.getName(), file));

        return executeHttpPostRequest(endpoint.concat(LIBRE_OFFICE_ROUTE), pageProperties);
    }

    /**
     * Converts a list of documents using PDF Engines.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public CloseableHttpResponse convertWithPdfEngines(List<File> files, PageProperties pageProperties) throws IOException {
        return getPdfEnginesHttpResponse(files, pageProperties, PDF_ENGINES_CONVERT_ROUTE);
    }

    /**
     * Merges a list of PDF documents using PDF Engines.
     *
     * @param files          The list of PDF files to merge.
     * @param pageProperties Page properties for the merge operation.
     * @return A CloseableHttpResponse containing the result of the merge.
     * @throws IOException If an I/O error occurs during the merge process.
     */
    public CloseableHttpResponse mergeWithPdfEngines(List<File> files, PageProperties pageProperties) throws IOException {
        return getPdfEnginesHttpResponse(files, pageProperties, PDF_ENGINES_MERGE_ROUTE);
    }

    /**
     * Executes an HTTP POST request for PDF Engines operations with the provided list of files, page properties,
     * and the specified PDF Engines route.
     *
     * @param files          The list of files to process with PDF Engines.
     * @param pageProperties Page properties for the PDF Engines operation.
     * @param pdfEnginesRoute The route for the PDF Engines operation (e.g., convert or merge).
     * @return A CloseableHttpResponse containing the result of the PDF Engines operation.
     * @throws IOException If an I/O error occurs during the PDF Engines operation.
     */
    private CloseableHttpResponse getPdfEnginesHttpResponse(List<File> files, PageProperties pageProperties, String pdfEnginesRoute) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        List<File> pdfFiles = files.stream().filter(CommonUtils::isPDF).toList();

        if (pdfFiles.isEmpty()) {
            throw new FileNotFoundException("No PDF file not found.");
        }

        pdfFiles.forEach(file -> this.builder.addBinaryBody(file.getName(), file));

        return executeHttpPostRequest(endpoint.concat(pdfEnginesRoute), pageProperties);
    }

    /**
     * Executes an HTTP POST request with the provided route and page properties.
     *
     * @param route          The route for the POST request.
     * @param pageProperties Page properties for the request.
     * @return A CloseableHttpResponse containing the response of the request.
     * @throws IOException If an I/O error occurs during the request.
     */
    private CloseableHttpResponse executeHttpPostRequest(String route, PageProperties pageProperties) throws IOException {
        buildPageProperties(pageProperties);
        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = this.builder.build();
        httpPost.setEntity(requestEntity);
        return this.client.execute(httpPost);
    }

    /**
     * Builds page properties using reflection and adds them to the request entity.
     *
     * @param pageProperties Page properties to add to the request entity.
     */
    private void buildPageProperties(PageProperties pageProperties) {
        Field[] fields = PageProperties.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                this.builder.addTextBody(field.getName(), field.get(pageProperties).toString());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() throws Exception {
        this.client.close();
    }
}