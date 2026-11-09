![build](https://github.com/cherfia/jotenberg/actions/workflows/build.yml/badge.svg)
![licence](https://img.shields.io/github/license/cherfia/jotenberg?style=flat-square)

A lightweight Java client for [Gotenberg](https://gotenberg.dev/) 9: convert URLs, HTML, Markdown and office documents
to PDF, post-process them with stages — merge, stamp, encrypt, sign, PDF/A and PDF/UA, and more — and read PDFs.

# Table of Contents

1. [Getting Started](#getting-started)
    - [Installation](#installation)
    - [Prerequisites](#prerequisites)
2. [Gotenberg Compatibility](#gotenberg-compatibility)
3. [Configuration](#configuration)
    - [Hosted API](#hosted-api)
4. [Results and errors](#results-and-errors)
5. [Chromium](#chromium)
    - [Screenshots](#screenshots)
6. [LibreOffice](#libreoffice)
7. [Stages](#stages)
    - [Files a stage needs](#files-a-stage-needs)
8. [PDF](#pdf)
    - [Read PDFs](#read-pdfs)
9. [Request options](#request-options)
    - [Webhooks](#webhooks)
10. [System](#system)
11. [Migrating from Jotenberg 2](#migrating-from-jotenberg-2)

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
    <version>3.0.0</version>
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
implementation group: 'io.bitizens', name: 'jotenberg', version: '3.0.0'
```

**Note:** You'll need to authenticate with GitHub Packages. Create a personal access token with `read:packages`
permission and set it as an environment variable `GITHUB_TOKEN` or as a Gradle property `gpr.token`.

### Prerequisites

Jotenberg needs **Java 17** or later, and a running [Gotenberg](https://gotenberg.dev/) 9, for example with
[Docker](https://www.docker.com/):

```bash
docker run --rm -p 3000:3000 gotenberg/gotenberg:9
```

## Gotenberg Compatibility

| Jotenberg | Gotenberg |
|:----------|:----------|
| `3.x`     | `9.x`     |
| `2.x`     | `8.x`     |

Gotenberg 9 renamed most fields, moved post-processing into ordered [stages](#stages), and replaced the
`/forms/pdfengines` routes with `/forms/pdf`. Its requests and Gotenberg 8's are not interchangeable: Jotenberg 3 can't
talk to Gotenberg 8, and Jotenberg 2 can't talk to Gotenberg 9.

- **On Gotenberg 8:** stay on Jotenberg 2.x. Its docs are in the
  [2.3.0 README](https://github.com/cherfia/jotenberg/blob/v2.3.0/README.md).
- **Moving to Gotenberg 9:** upgrade to Jotenberg 3 in the same deploy, then follow
  [Migrating from Jotenberg 2](#migrating-from-jotenberg-2).

## Configuration

Create a client once and reuse it: it's thread-safe.

```java
import io.bitizens.Jotenberg;

Jotenberg client = new Jotenberg("http://localhost:3000");
```

The builder sets authentication, headers sent with every request, a timeout, or your own `java.net.http.HttpClient`:

```java
Jotenberg client = Jotenberg.builder()
        .endpoint("http://localhost:3000")
        .basicAuth("user", "pass")                // Gotenberg started with --api-enable-basic-auth
        .header("X-Proxy-Token", "abc")           // e.g. for a proxy in front of Gotenberg
        .timeout(Duration.ofSeconds(60))
        .build();
```

`Jotenberg.fromEnvironment()` reads `GOTENBERG_ENDPOINT`, `GOTENBERG_API_BASIC_AUTH_USERNAME` and
`GOTENBERG_API_BASIC_AUTH_PASSWORD`, or `CHROMIUMLY_API_KEY`.

### Hosted API

With an API key from [chromiumly.dev](https://chromiumly.dev), requests go to the hosted API at
`https://api.chromiumly.dev` with an `X-Api-Key` header. No Gotenberg to run:

```java
Jotenberg client = Jotenberg.builder().apiKey("your-api-key").build();
```

## Results and errors

Every method returns the result: a `byte[]` for files, or a typed value for the routes that read. When Gotenberg answers
an error, it throws a `GotenbergException`, whose `status()`, `trace()` and `body()` say what failed:

```java
try {
    byte[] pdf = client.chromium().convert(UrlConversion.of("https://example.com"));
    Files.write(Path.of("example.pdf"), pdf);
} catch (GotenbergException e) {
    System.err.println(e.status() + " " + e.body()); // e.g. 400 Invalid form data: …
}
```

A `JotenbergException` means Gotenberg couldn't be reached. Options Gotenberg would refuse, such as a quality of 0,
throw an `IllegalArgumentException` before the request is sent.

## Chromium

`client.chromium()` prints web pages to PDF and captures them as images.

```java
// A URL
byte[] pdf = client.chromium().convert(UrlConversion.of("https://example.com"));

// An HTML page, uploaded as index.html, with the files it references
byte[] invoice = client.chromium().convert(HtmlConversion.of(Path.of("invoice.html"))
        .asset(Path.of("style.css"))
        .asset(Path.of("logo.png")));

// Markdown files, placed in an index.html with {{ toHTML "notes.md" }}
byte[] notes = client.chromium().convert(MarkdownConversion.of(Path.of("template.html"), Path.of("notes.md")));
```

Conversions take the page layout, the header and footer, waits, network checks and more:

```java
byte[] report = client.chromium().convert(UrlConversion.of("https://example.com/report")
        .paperSize(PaperSize.A4)
        .margins("1cm")
        .landscape(true)
        .printBackground(true)
        .header(Path.of("header.html"))           // uploaded as header.html
        .footer(Path.of("footer.html"))           // e.g. <span class="pageNumber"></span>
        .nativeBookmarks(true)                    // bookmarks from the headings
        .waitForSelector("#chart.ready")
        .emulatedMediaFeature("prefers-color-scheme", "dark")
        .failOnConsoleExceptions(true)
        .cookie(Cookie.of("session", "abc", "example.com")));
```

| Option                                                                   | Description                                                                 |
|:-------------------------------------------------------------------------|:----------------------------------------------------------------------------|
| `paperSize`, `paperWidth`, `paperHeight`                                 | A named size (Letter by default), or lengths such as `"210mm"`               |
| `margins`                                                                | One length, or top, right, bottom and left                                  |
| `landscape`, `scale`, `singlePage`, `preferCssPageSize`                  | Layout                                                                      |
| `printBackground`, `omitBackground`                                      | The page's own background, and transparency                                 |
| `nativePages`                                                            | Pages to print, such as `"1-5, 8"`                                          |
| `header`, `footer`                                                       | HTML printed on each page                                                   |
| `nativeBookmarks`, `nativeTaggedPdf`                                     | Bookmarks from the headings, and a tagged PDF                               |
| `waitDelay`, `waitForSelector`, `waitForExpression`, `waitForWindowStatus` | When to print                                                             |
| `emulatedMediaType`, `emulatedMediaFeature`, `userAgent`, `extraHttpHeader`, `cookie` | What the page sees                                             |
| `failOnHttpStatusCodes`, `failOnResourceHttpStatusCodes`, `ignoreResourceHttpStatusDomains`, `failOnResourceLoadingFailed`, `failOnConsoleExceptions`, `failOnConsoleMessageTypes` | Answer 409 instead of printing a broken page |
| `skipNetworkIdleEvent`, `skipNetworkAlmostIdleEvent`                     | `false` waits for the network to settle                                     |
| `stages`                                                                 | Post-processing on the printed PDF: see [Stages](#stages)                   |

### Screenshots

```java
byte[] image = client.chromium().screenshot(UrlScreenshot.of("https://example.com")
        .format(ImageFormat.JPEG)
        .quality(80)
        .width(1280)
        .height(720)
        .selector("#chart"));                     // capture one element
```

`HtmlScreenshot` and `MarkdownScreenshot` work like their conversions. Screenshots take the same waits and checks,
but not the page layout or stages.

## LibreOffice

`client.libreOffice()` converts office documents (DOCX, XLSX, PPTX and
[over a hundred formats](https://gotenberg.dev/docs/libreoffice/convert#formats)) to PDF. Each PDF is named after its
document; several documents answer a ZIP, unless a `merge` stage joins them.

```java
byte[] pdf = client.libreOffice().convert(LibreOfficeConversion.of(Path.of("annex.docx"), Path.of("report.docx"))
        .paperSize(PaperSize.A4)
        .nativePdfa(NativePdfa.PDF_A_2B)          // PDF/A at export
        .nativeWatermarkText("DRAFT")
        .initialView(InitialView.THUMBNAILS)
        .stages(Stage.merge()));
```

A field whose name starts with `native` asks LibreOffice to do the work while it exports; a [stage](#stages) does it
afterwards. In-memory documents need a name with their extension: `FileInput.of(bytes, "report.docx")`.

## Stages

A stage is one operation on a PDF: merge, stamp, sign, make PDF/A, and so on. Gotenberg runs the stages in the order you
list them, each on what the previous one produced. Chromium conversions, LibreOffice conversions and
`client.pdf().pipe()` take a list:

```java
byte[] pdf = client.chromium().convert(HtmlConversion.of(Path.of("report.html"))
        .stages(
                Stage.select("1-3"),
                Stage.stampText("DRAFT").at(72, 72).fontSize(24).color("#ff0000"),
                Stage.setMetadata().title("Quarterly report"),
                Stage.remediate(ConformanceProfile.PDF_A_2B, ConformanceProfile.PDF_UA_1)));
```

| Stage                                                      | Does                                                                   |
|:-----------------------------------------------------------|:-----------------------------------------------------------------------|
| `merge`, `split`, `select`, `delete`, `insert`, `compose`, `rotate` | Page operations                                               |
| `stampText`, `stampImage`, `stampPdf`                      | Draw text, an image, or a page of another PDF over pages               |
| `setMetadata`, `setBookmarks`                              | Write the document information, or the outline                         |
| `setForm`, `flattenForm`                                   | Fill form fields, or burn them into the pages                          |
| `flattenTransparency`, `convertColors`                     | Prepare for print                                                      |
| `redact`                                                   | Remove the content under regions from the file itself                  |
| `autotag`                                                  | Infer a structure tree for screen readers                              |
| `attach`, `detach`, `extractAttachments`                   | Embedded files                                                         |
| `extractText`, `render`                                    | The text the pages show, or images of the pages                        |
| `optimize`                                                 | Shrink the PDF without lowering image quality                          |
| `encrypt`, `decrypt`                                       | Passwords and permissions                                              |
| `remediate`                                                | Conform to PDF/A, PDF/UA or Factur-X                                   |
| `sign`, `timestamp`                                        | Digital signatures and RFC 3161 timestamps                             |

Options of type _pages_ take a [page selection](https://gotenberg.dev/docs/pdf/pipe#page-selections) such as `"1-3"`,
`"odd"` or `"!last"`. Gotenberg refuses a list that breaks its
[order rules](https://gotenberg.dev/docs/pdf/pipe#order-rules), such as `encrypt` before `stamp-text`.

### Files a stage needs

A stage that needs a file, such as an image to stamp or a certificate to sign with, takes a `StageFile`. Jotenberg
uploads it with the request and puts its name in the stage:

```java
StageFile.of(Path.of("logo.png"))                 // a file on disk, under its name
StageFile.of(xmlBytes, "factur-x.xml")            // in-memory content, under a name
StageFile.named("logo.png")                       // a file the request already uploads, such as an HTML asset
```

Two different uploads under one name throw an `IllegalArgumentException` before the request is sent.

## PDF

`client.pdf()` runs stages on PDFs you already have, and reads them. Gotenberg takes the PDFs in the alphanumeric order
of their names.

```java
PdfClient pdf = client.pdf();

// Several stages
byte[] draft = pdf.pipe(PdfRequest.of(Path.of("report.pdf")),
        Stage.select("1-2"),
        Stage.stampText("DRAFT").at(72, 72),
        Stage.optimize());

// One stage per method
byte[] merged = pdf.merge(PdfRequest.of(Path.of("1-cover.pdf"), Path.of("2-body.pdf")));
byte[] parts = pdf.split(PdfRequest.of(Path.of("report.pdf")), Stage.split().ranges("1", "2-3")); // a ZIP
byte[] locked = pdf.encrypt(PdfRequest.of(Path.of("report.pdf")),
        Stage.encrypt().userPassword("open-me").permissions(EncryptPermissions.create().print(false)));
byte[] signed = pdf.sign(PdfRequest.of(Path.of("contract.pdf")),
        Stage.sign(StageFile.of(Path.of("company.p12"))).certificatePassword("secret").pades(true));
byte[] invoice = pdf.remediate(PdfRequest.of(Path.of("invoice.pdf")),
        Stage.remediate(ConformanceProfile.FACTUR_X)
                .facturX(StageFile.of(Path.of("factur-x.xml")), FacturXLevel.EN_16931));
String text = pdf.extractText(PdfRequest.of(Path.of("report.pdf")), Stage.extractText().pages("1"));
```

`PdfRequest.of(FileInput.of(bytes, "1-cover.pdf"))` sends in-memory PDFs under the names you choose.

`remediate` answers even when violations remain; check the result with `validate`.

### Read PDFs

The read methods answer one entry per PDF, under its file name, as typed records:

```java
Map<String, PdfInfo> info = pdf.info(PdfRead.of(Path.of("report.pdf")));
int pages = info.get("report.pdf").pages();

Map<String, PdfMetadata> metadata = pdf.metadata(PdfRead.of(Path.of("report.pdf")));
Map<String, List<Bookmark>> bookmarks = pdf.bookmarks(PdfRead.of(Path.of("report.pdf")));
Map<String, List<PdfAttachment>> attachments = pdf.attachments(PdfRead.of(Path.of("invoice.pdf")));
Map<String, List<PdfFormField>> fields = pdf.form(PdfRead.of(Path.of("form.pdf")), "1");
Map<String, PdfValidation> validation = pdf.validate(PdfRead.of(Path.of("archive.pdf")).password("open-me"),
        ConformanceProfile.PDF_A_2B, ConformanceProfile.PDF_UA_1);
```

## Request options

Every request takes these besides its own files:

```java
byte[] merged = client.pdf().merge(PdfRequest.of(Path.of("cover.pdf"))
        .archive(Path.of("chapters.zip"))                           // unpacked by Gotenberg
        .downloadFrom(DownloadFrom.of("https://files.example.com/annex.pdf")
                .extraHttpHeader("Authorization", "Bearer …")
                .filename("annex.pdf"))
        .outputFilename("book")                                     // Gotenberg-Output-Filename
        .trace("request-42"));                                      // Gotenberg-Trace
```

### Webhooks

With a webhook, Gotenberg answers right away, so the method returns an empty array. It then uploads the result to the
webhook URL, and posts a `webhook.success` or `webhook.error` event to the events URL, which Gotenberg requires:

```java
client.chromium().convert(UrlConversion.of("https://example.com")
        .webhook(Webhook.of("https://example.com/upload", "https://example.com/events")
                .method(Webhook.Method.PUT)
                .extraHttpHeader("Authorization", "Bearer abc123"))
        .trace("request-42"));                    // comes back as the event's correlationId
```

The read methods take no webhook and no output filename.

## System

```java
Health health = client.system().health();         // up, and each engine's status
boolean up = client.system().isUp();              // HEAD /health
String version = client.system().version();       // e.g. 9.2.0
Map<String, Object> debug = client.system().debug(); // needs --api-enable-debug-route
```

## Migrating from Jotenberg 2

Jotenberg 3 targets Gotenberg 9 and its API is new; most changes follow
[Gotenberg's upgrade guide](https://gotenberg.dev/docs/getting-started/upgrade).

1. Move your Gotenberg image to `gotenberg/gotenberg:9`.
2. Bump Jotenberg to 3.0.0 in the same deploy: in between, every request fails.
3. Update your calls with the table below.

| Jotenberg 2                                                       | Jotenberg 3                                                                                  |
|:------------------------------------------------------------------|:---------------------------------------------------------------------------------------------|
| `client.convert(url, pageProperties, options)`                    | `client.chromium().convert(UrlConversion.of(url)…)`                                          |
| `client.convert(file, …)`, `client.convert(files, …)`             | `HtmlConversion.of(index)`, `MarkdownConversion.of(index, markdowns…)`                       |
| `client.capture(…)`                                               | `client.chromium().screenshot(UrlScreenshot.of(url)…)`, and the HTML and Markdown screenshots |
| `client.convertWithLibreOffice(files, pageProperties, options)`   | `client.libreOffice().convert(LibreOfficeConversion.of(files…)…)`                            |
| `ChromiumPageProperties`, `ChromiumOptions`, `ImageProperties`, `ScreenshotOptions`, `LibreOfficePageProperties`, `LibreOfficeOptions` | Setters on the conversion or screenshot itself |
| `CloseableHttpResponse`                                           | `byte[]`, or a typed result; errors throw `GotenbergException`                               |
| `mergeWithPdfEngines(files, options)`                             | `client.pdf().merge(PdfRequest.of(files…))`                                                  |
| `splitWithPdfEngines(files, mode, span, unify, flatten)`          | `pdf().split(…, Stage.split().every(n))` or `.ranges(…)`; `pdf().select(…)` for unify         |
| `flattenWithPdfEngines(files)`                                    | `pdf().flattenForm(…)`                                                                       |
| `convertWithPdfEngines(files, options)`                           | `pdf().remediate(…, Stage.remediate(profile))`                                               |
| `readMetadataWithPdfEngines`, `writeMetadataWithPdfEngines`       | `pdf().metadata(…)`, `pdf().setMetadata(…, Stage.setMetadata()…)`                            |
| `readBookmarksWithPdfEngines`, `writeBookmarksWithPdfEngines`     | `pdf().bookmarks(…)`, `pdf().setBookmarks(…, Stage.setBookmarks(…))`                         |
| `encryptWithPdfEngines(files, options)`                           | `pdf().encrypt(…, Stage.encrypt()…)`                                                         |
| `embedWithPdfEngines(files, embeds)`                              | `pdf().attach(…, Stage.attach(file))`, one per file                                          |
| `watermarkWithPdfEngines`, `stampWithPdfEngines`                  | `pdf().stampText`, `stampImage` or `stampPdf`                                                |
| `rotateWithPdfEngines(files, angle, pages)`                       | `pdf().rotate(…, Stage.rotate(angle).pages(pages))`                                          |
| `pdfa`, `pdfua`, `metadata`, `split`, `userPassword`, `ownerPassword`, `embeds`, `flatten` options | Stages on the conversion: `remediate`, `setMetadata`, `split`, `encrypt`, `attach`, `flattenForm` |
| `generateDocumentOutline`                                         | `nativeBookmarks`                                                                            |
| `nativePageRanges`                                                | `nativePages`                                                                                |
| LibreOffice `quality`, `losslessImageCompression`, `reduceImageResolution`, `maxImageResolution` | `nativeQuality`, `nativeLosslessImageCompression`, `nativeReduceImageResolution(dpi)` |
| LibreOffice `initialView`, `magnification`, `pageLayout` numbers  | `InitialView`, `Magnification` and `PageLayout` enums                                        |
| LibreOffice `singlePageSheets`, `exportBookmarks`                 | `singlePage`, `nativeBookmarks`                                                              |
| LibreOffice `nativeWatermarkFontHeight`, `…FontName`, `…RotateAngle` | `nativeWatermarkFontSize`, `nativeWatermarkFont`, `nativeWatermarkRotation` (degrees)      |
| `emulatedMediaFeatures` JSON array                                | `emulatedMediaFeature(name, value)`                                                          |
| `DownloadFrom` `field`                                            | Removed: name the download in a stage with `StageFile.named(…)`                              |
| `new Jotenberg(endpoint)` throwing `MalformedURLException`        | Throws `IllegalArgumentException`                                                            |

Jotenberg no longer depends on Apache HttpClient: it uses the JDK's `java.net.http`. It ships as the Java module
`io.bitizens.jotenberg`; `Cookie` moved to `io.bitizens.chromium`.

Jotenberg 2 sent the previous request's files and fields again on each later request of the same client. Jotenberg 3
sends each request on its own.
