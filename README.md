# Jotenberg

[![build](https://github.com/cherfia/jotenberg/actions/workflows/build.yml/badge.svg)](https://github.com/cherfia/jotenberg/actions/workflows/build.yml)

A Java library that interacts with [Gotenberg](https://gotenberg.dev/)'s different modules to convert a variety of
document formats to PDF files.

## Snippets

To incorporate `jotenberg` into your project, follow the snippets below for Apache Maven and Gradle dependencies.

### Apache Maven

First, add the GitHub Packages repository to your `pom.xml`:

```xml

<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/cherfia/jotenberg</url>
    </repository>
</repositories>
```

Then add the dependency:

```xml

<dependency>
    <groupId>dev.inaka</groupId>
    <artifactId>jotenberg</artifactId>
    <version>1.1.0</version>
</dependency>
```

**Note:** You'll need to authenticate with GitHub Packages. Create a personal access token with `read:packages`
permission and add it to your `~/.m2/settings.xml`:

```xml

<settings>
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

### Gradle

First, add the GitHub Packages repository to your `build.gradle`:

```gradle
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/cherfia/jotenberg")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Then add the dependency:

```gradle
implementation group: 'dev.inaka', name: 'jotenberg', version: '1.1.0'
```

**Note:** You'll need to authenticate with GitHub Packages. Create a personal access token with `read:packages`
permission and set it as an environment variable `GITHUB_TOKEN` or as a Gradle property `gpr.token`.

## Prerequisites

Before attempting to use `Jotenberg`, be sure you install [Docker](https://www.docker.com/) if you have not already done
so.

Once the docker Daemon is up and running, you can start a default Docker container
of [Gotenberg](https://gotenberg.dev/) as follows:

```bash
docker run --rm -p 8090:8090 gotenberg/gotenberg:8 gotenberg --api-port=8090
```

After that, you can use `Jotenberg` by adding it as a dependency to your project (see Snippets section above) or
download the latest JAR library from the
GitHub [Releases](https://github.com/cherfia/jotenberg/releases) page and add it to your Java project `classpath`.

## Get Started

Create an instance of `Jotenberg` class and pass your `Gotenberg` `endpoint` url as a constructor parameter.

```java
Jotenberg client = new Jotenberg("http://localhost:8090/");
```

### Chromium

`Jotenberg` client comes with a `convert` method that calls one of
Chromium's [routes](https://gotenberg.dev/docs/modules/chromium#routes) to convert `html` and `markdown` files, or
a `url` to a `CloseableHttpResponse` that contains the `HttpEntity` which holds the content of the converted PDF file.

`convert` expects three parameters; the first parameter represents what will be converted (i.e. `url`, `html`,
or `markdown` files), the second one is a `ChromiumPageProperties` parameter, and the third is a `ChromiumOptions`
parameter.

#### ChromiumOptions

The `ChromiumOptions` class supports the following properties:

- `header` - Header HTML file
- `footer` - Footer HTML file
- `emulatedMediaType` - Emulated media type (screen or print)
- `waitDelay` - Duration to wait when loading an HTML document before conversion
- `waitForExpression` - JavaScript expression to wait before converting
- `extraHttpHeaders` - Additional HTTP headers as JSON object
- `failOnHttpStatusCodes` - HTTP status codes to fail on
- `failOnResourceHttpStatusCodes` - HTTP status codes to fail on for resources
- `failOnResourceLoadingFailed` - Whether to fail on resource loading failed
- `failOnConsoleExceptions` - Whether to fail on console exceptions
- `skipNetworkIdleEvent` - Whether to skip network idle event
- `generateDocumentOutline` - Whether to generate document outline
- `cookies` - Cookies to be written (as JSON string)
- `downloadFrom` - Download a file from a URL (as JSON string)
- `metadata` - Metadata to be written (as JSON string)
- `split` - Split the PDF into multiple files (as JSON string)
- `userPassword` - Password for opening the resulting PDF(s)
- `ownerPassword` - Password for full access on the resulting PDF(s)
- `embeds` - Files to embed in the generated PDF

#### URL

```java
CloseableHttpResponse response = client.convert("https://gotenberg.dev/", pageProperties, options);
```

#### HTML

The only requirement is that the file name should be `index.html`.

```java
File file = new File("path/to/index.html");
CloseableHttpResponse response = client.convert(file, pageProperties, options);
```

#### Markdown

This route accepts an `index.html` file plus markdown files.

```java
List<File> files = new ArrayList<>();

files.

add(new File("path/to/index.html"));
        files.

add(new File("path/to/markdown.md"));

CloseableHttpResponse response = client.convert(files, pageProperties, options);
```

#### Screenshots

`Jotenberg` client also provides a `capture` method for taking screenshots of HTML, Markdown, or URLs. The method
accepts `ImageProperties` and `ScreenshotOptions` parameters.

```java
// Screenshot a URL
ImageProperties imageProperties = new ImageProperties.Builder()
                .addFormat("png")
                .addWidth(1920)
                .addHeight(1080)
                .build();

ScreenshotOptions screenshotOptions = new ScreenshotOptions.Builder()
        .addOptimizeForSpeed(true)
        .build();

CloseableHttpResponse response = client.capture("https://gotenberg.dev/", imageProperties, screenshotOptions);
```

### LibreOffice

`Jotenberg` client provides a `convertWithLibreOffice` method which interacts
with [LibreOffice](https://gotenberg.dev/docs/modules/libreoffice) to convert different types of documents such
as `.docx`, `.epub`, `.eps`, and so on. You can find the list of all file
extensions [here](https://gotenberg.dev/docs/modules/libreoffice#route).

```java
List<File> files = new ArrayList<>();

files.

add(new File("path/to/file.docx"));
        files.

add(new File("path/to/file.xlsx"));

CloseableHttpResponse response = client.convertWithLibreOffice(files, pageProperties, options);
```

#### LibreOfficeOptions

The `LibreOfficeOptions` class supports the following properties:

- `merge` - Whether to merge the conversion result
- `pdfa` - PDF/A format
- `pdfua` - Whether to use PDF/UA
- `losslessImageCompression` - Whether to enable lossless image compression
- `reduceImageResolution` - Whether to reduce image resolution
- `quality` - Quality of the JPG export (1-100)
- `maxImageResolution` - Maximum image resolution (75, 150, 300, 600, 1200)
- `downloadFrom` - Download a file from a URL (as JSON string)
- `split` - Split the PDF into multiple files (as JSON string)
- `flatten` - Whether to flatten the PDF document
- `userPassword` - Password for opening the resulting PDF(s)
- `ownerPassword` - Password for full access on the resulting PDF(s)
- `embeds` - Files to embed in the generated PDF

#### LibreOfficePageProperties

The `LibreOfficePageProperties` class supports the following properties:

- `landscape` - Paper orientation landscape
- `nativePageRanges` - Page ranges to print
- `exportFormFields` - Export form fields or inputted content
- `singlePageSheets` - Render entire spreadsheet as single page
- `allowDuplicateFieldNames` - Allow multiple form fields with same name
- `exportBookmarks` - Export bookmarks to PDF
- `exportBookmarksToPdfDestination` - Export bookmarks as Named Destination in PDF
- `exportPlaceholders` - Export placeholder fields
- `exportNotes` - Export notes to PDF
- `exportNotesPages` - Export notes pages (Impress documents only)
- `exportOnlyNotesPages` - Export only notes pages if exportNotesPages is true
- `exportNotesInMargin` - Export notes in margin to PDF
- `convertOooTargetToPdfTarget` - Change .od[tpgs] extension to .pdf in links
- `exportLinksRelativeFsys` - Export file system hyperlinks as relative
- `exportHiddenSlides` - Export hidden slides (Impress only)
- `skipEmptyPages` - Suppress automatically inserted empty pages (Writer only)
- `addOriginalDocumentAsStream` - Insert original document as a stream in PDF
- `password` - Password to open the document

### PDF Engines

Similarly, `Jotenberg` client provides several methods which interact
with [PDF Engines](https://gotenberg.dev/docs/modules/pdf-engines) to perform various operations on PDF files.

#### Convert

Convert PDF files to a specific format (i.e. `PDF/A-1a`, `PDF/A-2b`, `PDF/A-3b`).

The supported formats can be found in `PdfFormat`.

```java
List<File> files = new ArrayList<>();
files.

add(new File("path/to/first.pdf"));
        files.

add(new File("path/to/second.pdf"));

PDFEnginesConversionOptions options = new PDFEnginesConversionOptions.Builder()
        .addPdfa(PdfFormat.A_3B)
        .addPdfua("false")
        .addDownloadFrom("{\"url\":\"https://example.com/file.pdf\"}")
        .build();

CloseableHttpResponse response = client.convertWithPdfEngines(files, options);
```

#### Merge

Merge PDF files alphabetically.

```java
List<File> files = new ArrayList<>();
files.

add(new File("path/to/first.pdf"));
        files.

add(new File("path/to/second.pdf"));

JSONObject metadata = new JSONObject();
metadata.

put("Title","Merged Document");

PDFEnginesMergeOptions options = new PDFEnginesMergeOptions.Builder()
        .addPdfa(PdfFormat.A_1A)
        .addMetadata(metadata)
        .addFlatten(true)
        .addDownloadFrom("{\"url\":\"https://example.com/file.pdf\"}")
        .build();

CloseableHttpResponse response = client.mergeWithPdfEngines(files, options);
```

#### Read Metadata

Read metadata from PDF files.

```java
List<File> files = new ArrayList<>();
files.

add(new File("path/to/document.pdf"));

CloseableHttpResponse response = client.readMetadataWithPdfEngines(files);
```

#### Write Metadata

Write metadata to PDF files.

```java
List<File> files = new ArrayList<>();
files.

add(new File("path/to/document.pdf"));

JSONObject metadata = new JSONObject();
metadata.

put("Title","My Document");
metadata.

put("Author","John Doe");

CloseableHttpResponse response = client.writeMetadataWithPdfEngines(files, metadata.toString());
```

#### Split

Split PDF files into multiple files.

```java
List<File> files = new ArrayList<>();
files.

add(new File("path/to/document.pdf"));

// Split by pages (e.g., every 2 pages)
CloseableHttpResponse response = client.splitWithPdfEngines(
        files,
        "pages",
        "2",
        true,  // unify
        false  // flatten
);

// Split by intervals (e.g., every 5 seconds)
CloseableHttpResponse response = client.splitWithPdfEngines(
        files,
        "intervals",
        "5s",
        null,  // unify (not applicable for intervals)
        false  // flatten
);
```

#### Flatten

Flatten PDF files.

```java
List<File> files = new ArrayList<>();
files.

add(new File("path/to/document.pdf"));

CloseableHttpResponse response = client.flattenWithPdfEngines(files);
```

#### Encrypt

Encrypt PDF files.

```java
List<File> files = new ArrayList<>();
files.

add(new File("path/to/document.pdf"));

PDFEnginesEncryptOptions encryptOptions = new PDFEnginesEncryptOptions.Builder()
        .addUserPassword("user123")
        .addOwnerPassword("owner123")
        .build();

CloseableHttpResponse response = client.encryptWithPdfEngines(files, encryptOptions);
```

#### Embed

Embed files into PDF files.

```java
List<File> pdfFiles = new ArrayList<>();
pdfFiles.

add(new File("path/to/document.pdf"));

List<File> embeds = new ArrayList<>();
embeds.

add(new File("path/to/attachment.txt"));
        embeds.

add(new File("path/to/image.png"));

CloseableHttpResponse response = client.embedWithPdfEngines(pdfFiles, embeds);
```

## Example

The following is a short snippet of how to use the library.

```java
import io.bitizens.Jotenberg;
import chromium.io.bitizens.ChromiumOptions;
import chromium.io.bitizens.ChromiumPageProperties;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try (var client = new Jotenberg("http://localhost:8090/")) {
            var url = "https://gotenberg.dev/";

            var pageProperties = new ChromiumPageProperties.Builder()
                    .addMarginTop(1.0f)
                    .addMarginLeft(0.5f)
                    .addMarginBottom(1.0f)
                    .addMarginRight(0.5f)
                    .addPrintBackground(true)
                    .build();

            var options = new ChromiumOptions.Builder()
                    .addFailOnConsoleExceptions(false)
                    .addSkipNetworkIdleEvent(false)
                    .build();

            var response = client.convert(url, pageProperties, options);
            var projectDir = Paths.get("").toAbsolutePath().normalize();
            var tempDir = Files.createTempDirectory(projectDir, "temp_");
            var tempFile = Files.createTempFile(tempDir, "PDF_", ".pdf").toFile();
            var pdfContent = response.getEntity().getContent();
            FileUtils.copyInputStreamToFile(pdfContent, tempFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Advanced Example with New Features

```java
import io.bitizens.Jotenberg;
import chromium.io.bitizens.ChromiumOptions;
import chromium.io.bitizens.ChromiumPageProperties;
import pdfengines.io.bitizens.PDFEnginesMergeOptions;
import common.io.bitizens.PdfFormat;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdvancedExample {
    public static void main(String[] args) {
        try (var client = new Jotenberg("http://localhost:8090/")) {
            // Convert URL with metadata and passwords
            var pageProperties = new ChromiumPageProperties.Builder()
                    .addLandscape(true)
                    .addScale(1.2f)
                    .build();

            JSONObject metadata = new JSONObject();
            metadata.put("Title", "My Document");
            metadata.put("Author", "John Doe");

            var options = new ChromiumOptions.Builder()
                    .addMetadata(metadata.toString())
                    .addUserPassword("user123")
                    .addOwnerPassword("owner123")
                    .addGenerateDocumentOutline(true)
                    .build();

            var response = client.convert("https://example.com", pageProperties, options);

            // Merge PDFs with flatten
            List<File> pdfFiles = new ArrayList<>();
            pdfFiles.add(new File("file1.pdf"));
            pdfFiles.add(new File("file2.pdf"));

            var mergeOptions = new PDFEnginesMergeOptions.Builder()
                    .addPdfa(PdfFormat.A_1A)
                    .addFlatten(true)
                    .build();

            var mergeResponse = client.mergeWithPdfEngines(pdfFiles, mergeOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

