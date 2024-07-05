# Jotenberg

[![build](https://github.com/cherfia/jotenberg/actions/workflows/build.yml/badge.svg)](https://github.com/cherfia/jotenberg/actions/workflows/build.yml)

A Java library that interacts with [Gotenberg](https://gotenberg.dev/)'s different modules to convert a variety of
document formats to PDF files.

## Snippets

To incorporate `jotenberg` into your project, follow the snippets below for Apache Maven and Gradle dependencies.

### Apache Maven

```xml

<dependency>
    <groupId>dev.inaka</groupId>
    <artifactId>jotenberg</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Gradle

```gradle
implementation group: 'dev.inaka', name: 'jotenberg', version: '1.1.0'
```

## Prerequisites

Before attempting to use `Jotenberg`, be sure you install [Docker](https://www.docker.com/) if you have not already done
so.

Once the docker Daemon is up and running, you can start a default Docker container
of [Gotenberg](https://gotenberg.dev/) as follows:

```bash
docker run --rm -p 8090:8090 gotenberg/gotenberg:8 gotenberg --api-port=8090
```

After that, you need to download the latest `Jotenberg` JAR library from the
GitHub [Releases](https://github.com/cherfia/jotenberg/releases) page and add it to your Java project `classpath`.

## Get Started

Create an instance of `Jotenberg` class and pass your `Gotenberg` `endpoint` url as a constructor parameter.

```java
Jotenberg client = new Jotenberg("http://localhost:8090/");
```

### Chormium

`Jotenberg` client comes with a `convert` method that calls one of
Chromium's [routes](https://gotenberg.dev/docs/modules/chromium#routes) to convert `html` and `markdown` files, or
a `url` to a `CloseableHttpResponse` that contains the `HttpEntity` which holds the content of the converted PDF file.

`convert` expects three parameters; the first parameter represents what will be converted (i.e. `url`, `html`,
or `markdown` files), the second one is a `ChromiumPageProperties` parameter, and the third is a `ChromiumOptions`
parameter.

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

### Libre Office

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

### PDF Engines

Similarly, `Jotenberg` client provides a `convertWithPdfEngines` method which interacts
with [PDF Engines](https://gotenberg.dev/docs/modules/pdf-engines) to convert PDF files to a specific format (
i.e. `PDF/A-1a`, `PDF/A-2b`, `PDF/A-3b`)).

The supported formats can be found in `PdfFormat`.

```java
List<File> files = new ArrayList<>();

files.

add(new File("path/to/first.pdf"));
        files.

add(new File("path/to/second.pdf"));

PageProperties pageProperties = new PageProperties.Builder()
        .addPdfFormat(PdfFormat.A_3B.format())
        .build();

CloseableHttpResponse response = client.convertWithPdfEngines(files, pageProperties, options);
```

Additionally, you can also use `merge` method to alphabetically merge the PDF files.

```java
List<File> files = new ArrayList<>();

files.

add(new File("path/to/first.pdf"));
        files.

add(new File("path/to/second.pdf"));

CloseableHttpResponse response = client.merge(files, pageProperties, options);
```

## Example

The following is a short snippet of how to use the library.

```java
import dev.inaka.Jotenberg;
import dev.inaka.chromium.ChromiumPageProperties;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try (var client = new Jotenberg("http://localhost:80/")) {
            var url = "https://gotenberg.dev/";
            var properties = new PageProperties.Builder()
                    .addMarginTop(1.0f)
                    .addMarginLeft(0.5f)
                    .addMarginBottom(1.0f)
                    .addMarginTop(0.5f)
                    .addPrintBackground(true)
                    .build();
            var response = client.convert(url, properties);
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

