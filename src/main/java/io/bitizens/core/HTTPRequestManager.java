package io.bitizens.core;

import io.bitizens.Jotenberg;
import io.bitizens.chromium.ChromiumOptions;
import io.bitizens.chromium.ChromiumPageProperties;
import io.bitizens.common.CommonUtils;
import io.bitizens.common.exceptions.EmptyFileListException;
import io.bitizens.libreoffice.LibreOfficeOptions;
import io.bitizens.libreoffice.LibreOfficePageProperties;
import io.bitizens.pdfengines.PDFEnginesEncryptOptions;
import io.bitizens.pdfengines.PDFEnginesOptions;
import io.bitizens.screenshots.ImageProperties;
import io.bitizens.screenshots.ScreenshotOptions;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * HTTPRequestManager is a class that manages HTTP requests for Jotenberg.
 */
public class HTTPRequestManager {
    private final Jotenberg jotenberg;

    public HTTPRequestManager(Jotenberg jotenberg) {
        this.jotenberg = jotenberg;
    }

    /**
     * Executes an HTTP POST request for PDF Engines operations with the provided list of files, page properties,
     * and the specified PDF Engines route.
     *
     * @param files   The list of files to process with PDF Engines.
     * @param options Options for the PDF Engines operation.
     * @param route   The route for the PDF Engines operation (e.g., convert or merge).
     * @return A CloseableHttpResponse containing the result of the PDF Engines operation.
     * @throws IOException If an I/O error occurs during the PDF Engines operation.
     */
    public CloseableHttpResponse getPdfEnginesHttpResponse(List<File> files, PDFEnginesOptions options, String route) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        List<File> pdfFiles = files.stream().filter(CommonUtils::isPDF).toList();

        if (pdfFiles.isEmpty()) {
            throw new FileNotFoundException("No PDF file not found.");
        }

        pdfFiles.forEach(file -> jotenberg.getBuilder().addBinaryBody(file.getName(), file));

        if (options != null) {
            jotenberg.getConversionHelper().buildPdfEngineOptions(options);
        }
        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = jotenberg.getBuilder().build();
        httpPost.setEntity(requestEntity);
        return jotenberg.getClient().execute(httpPost);
    }

    /**
     * Executes an HTTP POST request with the provided route and page properties.
     *
     * @param route          The route for the POST request.
     * @param pageProperties Page properties for the request.
     * @param options        Chromium conversion options.
     * @return A CloseableHttpResponse containing the response of the request.
     * @throws IOException If an I/O error occurs during the request.
     */
    public CloseableHttpResponse executeHttpPostRequest(String route, ChromiumPageProperties pageProperties, ChromiumOptions options) throws IOException {
        jotenberg.getConversionHelper().buildPageProperties(pageProperties);
        jotenberg.getConversionHelper().buildChromiumOptions(options);
        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = jotenberg.getBuilder().build();
        httpPost.setEntity(requestEntity);
        return jotenberg.getClient().execute(httpPost);
    }

    /**
     * Executes an HTTP POST request with the provided route and page properties.
     *
     * @param route          The route for the POST request.
     * @param pageProperties Page properties for the request.
     * @param options        LibreOffice conversion options.
     * @return A CloseableHttpResponse containing the response of the request.
     * @throws IOException If an I/O error occurs during the request.
     */
    public CloseableHttpResponse executeHttpPostRequest(String route, LibreOfficePageProperties pageProperties, LibreOfficeOptions options) throws IOException {
        jotenberg.getConversionHelper().buildPageProperties(pageProperties);
        jotenberg.getConversionHelper().buildPageOptions(options);
        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = jotenberg.getBuilder().build();
        httpPost.setEntity(requestEntity);
        return jotenberg.getClient().execute(httpPost);
    }

    /**
     * Executes an HTTP POST request with the provided route and page properties.
     *
     * @param route           The route for the POST request.
     * @param imageProperties Image properties for the request.
     * @param options         Screenshot capture options.
     * @return A CloseableHttpResponse containing the response of the request.
     * @throws IOException If an I/O error occurs during the request.
     */
    public CloseableHttpResponse executeHttpPostRequest(String route, ImageProperties imageProperties, ScreenshotOptions options) throws IOException {
        jotenberg.getConversionHelper().buildImageProperties(imageProperties);
        jotenberg.getConversionHelper().buildChromiumOptions(options);
        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = jotenberg.getBuilder().build();
        httpPost.setEntity(requestEntity);
        return jotenberg.getClient().execute(httpPost);
    }

    /**
     * Executes an HTTP POST request for PDF Engines write metadata operation.
     *
     * @param files    The list of PDF files.
     * @param metadata The metadata to write as JSON string.
     * @param route    The route for the PDF Engines operation.
     * @return A CloseableHttpResponse containing the result.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public CloseableHttpResponse getPdfEnginesHttpResponseWithMetadata(List<File> files, String metadata, String route) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        List<File> pdfFiles = files.stream().filter(CommonUtils::isPDF).toList();

        if (pdfFiles.isEmpty()) {
            throw new FileNotFoundException("No PDF file not found.");
        }

        jotenberg.getBuilder().addTextBody("metadata", metadata);
        pdfFiles.forEach(file -> jotenberg.getBuilder().addBinaryBody(file.getName(), file));

        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = jotenberg.getBuilder().build();
        httpPost.setEntity(requestEntity);
        return jotenberg.getClient().execute(httpPost);
    }

    /**
     * Executes an HTTP POST request for PDF Engines split operation.
     *
     * @param files      The list of PDF files to split.
     * @param splitMode  The split mode ('pages' or 'intervals').
     * @param splitSpan  The split span.
     * @param splitUnify Whether to unify (only for pages mode).
     * @param flatten    Whether to flatten.
     * @param route      The route for the PDF Engines operation.
     * @return A CloseableHttpResponse containing the result.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public CloseableHttpResponse getPdfEnginesHttpResponseWithSplit(List<File> files, String splitMode, String splitSpan, Boolean splitUnify, Boolean flatten, String route) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        List<File> pdfFiles = files.stream().filter(CommonUtils::isPDF).toList();

        if (pdfFiles.isEmpty()) {
            throw new FileNotFoundException("No PDF file not found.");
        }

        pdfFiles.forEach(file -> jotenberg.getBuilder().addBinaryBody(file.getName(), file));
        jotenberg.getBuilder().addTextBody("splitMode", splitMode);
        jotenberg.getBuilder().addTextBody("splitSpan", splitSpan);

        if (splitUnify != null) {
            jotenberg.getBuilder().addTextBody("splitUnify", String.valueOf(splitUnify));
        }

        if (flatten != null) {
            jotenberg.getBuilder().addTextBody("flatten", String.valueOf(flatten));
        }

        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = jotenberg.getBuilder().build();
        httpPost.setEntity(requestEntity);
        return jotenberg.getClient().execute(httpPost);
    }

    /**
     * Executes an HTTP POST request for PDF Engines encrypt operation.
     *
     * @param files   The list of PDF files to encrypt.
     * @param options PDF Engines encrypt options.
     * @param route   The route for the PDF Engines operation.
     * @return A CloseableHttpResponse containing the result.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public CloseableHttpResponse getPdfEnginesHttpResponseWithEncrypt(List<File> files, PDFEnginesEncryptOptions options, String route) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        List<File> pdfFiles = files.stream().filter(CommonUtils::isPDF).toList();

        if (pdfFiles.isEmpty()) {
            throw new FileNotFoundException("No PDF file not found.");
        }

        pdfFiles.forEach(file -> jotenberg.getBuilder().addBinaryBody(file.getName(), file));
        jotenberg.getBuilder().addTextBody("userPassword", options.getUserPassword());

        if (options.getOwnerPassword() != null) {
            jotenberg.getBuilder().addTextBody("ownerPassword", options.getOwnerPassword());
        }

        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = jotenberg.getBuilder().build();
        httpPost.setEntity(requestEntity);
        return jotenberg.getClient().execute(httpPost);
    }

    /**
     * Executes an HTTP POST request for PDF Engines embed operation.
     *
     * @param files  The list of PDF files to embed files into.
     * @param embeds The list of files to embed.
     * @param route  The route for the PDF Engines operation.
     * @return A CloseableHttpResponse containing the result.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public CloseableHttpResponse getPdfEnginesHttpResponseWithEmbed(List<File> files, List<File> embeds, String route) throws IOException {
        if (files.isEmpty()) {
            throw new EmptyFileListException();
        }

        List<File> pdfFiles = files.stream().filter(CommonUtils::isPDF).toList();

        if (pdfFiles.isEmpty()) {
            throw new FileNotFoundException("No PDF file not found.");
        }

        pdfFiles.forEach(file -> jotenberg.getBuilder().addBinaryBody(file.getName(), file));
        embeds.forEach(file -> jotenberg.getBuilder().addBinaryBody("embeds", file));

        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = jotenberg.getBuilder().build();
        httpPost.setEntity(requestEntity);
        return jotenberg.getClient().execute(httpPost);
    }
}