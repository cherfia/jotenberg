![build](https://github.com/cherfia/jotenberg/actions/workflows/build.yml/badge.svg)
![licence](https://img.shields.io/github/license/cherfia/jotenberg?style=flat-square)

A lightweight Java library that interacts with [Gotenberg](https://gotenberg.dev/)'s different routes to convert
a variety of document formats to PDF files.

# Table of Contents

1. [Getting Started](#getting-started)
   - [Installation](#installation)
   - [Prerequisites](#prerequisites)
   - [Configuration](#configuration)
2. [Authentication](#authentication)
   - [Basic Authentication](#basic-authentication)
   - [Advanced Authentication](#advanced-authentication)
3. [Core Features](#core-features)
   - [Chromium](#chromium)
     - [URL](#url)
     - [HTML](#html)
     - [Markdown](#markdown)
     - [Screenshot](#screenshot)
   - [LibreOffice](#libreoffice)
   - [PDF Engines](#pdf-engines)
     - [Format Conversion](#format-conversion)
     - [Merging](#merging)
     - [Metadata Management](#metadata-management)
   - [PDF Splitting](#pdf-splitting)
   - [PDF Flattening](#pdf-flattening)
   - [PDF Encryption](#pdf-encryption)
   - [Embedding Files](#embedding-files)
4. [Usage Example](#usage-example)

## Getting Started

### Installation

#### Apache Maven

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
    <groupId>io.bitizens</groupId>
    <artifactId>jotenberg</artifactId>
    <version>2.0.0</version>
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

#### Gradle

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
implementation group: 'io.bitizens', name: 'jotenberg', version: '2.0.0'
```

**Note:** You'll need to authenticate with GitHub Packages. Create a personal access token with `read:packages`
permission and set it as an environment variable `GITHUB_TOKEN` or as a Gradle property `gpr.token`.

### Prerequisites

Before attempting to use Jotenberg, be sure you install [Docker](https://www.docker.com/) if you have not already done
so.

After that, you can start a default Docker container of [Gotenberg](https://gotenberg.dev/) as follows:

```bash
docker run --rm -p 3000:3000 gotenberg/gotenberg:8
```

### Configuration

Jotenberg uses constructor-based configuration. Create an instance of `Jotenberg` class and pass your Gotenberg endpoint URL as a constructor parameter:

```java
import io.bitizens.Jotenberg;

Jotenberg client = new Jotenberg("http://localhost:3000");
```

## Authentication

### Basic Authentication

Gotenberg introduces basic authentication support starting from version [8.4.0](https://github.com/gotenberg/gotenberg/releases/tag/v8.4.0). Suppose you are running a Docker container using the command below:

```bash
docker run --rm -p 3000:3000 \
-e GOTENBERG_API_BASIC_AUTH_USERNAME=user \
-e GOTENBERG_API_BASIC_AUTH_PASSWORD=pass \
gotenberg/gotenberg:8.4.0 gotenberg --api-enable-basic-auth
```

To integrate this setup with Jotenberg, you need to configure the HTTP client with basic authentication. You can extend the `Jotenberg` class or configure the underlying HTTP client to include authentication headers.

### Advanced Authentication

To implement advanced authentication or add custom HTTP headers to your requests, you can configure the underlying HTTP client used by Jotenberg. This allows you to pass additional headers, such as authentication tokens or custom metadata, with each API call.

For example, you can include a Bearer token for authentication along with a custom header by configuring the HTTP client:

```java
import io.bitizens.Jotenberg;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpRequest;

// Create a custom HTTP client with authentication
CloseableHttpClient httpClient = HttpClients.custom()
    .addInterceptorFirst((HttpRequest request) -> {
        request.addHeader("Authorization", "Bearer " + token);
        request.addHeader("X-Custom-Header", "value");
    })
    .build();

// Note: Jotenberg currently uses an internal HTTP client.
// For advanced authentication, you may need to extend the class or modify the HTTPRequestManager.
```

## Core Features

Jotenberg introduces different classes that serve as wrappers to
Gotenberg's [routes](https://gotenberg.dev/docs/routes). These classes encompass methods featuring an
input file parameter, such as `html`, `header`, `footer`, and `markdown`, capable of accepting inputs in the form of a
`File` or `List<File>`.

### Chromium

There are three different methods that come with the `Jotenberg` class (i.e. `convert`) which calls one of
Chromium's [Conversion routes](https://gotenberg.dev/docs/routes#convert-with-chromium) to convert `html` and `markdown` files, or
a `url` to a `CloseableHttpResponse` which contains the `HttpEntity` that holds the content of the converted PDF file.

Similarly, a new set of methods have been added to harness the recently introduced Gotenberg [Screenshot routes](https://gotenberg.dev/docs/routes#screenshots-route). These methods include a single method called `capture`, which allows capturing full-page screenshots of `html`, `markdown`, and `url`.

#### URL

```java
import io.bitizens.Jotenberg;
import io.bitizens.chromium.ChromiumPageProperties;
import io.bitizens.chromium.ChromiumOptions;
import org.apache.http.client.methods.CloseableHttpResponse;

Jotenberg client = new Jotenberg("http://localhost:3000");

ChromiumPageProperties pageProperties = new ChromiumPageProperties.Builder().build();
ChromiumOptions options = new ChromiumOptions.Builder().build();

CloseableHttpResponse response = client.convert(
    "https://www.example.com/",
    pageProperties,
    options
);
```

```java
import io.bitizens.Jotenberg;
import io.bitizens.screenshots.ImageProperties;
import io.bitizens.screenshots.ScreenshotOptions;
import org.apache.http.client.methods.CloseableHttpResponse;

Jotenberg client = new Jotenberg("http://localhost:3000");

ImageProperties imageProperties = new ImageProperties.Builder()
    .addFormat("png")
    .addWidth(1920)
    .addHeight(1080)
    .build();

ScreenshotOptions screenshotOptions = new ScreenshotOptions.Builder().build();

CloseableHttpResponse response = client.capture(
    "https://www.example.com/",
    imageProperties,
    screenshotOptions
);
```

#### HTML

The only requirement is that the file name should be `index.html`.

```java
import io.bitizens.Jotenberg;
import io.bitizens.chromium.ChromiumPageProperties;
import io.bitizens.chromium.ChromiumOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;

Jotenberg client = new Jotenberg("http://localhost:3000");

File file = new File("path/to/index.html");
ChromiumPageProperties pageProperties = new ChromiumPageProperties.Builder().build();
ChromiumOptions options = new ChromiumOptions.Builder().build();

CloseableHttpResponse response = client.convert(file, pageProperties, options);
```

```java
import io.bitizens.Jotenberg;
import io.bitizens.screenshots.ImageProperties;
import io.bitizens.screenshots.ScreenshotOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;

Jotenberg client = new Jotenberg("http://localhost:3000");

File file = new File("path/to/index.html");
ImageProperties imageProperties = new ImageProperties.Builder()
    .addFormat("png")
    .build();
ScreenshotOptions screenshotOptions = new ScreenshotOptions.Builder().build();

CloseableHttpResponse response = client.capture(file, imageProperties, screenshotOptions);
```

#### Markdown

This route accepts an `index.html` file plus a markdown file.

```java
import io.bitizens.Jotenberg;
import io.bitizens.chromium.ChromiumPageProperties;
import io.bitizens.chromium.ChromiumOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/index.html"));
files.add(new File("path/to/file.md"));

ChromiumPageProperties pageProperties = new ChromiumPageProperties.Builder().build();
ChromiumOptions options = new ChromiumOptions.Builder().build();

CloseableHttpResponse response = client.convert(files, pageProperties, options);
```

```java
import io.bitizens.Jotenberg;
import io.bitizens.screenshots.ImageProperties;
import io.bitizens.screenshots.ScreenshotOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/index.html"));
files.add(new File("path/to/file.md"));

ImageProperties imageProperties = new ImageProperties.Builder()
    .addFormat("png")
    .build();
ScreenshotOptions screenshotOptions = new ScreenshotOptions.Builder().build();

CloseableHttpResponse response = client.capture(files, imageProperties, screenshotOptions);
```

Each `convert()` method takes a `ChromiumPageProperties` parameter which dictates how the PDF generated
file will look like. The `ChromiumPageProperties` class supports the following properties:

- `paperWidth` - Paper width, in inches (default 8.5)
- `paperHeight` - Paper height, in inches (default 11)
- `marginTop` - Top margin, in inches (default 0.39)
- `marginBottom` - Bottom margin, in inches (default 0.39)
- `marginLeft` - Left margin, in inches (default 0.39)
- `marginRight` - Right margin, in inches (default 0.39)
- `preferCssPageSize` - Define whether to prefer page size as defined by CSS (default false)
- `printBackground` - Print the background graphics (default false)
- `omitBackground` - Hide the default white background and allow generating PDFs with transparency (default false)
- `landscape` - Set the paper orientation to landscape (default false)
- `scale` - The scale of the page rendering (default 1.0)
- `nativePageRanges` - Page ranges to print
- `singlePage` - Print the entire content in one single page (default false)
- `pdfa` - PDF format of the conversion resulting file
- `nativePdfFormat` - Native PDF format (default PDF/A-1a)

In addition to the `ChromiumPageProperties` customization options, the `convert()` method also accepts a `ChromiumOptions` parameter to further enhance the versatility of the conversion process. Here's an overview of the available options:

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

#### Screenshot

Similarly, the `capture()` method takes an `ImageProperties` parameter, influencing the appearance of the captured screenshot file.

- `format` - The image compression format, either "png", "jpeg" or "webp" (default "png")
- `quality` - The compression quality from range 0 to 100 (jpeg only)
- `omitBackground` - Hide the default white background and allow generating screenshots with transparency (default false)
- `width` - The device screen width in pixels (default 800)
- `height` - The device screen height in pixels (default 600)
- `clip` - Define whether to clip the screenshot according to the device dimensions (default false)

Furthermore, alongside the customization options offered by `ImageProperties`, the `capture()` method accommodates a `ScreenshotOptions` parameter to expand the versatility of the screenshot process. Below is a comprehensive overview of all options available:

- `header` - Header HTML file
- `footer` - Footer HTML file
- `emulatedMediaType` - Emulated media type (screen or print)
- `waitDelay` - Duration (e.g, '5s') to wait when loading an HTML document before conversion
- `waitForExpression` - JavaScript's expression to wait before converting an HTML document into PDF until it returns true
- `extraHttpHeaders` - Additional HTTP headers as JSON object
- `failOnHttpStatusCodes` - Return a 409 Conflict response if the HTTP status code is in the list (default [499,599])
- `failOnConsoleExceptions` - Return a 409 Conflict response if there are exceptions in the Chromium console (default false)
- `skipNetworkIdleEvent` - Do not wait for Chromium network to be idle (default true)
- `optimizeForSpeed` - Define whether to optimize image encoding for speed, not for resulting size
- `cookies` - Cookies to be written (as JSON string)
- `downloadFrom` - Download the file from a specific URL. It must return a Content-Disposition header with a filename parameter
- `userPassword` - Password for opening the resulting PDF(s)
- `ownerPassword` - Password for full access on the resulting PDF(s)
- `embeds` - Files to embed in the generated PDF

### LibreOffice

The `Jotenberg` class comes with a `convertWithLibreOffice` method. This method interacts with [LibreOffice](https://gotenberg.dev/docs/routes#convert-with-libreoffice) route to convert different documents to PDF files. You can find the file extensions
accepted [here](https://gotenberg.dev/docs/routes#convert-with-libreoffice).

```java
import io.bitizens.Jotenberg;
import io.bitizens.libreoffice.LibreOfficePageProperties;
import io.bitizens.libreoffice.LibreOfficeOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/file.docx"));
files.add(new File("path/to/file.png"));

LibreOfficePageProperties pageProperties = new LibreOfficePageProperties.Builder().build();
LibreOfficeOptions options = new LibreOfficeOptions.Builder().build();

CloseableHttpResponse response = client.convertWithLibreOffice(files, pageProperties, options);
```

Similarly to Chromium's route `convert` method, this method takes the following optional parameters:

- `pageProperties`: changes how the PDF generated file will look like. It also includes a `password` parameter to open the source file.
- `options`: includes:
  - `merge` - merges all the resulting files from the conversion into an individual PDF file
  - `pdfa` - PDF format of the conversion resulting file (i.e. `PDF/A-1a`, `PDF/A-2b`, `PDF/A-3b`)
  - `pdfUA` - enables PDF for Universal Access for optimal accessibility
  - `metadata` - writes metadata to the generated PDF file
  - `losslessImageCompression` - allows turning lossless compression on or off to tweak image conversion performance
  - `reduceImageResolution` - allows turning on or off image resolution reduction to tweak image conversion performance
  - `quality` - specifies the quality of the JPG export. The value ranges from 1 to 100, with higher values producing higher-quality images and larger file sizes
  - `maxImageResolution` - specifies if all images will be reduced to the specified DPI value. Possible values are: `75`, `150`, `300`, `600`, and `1200`
  - `flatten` - a boolean that, when set to true, flattens the split PDF files, making form fields and annotations uneditable
  - `userPassword` - password for opening the resulting PDF(s)
  - `ownerPassword` - password for full access on the resulting PDF(s)
  - `embeds` - files to embed in the generated PDF (repeatable). This feature enables the creation of PDFs compatible with standards like [ZUGFeRD / Factur-X](https://fnfe-mpe.org/factur-x/), which require embedding XML invoices and other files within the PDF

### PDF Engines

The `Jotenberg` class interacts with Gotenberg's [PDF Engines](https://gotenberg.dev/docs/routes#convert-into-pdfa--pdfua-route) routes to manipulate PDF files.

#### Format Conversion

This method interacts with [PDF Engines](https://gotenberg.dev/docs/routes#convert-into-pdfa--pdfua-route) conversion route to transform PDF files into the requested PDF/A format and/or PDF/UA.

```java
import io.bitizens.Jotenberg;
import io.bitizens.pdfengines.PDFEnginesConversionOptions;
import io.bitizens.common.PdfFormat;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/file_1.pdf"));
files.add(new File("path/to/file_2.pdf"));

PDFEnginesConversionOptions options = new PDFEnginesConversionOptions.Builder()
    .addPdfa(PdfFormat.A_2B)
    .addPdfua(true)
    .build();

CloseableHttpResponse response = client.convertWithPdfEngines(files, options);
```

#### Merging

This method interacts with [PDF Engines](https://gotenberg.dev/docs/routes#merge-pdfs-route) merge route which gathers different
engines that can manipulate and merge PDF files such
as: [PDFtk](https://gitlab.com/pdftk-java/pdftk), [PDFcpu](https://github.com/pdfcpu/pdfcpu), [QPDF](https://github.com/qpdf/qpdf),
and [UNO](https://github.com/unoconv/unoconv).

```java
import io.bitizens.Jotenberg;
import io.bitizens.pdfengines.PDFEnginesMergeOptions;
import io.bitizens.common.PdfFormat;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/file_1.pdf"));
files.add(new File("path/to/file_2.pdf"));

JSONObject metadata = new JSONObject();
metadata.put("Title", "Merged Document");

PDFEnginesMergeOptions options = new PDFEnginesMergeOptions.Builder()
    .addPdfa(PdfFormat.A_2B)
    .addPdfua(true)
    .addMetadata(metadata)
    .addFlatten(true)
    .build();

CloseableHttpResponse response = client.mergeWithPdfEngines(files, options);
```

#### Metadata Management

##### readMetadata

This method reads metadata from the provided PDF files.

```java
import io.bitizens.Jotenberg;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/file_1.pdf"));
files.add(new File("path/to/file_2.pdf"));

CloseableHttpResponse response = client.readMetadataWithPdfEngines(files);
```

##### writeMetadata

This method writes metadata to the provided PDF files.

```java
import io.bitizens.Jotenberg;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/file_1.pdf"));
files.add(new File("path/to/file_2.pdf"));

JSONObject metadata = new JSONObject();
metadata.put("Author", "Taha Cherfia");
metadata.put("Title", "Jotenberg");
metadata.put("Keywords", new String[]{"pdf", "html", "gotenberg"});

CloseableHttpResponse response = client.writeMetadataWithPdfEngines(files, metadata.toString());
```

Please consider referring to [ExifTool](https://exiftool.org/TagNames/XMP.html#pdf) for a comprehensive list of accessible metadata options.

### PDF Splitting

Each [Chromium](#chromium) and [LibreOffice](#libreoffice) route has a `split` parameter that allows splitting the PDF file into multiple files. The `split` parameter is passed as a JSON string in the options with the following properties:

- `mode`: the mode of the split. It can be `pages` or `intervals`
- `span`: the span of the split. It is a string that represents the range of pages to split
- `unify`: a boolean that allows unifying the split files. Only works when `mode` is `pages`
- `flatten`: a boolean that, when set to true, flattens the split PDF files, making form fields and annotations uneditable

```java
import io.bitizens.Jotenberg;
import io.bitizens.chromium.ChromiumPageProperties;
import io.bitizens.chromium.ChromiumOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;

Jotenberg client = new Jotenberg("http://localhost:3000");

JSONObject split = new JSONObject();
split.put("mode", "pages");
split.put("span", "1-2");
split.put("unify", true);

ChromiumPageProperties pageProperties = new ChromiumPageProperties.Builder().build();
ChromiumOptions options = new ChromiumOptions.Builder()
    .addSplit(split.toString())
    .build();

CloseableHttpResponse response = client.convert(
    "https://www.example.com/",
    pageProperties,
    options
);
```

On the other hand, PDFEngines' has a `splitWithPdfEngines` method that interacts with [PDF Engines](https://gotenberg.dev/docs/routes#split-pdfs-route) split route which splits PDF files into multiple files.

```java
import io.bitizens.Jotenberg;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/file_1.pdf"));
files.add(new File("path/to/file_2.pdf"));

CloseableHttpResponse response = client.splitWithPdfEngines(
    files,
    "pages",  // split mode
    "1-2",    // split span
    true,     // unify
    false     // flatten
);
```

> ⚠️ **Note**: Gotenberg does not currently validate the `span` value when `mode` is set to `pages`, as the validation depends on the chosen engine for the split feature. See [PDF Engines module configuration](https://gotenberg.dev/docs/configuration#pdf-engines) for more details.

### PDF Flattening

PDF flattening converts interactive elements like forms and annotations into a static PDF. This ensures the document looks the same everywhere and prevents further edits.

```java
import io.bitizens.Jotenberg;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/file_1.pdf"));
files.add(new File("path/to/file_2.pdf"));

CloseableHttpResponse response = client.flattenWithPdfEngines(files);
```

### PDF Encryption

Each [Chromium](#chromium) and [LibreOffice](#libreoffice) route supports PDF encryption through the `userPassword` and `ownerPassword` parameters in their respective options. The `userPassword` is required to open the PDF, while the `ownerPassword` provides full access permissions.

```java
import io.bitizens.Jotenberg;
import io.bitizens.chromium.ChromiumPageProperties;
import io.bitizens.chromium.ChromiumOptions;
import org.apache.http.client.methods.CloseableHttpResponse;

Jotenberg client = new Jotenberg("http://localhost:3000");

ChromiumPageProperties pageProperties = new ChromiumPageProperties.Builder().build();
ChromiumOptions options = new ChromiumOptions.Builder()
    .addUserPassword("my_user_password")
    .addOwnerPassword("my_owner_password")
    .build();

CloseableHttpResponse response = client.convert(
    "https://www.example.com/",
    pageProperties,
    options
);
```

Alternatively, you can use the `encryptWithPdfEngines` method:

```java
import io.bitizens.Jotenberg;
import io.bitizens.pdfengines.PDFEnginesEncryptOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> files = new ArrayList<>();
files.add(new File("path/to/document.pdf"));

PDFEnginesEncryptOptions encryptOptions = new PDFEnginesEncryptOptions.Builder()
    .addUserPassword("user123")
    .addOwnerPassword("owner123")
    .build();

CloseableHttpResponse response = client.encryptWithPdfEngines(files, encryptOptions);
```

### Embedding Files

Each [Chromium](#chromium) and [LibreOffice](#libreoffice) route supports embedding files into the generated PDF through the `embeds` parameter in their respective options. This feature enables the creation of PDFs compatible with standards like [ZUGFeRD / Factur-X](https://fnfe-mpe.org/factur-x/), which require embedding XML invoices and other files within the PDF.

You can embed multiple files by passing a list of files:

```java
import io.bitizens.Jotenberg;
import io.bitizens.chromium.ChromiumPageProperties;
import io.bitizens.chromium.ChromiumOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> embeds = new ArrayList<>();
embeds.add(new File("path/to/invoice.xml"));
embeds.add(new File("path/to/logo.png"));

ChromiumPageProperties pageProperties = new ChromiumPageProperties.Builder().build();
ChromiumOptions options = new ChromiumOptions.Builder()
    .addEmbeds(embeds)
    .build();

File htmlFile = new File("path/to/index.html");
CloseableHttpResponse response = client.convert(htmlFile, pageProperties, options);
```

All embedded files will be attached to the generated PDF and can be extracted using PDF readers that support file attachments.

Alternatively, you can use the `embedWithPdfEngines` method:

```java
import io.bitizens.Jotenberg;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

Jotenberg client = new Jotenberg("http://localhost:3000");

List<File> pdfFiles = new ArrayList<>();
pdfFiles.add(new File("path/to/document.pdf"));

List<File> embeds = new ArrayList<>();
embeds.add(new File("path/to/attachment.txt"));
embeds.add(new File("path/to/image.png"));

CloseableHttpResponse response = client.embedWithPdfEngines(pdfFiles, embeds);
```

## Usage Example

The following is a short snippet of how to use the library.

```java
import io.bitizens.Jotenberg;
import io.bitizens.chromium.ChromiumPageProperties;
import io.bitizens.chromium.ChromiumOptions;
import io.bitizens.pdfengines.PDFEnginesMergeOptions;
import io.bitizens.common.PdfFormat;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (Jotenberg client = new Jotenberg("http://localhost:3000")) {
            String url = "https://gotenberg.dev/";

            ChromiumPageProperties pageProperties = new ChromiumPageProperties.Builder()
                .addSinglePage(true)
                .addPaperWidth(8.5f)
                .addPaperHeight(11f)
                .build();

            JSONObject split = new JSONObject();
            split.put("mode", "pages");
            split.put("span", "1-2");
            split.put("unify", true);

            ChromiumOptions options = new ChromiumOptions.Builder()
                .addSplit(split.toString())
                .build();

            CloseableHttpResponse response = client.convert(url, pageProperties, options);

            // Save the PDF to a file
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
