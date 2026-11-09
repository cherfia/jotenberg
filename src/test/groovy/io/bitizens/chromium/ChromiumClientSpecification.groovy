package io.bitizens.chromium

import io.bitizens.GotenbergStub
import io.bitizens.Jotenberg
import io.bitizens.common.EmulatedMediaType
import io.bitizens.common.FileInput
import io.bitizens.common.PaperSize
import io.bitizens.pdf.ConformanceProfile
import io.bitizens.pdf.Stage
import io.bitizens.pdf.StageFile
import org.json.JSONArray
import org.json.JSONObject
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

class ChromiumClientSpecification extends Specification {
    @AutoCleanup
    GotenbergStub stub = new GotenbergStub()
    ChromiumClient chromium
    Path dir = Files.createTempDirectory('chromium')

    def setup() {
        chromium = new Jotenberg(stub.endpoint).chromium()
    }

    def cleanup() {
        dir.toFile().deleteDir()
    }

    Path file(String name, String content = name) {
        def path = dir.resolve(name)
        Files.write(path, content.bytes)
        path
    }

    def "should convert a URL with only the url field by default"() {
        when:
        chromium.convert(UrlConversion.of('https://example.com'))

        then:
        stub.last.path == '/forms/chromium/convert/url'
        stub.last.fields() == [url: 'https://example.com']
    }

    def "should refuse a URL that isn't http or https"() {
        when:
        UrlConversion.of('file:///etc/passwd')

        then:
        thrown(IllegalArgumentException)
    }

    def "should upload the HTML as index.html, with its assets"() {
        when:
        chromium.convert(HtmlConversion.of(file('page.html')).asset(file('style.css')).asset(FileInput.of('png'.bytes, 'logo.png')))

        then:
        stub.last.path == '/forms/chromium/convert/html'
        stub.last.fileNames() == ['index.html', 'style.css', 'logo.png']
        stub.last.files()[0].text == 'page.html'
    }

    def "should upload markdown files under their names"() {
        when:
        chromium.convert(MarkdownConversion.of(file('template.html'), file('one.md'), file('two.md')))

        then:
        stub.last.path == '/forms/chromium/convert/markdown'
        stub.last.fileNames() == ['index.html', 'one.md', 'two.md']
    }

    def "should refuse a markdown conversion without markdown"() {
        when:
        chromium.convert(MarkdownConversion.of(file('template.html')))

        then:
        thrown(IllegalArgumentException)
    }

    def "should send every page and Chromium field"() {
        when:
        chromium.convert(UrlConversion.of('https://example.com')
                .paperSize(PaperSize.A4)
                .paperWidth('210mm')
                .margins('1cm', '2cm', '1cm', '2cm')
                .landscape(true)
                .printBackground(true)
                .preferCssPageSize(true)
                .scale(0.8)
                .singlePage(false)
                .nativePages('1-3')
                .nativeBookmarks(true)
                .nativeTaggedPdf(true)
                .omitBackground(true)
                .userAgent('agent')
                .emulatedMediaType(EmulatedMediaType.SCREEN)
                .emulatedMediaFeature('prefers-color-scheme', 'dark')
                .waitDelay(Duration.ofMillis(1500))
                .waitForSelector('#ready')
                .waitForExpression('window.ready === true')
                .waitForWindowStatus('done')
                .extraHttpHeader('X-Token', 'abc')
                .failOnHttpStatusCodes(499, 599)
                .failOnResourceHttpStatusCodes(404)
                .ignoreResourceHttpStatusDomains('sentry-cdn.com')
                .failOnResourceLoadingFailed(true)
                .failOnConsoleExceptions(true)
                .failOnConsoleMessageTypes(ConsoleMessageType.ERROR, ConsoleMessageType.WARNING)
                .skipNetworkIdleEvent(false)
                .skipNetworkAlmostIdleEvent(false)
                .cookie(Cookie.of('session', 'abc', 'example.com').httpOnly(true).sameSite(Cookie.SameSite.Lax)))

        then:
        def fields = stub.last.fields()
        fields.paperSize == 'A4'
        fields.paperWidth == '210mm'
        [fields.marginTop, fields.marginRight, fields.marginBottom, fields.marginLeft] == ['1cm', '2cm', '1cm', '2cm']
        fields.landscape == 'true'
        fields.printBackground == 'true'
        fields.preferCssPageSize == 'true'
        fields.scale == '0.8'
        fields.singlePage == 'false'
        fields.nativePages == '1-3'
        fields.nativeBookmarks == 'true'
        fields.nativeTaggedPdf == 'true'
        fields.omitBackground == 'true'
        fields.userAgent == 'agent'
        fields.emulatedMediaType == 'screen'
        new JSONObject(fields.emulatedMediaFeatures).toMap() == ['prefers-color-scheme': 'dark']
        fields.waitDelay == '1500ms'
        fields.waitForSelector == '#ready'
        fields.waitForExpression == 'window.ready === true'
        fields.waitForWindowStatus == 'done'
        new JSONObject(fields.extraHttpHeaders).toMap() == ['X-Token': 'abc']
        new JSONArray(fields.failOnHttpStatusCodes).toList() == [499, 599]
        new JSONArray(fields.failOnResourceHttpStatusCodes).toList() == [404]
        new JSONArray(fields.ignoreResourceHttpStatusDomains).toList() == ['sentry-cdn.com']
        fields.failOnResourceLoadingFailed == 'true'
        fields.failOnConsoleExceptions == 'true'
        new JSONArray(fields.failOnConsoleMessageTypes).toList() == ['error', 'warning']
        fields.skipNetworkIdleEvent == 'false'
        fields.skipNetworkAlmostIdleEvent == 'false'
        new JSONArray(fields.cookies).toList() == [[name: 'session', value: 'abc', domain: 'example.com', httpOnly: true, sameSite: 'Lax']]
        !fields.keySet().any { it in ['nativePdfFormat', 'pdfa', 'pdfua', 'generateDocumentOutline', 'split', 'metadata'] }
    }

    def "should refuse lengths and scales Gotenberg refuses"() {
        when:
        build()

        then:
        thrown(IllegalArgumentException)

        where:
        build << [
                { UrlConversion.of('https://example.com').paperWidth('-1in') },
                { UrlConversion.of('https://example.com').margins('ten') },
                { UrlConversion.of('https://example.com').scale(3) },
                { UrlScreenshot.of('https://example.com').quality(0) }
        ]
    }

    def "should upload the header and footer under the names Chromium looks for"() {
        when:
        chromium.convert(UrlConversion.of('https://example.com').header(file('top.html')).footer(FileInput.of('f'.bytes, 'x.html')))

        then:
        stub.last.fileNames() == ['header.html', 'footer.html']
    }

    def "should send stages and the files they name"() {
        when:
        chromium.convert(HtmlConversion.of(file('page.html'))
                .asset(file('logo.png'))
                .stages(
                        Stage.stampImage(StageFile.named('logo.png')).at(40, 760).width(80),
                        Stage.attach(StageFile.of('<xml/>'.bytes, 'invoice.xml')),
                        Stage.remediate(ConformanceProfile.PDF_A_3B)))

        then:
        stub.last.fileNames() == ['index.html', 'logo.png', 'invoice.xml']
        new JSONArray(stub.last.fields().stages).toList()*.op == ['stamp-image', 'attach', 'remediate']
    }

    def "should capture screenshots without stages"() {
        when:
        chromium.screenshot(HtmlScreenshot.of(file('page.html'))
                .format(ImageFormat.JPEG)
                .quality(80)
                .width(1280)
                .height(720)
                .clip(true)
                .selector('#chart')
                .deviceScaleFactor(2)
                .optimizeForSpeed(true))

        then:
        stub.last.path == '/forms/chromium/screenshot/html'
        stub.last.fields() == [format: 'jpeg', quality: '80', width: '1280', height: '720', clip: 'true',
                               selector: '#chart', deviceScaleFactor: '2.0', optimizeForSpeed: 'true']
    }

    def "should capture URL and markdown screenshots"() {
        when:
        chromium.screenshot(UrlScreenshot.of('https://example.com'))

        then:
        stub.last.path == '/forms/chromium/screenshot/url'

        when:
        chromium.screenshot(MarkdownScreenshot.of(file('template.html'), file('notes.md')))

        then:
        stub.last.path == '/forms/chromium/screenshot/markdown'
        stub.last.fileNames() == ['index.html', 'notes.md']
    }
}
