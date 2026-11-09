package io.bitizens.libreoffice

import io.bitizens.GotenbergStub
import io.bitizens.Jotenberg
import io.bitizens.common.DownloadFrom
import io.bitizens.common.FileInput
import io.bitizens.common.PaperSize
import io.bitizens.pdf.ConformanceProfile
import io.bitizens.pdf.Stage
import org.json.JSONArray
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class LibreOfficeClientSpecification extends Specification {
    @AutoCleanup
    GotenbergStub stub = new GotenbergStub()
    LibreOfficeClient libreOffice
    Path dir = Files.createTempDirectory('libreoffice')

    def setup() {
        libreOffice = new Jotenberg(stub.endpoint).libreOffice()
    }

    def cleanup() {
        dir.toFile().deleteDir()
    }

    Path file(String name) {
        def path = dir.resolve(name)
        Files.write(path, name.bytes)
        path
    }

    def "should upload documents under their names, with no field by default"() {
        when:
        libreOffice.convert(LibreOfficeConversion.of(file('report.docx'), file('annex.xlsx')))

        then:
        stub.last.path == '/forms/libreoffice/convert'
        stub.last.fileNames() == ['report.docx', 'annex.xlsx']
        stub.last.fields() == [:]
    }

    def "should refuse documents LibreOffice doesn't convert"() {
        when:
        LibreOfficeConversion.of(FileInput.of('x'.bytes, name))

        then:
        thrown(IllegalArgumentException)

        where:
        name << ['book.epub', 'archive.rar', 'noextension']
    }

    def "should require a document, an archive or a download"() {
        when:
        libreOffice.convert(LibreOfficeConversion.of(new Path[0]))

        then:
        thrown(IllegalArgumentException)

        when:
        libreOffice.convert(LibreOfficeConversion.of(new Path[0]).downloadFrom(DownloadFrom.of('https://files.example.com/a.docx')))

        then:
        stub.last.fields().containsKey('downloadFrom')
    }

    def "should send the Gotenberg 9 fields"() {
        when:
        libreOffice.convert(LibreOfficeConversion.of(file('report.docx'))
                .password('open')
                .paperSize(PaperSize.A4)
                .landscape(true)
                .nativePages('1-3')
                .singlePage(true)
                .nativeBookmarks(false)
                .nativePdfa(NativePdfa.PDF_A_2B)
                .nativePdfua()
                .nativeTaggedPdf(true)
                .nativeQuality(80)
                .nativeLosslessImageCompression(true)
                .nativeReduceImageResolution(150)
                .nativeWatermarkText('DRAFT')
                .nativeWatermarkColor('#ff0000')
                .nativeWatermarkFontSize(48)
                .nativeWatermarkRotation(45)
                .nativeWatermarkFont('Arial')
                .initialView(InitialView.THUMBNAILS)
                .magnification(Magnification.FIT_WIDTH)
                .pageLayout(PageLayout.TWO_COLUMN_RIGHT)
                .openBookmarkLevels(-1))

        then:
        stub.last.fields() == [
                password                      : 'open',
                paperSize                     : 'A4',
                landscape                     : 'true',
                nativePages                   : '1-3',
                singlePage                    : 'true',
                nativeBookmarks               : 'false',
                nativePdfa                    : 'PDF/A-2b',
                nativePdfua                   : 'PDF/UA-1',
                nativeTaggedPdf               : 'true',
                nativeQuality                 : '80',
                nativeLosslessImageCompression: 'true',
                nativeReduceImageResolution   : 'true',
                nativeMaxImageResolution      : '150',
                nativeWatermarkText           : 'DRAFT',
                nativeWatermarkColor          : '#ff0000',
                nativeWatermarkFontSize       : '48',
                nativeWatermarkRotation       : '45.0',
                nativeWatermarkFont           : 'Arial',
                initialView                   : 'thumbnails',
                magnification                 : 'fitWidth',
                pageLayout                    : 'twoColumnRight',
                openBookmarkLevels            : '-1'
        ]
    }

    def "should keep one native watermark text"() {
        when:
        libreOffice.convert(LibreOfficeConversion.of(file('report.docx')).nativeWatermarkText('DRAFT').nativeTiledWatermarkText('COPY'))

        then:
        stub.last.fields() == [nativeTiledWatermarkText: 'COPY']
    }

    def "should refuse values Gotenberg refuses"() {
        when:
        build()

        then:
        thrown(IllegalArgumentException)

        where:
        build << [
                { LibreOfficeConversion.of(new Path[0]).nativeQuality(0) },
                { LibreOfficeConversion.of(new Path[0]).nativeReduceImageResolution(100) },
                { LibreOfficeConversion.of(new Path[0]).paperWidth('ten') },
                { LibreOfficeConversion.of(new Path[0]).paperHeight('-1in') },
                { LibreOfficeConversion.of(new Path[0]).openBookmarkLevels(0) },
                { LibreOfficeConversion.of(new Path[0]).openBookmarkLevels(11) }
        ]
    }

    def "should accept the bookmark levels Gotenberg takes"() {
        when:
        libreOffice.convert(LibreOfficeConversion.of(file('report.docx')).openBookmarkLevels(levels).paperWidth('210mm').paperHeight('297MM'))

        then:
        stub.last.fields() == [openBookmarkLevels: "${levels}".toString(), paperWidth: '210mm', paperHeight: '297mm']

        where:
        levels << [-1, 1, 10]
    }

    def "should unset an option set to null, not send the string null"() {
        when:
        libreOffice.convert(LibreOfficeConversion.of(file('report.docx')).password('open').password(null).nativeWatermarkFont(null))

        then:
        stub.last.fields() == [:]
    }

    def "should send stages to run on the exported PDFs"() {
        when:
        libreOffice.convert(LibreOfficeConversion.of(file('a.docx'), file('b.docx'))
                .stages(Stage.merge(), Stage.remediate(ConformanceProfile.PDF_A_2B)))

        then:
        new JSONArray(stub.last.fields().stages).toList() == [[op: 'merge'], [op: 'remediate', profile: 'PDF/A-2b']]
    }
}
