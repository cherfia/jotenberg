package dev.inaka.core;

import dev.inaka.Jotenberg;
import dev.inaka.chromium.ChromiumOptions;
import dev.inaka.chromium.ChromiumPageProperties;
import dev.inaka.common.CommonUtils;
import dev.inaka.common.exceptions.EmptyFileListException;
import dev.inaka.libreoffice.LibreOfficeOptions;
import dev.inaka.libreoffice.LibreOfficePageProperties;
import dev.inaka.pdfengines.PDFEnginesOptions;
import dev.inaka.screenshots.ImageProperties;
import dev.inaka.screenshots.ScreenshotOptions;
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

        jotenberg.getConversionHelper().buildPdfEngineOptions(options);
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
}