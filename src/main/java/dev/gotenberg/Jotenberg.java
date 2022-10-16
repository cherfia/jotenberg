package dev.gotenberg;

import dev.gotenberg.common.CommonUtils;
import dev.gotenberg.common.PageProperties;
import dev.gotenberg.common.exceptions.IndexFileNotFoundExceptions;
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

public class Jotenberg {
    private static final String CHROMIUM_HTML_ROUTE = "forms/chromium/convert/html";
    private static final String CHROMIUM_MARKDOWN_ROUTE = "forms/chromium/convert/markdown";
    private static final String CHROMIUM_URL_ROUTE = "forms/chromium/convert/url";
    private static final String LIBRE_OFFICE_ROUTE = "forms/libreoffice/convert";
    private static final String PDF_ENGINES_CONVERT_ROUTE = "forms/pdfengines/convert";
    private static final String PDF_ENGINES_MERGE_ROUTE = "forms/pdfengines/merge";
    private final MultipartEntityBuilder builder;
    private final CloseableHttpClient client;
    private final String endpoint;

    public Jotenberg(String endpoint) throws MalformedURLException {
        if (!CommonUtils.isValid(endpoint)) {
            throw new MalformedURLException(endpoint + " is not a valid URL");
        }
        this.endpoint = endpoint;
        this.builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        this.client = HttpClients.createDefault();
    }


    public CloseableHttpResponse convert(String url, PageProperties pageProperties) throws IOException {
        if (!CommonUtils.isValid(url)) {
            throw new MalformedURLException(url + " is not a valid URL.");
        }
        this.builder.addTextBody("url", url);
        return executeHttpPostRequest(endpoint.concat(CHROMIUM_URL_ROUTE), pageProperties);
    }

    public CloseableHttpResponse convert(File file, PageProperties pageProperties) throws IOException {
        if (!CommonUtils.isIndexFile(file)) {
            throw new FileNotFoundException("No index.html file found.");
        }

        this.builder.addBinaryBody(file.getName(), file);

        return executeHttpPostRequest(endpoint.concat(CHROMIUM_HTML_ROUTE), pageProperties);
    }

    public CloseableHttpResponse convert(List<File> files, PageProperties pageProperties) throws IOException {
        if (CommonUtils.isEmpty(files)) {
            throw new IndexFileNotFoundExceptions();
        }

        if (!CommonUtils.containsIndexFile(files)) {
            throw new FileNotFoundException("No index.html file found.");
        }

        List<File> supportedFiles = files.stream().filter(CommonUtils::isValidMarkdownFile).toList();

        if (supportedFiles.isEmpty()) {
            throw new FileNotFoundException("Chromium's markdwon route accepts a single index.html and markdown files.");
        }

        supportedFiles.forEach(file -> this.builder.addBinaryBody(file.getName(), file));

        return executeHttpPostRequest(endpoint.concat(CHROMIUM_MARKDOWN_ROUTE), pageProperties);
    }

    public CloseableHttpResponse convertWithLibreOffice(List<File> files, PageProperties pageProperties) throws IOException {
        if (CommonUtils.isEmpty(files)) {
            throw new FileNotFoundException("Files should not be empty.");
        }

        List<File> supportedFiles = files.stream().filter(CommonUtils::isValidFile).toList();

        if (supportedFiles.isEmpty()) {
            throw new FileNotFoundException("File extensions are not supported by Libre Office. Please refer to https://gotenberg.dev/docs/modules/libreoffice for more details.");
        }

        supportedFiles.forEach(file -> this.builder.addBinaryBody(file.getName(), file));

        return executeHttpPostRequest(endpoint.concat(LIBRE_OFFICE_ROUTE), pageProperties);
    }

    public CloseableHttpResponse convertWithPdfEngines(List<File> files, PageProperties pageProperties) throws IOException {
        return getPdfEnginesHttpResponse(files, pageProperties, PDF_ENGINES_CONVERT_ROUTE);
    }

    public CloseableHttpResponse mergeWithPdfEngines(List<File> files, PageProperties pageProperties) throws IOException {
        return getPdfEnginesHttpResponse(files, pageProperties, PDF_ENGINES_MERGE_ROUTE);
    }

    private CloseableHttpResponse getPdfEnginesHttpResponse(List<File> files, PageProperties pageProperties, String pdfEnginesRoute) throws IOException {
        if (CommonUtils.isEmpty(files)) {
            throw new FileNotFoundException("Files should not be empty.");
        }

        List<File> pdfFiles = files.stream().filter(CommonUtils::isValidPdfFile).toList();

        if (pdfFiles.isEmpty()) {
            throw new FileNotFoundException("No PDF file not found.");
        }

        pdfFiles.forEach(file -> this.builder.addBinaryBody(file.getName(), file));

        return executeHttpPostRequest(endpoint.concat(pdfEnginesRoute), pageProperties);
    }

    private CloseableHttpResponse executeHttpPostRequest(String route, PageProperties pageProperties) throws IOException {
        buildPageProperties(pageProperties);
        HttpPost httpPost = new HttpPost(route);
        HttpEntity requestEntity = this.builder.build();
        httpPost.setEntity(requestEntity);
        return this.client.execute(httpPost);
    }

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

    public void close() throws IOException {
        this.client.close();
    }

}