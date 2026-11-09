package io.bitizens.pdf

import io.bitizens.GotenbergStub
import io.bitizens.Jotenberg
import io.bitizens.common.DownloadFrom
import io.bitizens.common.FileInput
import io.bitizens.common.Webhook
import org.json.JSONArray
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class PdfClientSpecification extends Specification {
    @AutoCleanup
    GotenbergStub stub = new GotenbergStub()
    PdfClient pdf
    Path dir = Files.createTempDirectory('pdf')
    Path report

    def setup() {
        pdf = new Jotenberg(stub.endpoint).pdf()
        report = dir.resolve('report.pdf')
        Files.write(report, '%PDF-1.7 report'.bytes)
    }

    def cleanup() {
        dir.toFile().deleteDir()
    }

    def "should post the PDFs and the stages to the pipe route"() {
        when:
        def result = pdf.pipe(PdfRequest.of(report), Stage.select('1-2'), Stage.optimize())

        then:
        new String(result) == '%PDF-1.7 stub'
        stub.last.path == '/forms/pdf/pipe'
        stub.last.fileNames() == ['report.pdf']
        new JSONArray(stub.last.fields().stages).toList() == [[op: 'select', pages: '1-2'], [op: 'optimize']]
    }

    def "should refuse a pipe without stages"() {
        when:
        pdf.pipe(PdfRequest.of(report))

        then:
        thrown(IllegalArgumentException)
    }

    def "should post each stage to its own route, with its options as fields"() {
        when:
        call(pdf, PdfRequest.of(report))

        then:
        stub.last.path == "/forms/pdf/${route}"
        stub.last.fields().findAll { it.key != 'stages' } == fields
        !stub.last.fields().containsKey('stages')

        where:
        route                  | fields                                     | call
        'merge'                | [:]                                        | { PdfClient c, PdfRequest r -> c.merge(r) }
        'split'                | [every: '2']                               | { c, r -> c.split(r, Stage.split().every(2)) }
        'select'               | [pages: 'odd']                             | { c, r -> c.select(r, 'odd') }
        'delete'               | [pages: 'last']                            | { c, r -> c.delete(r, 'last') }
        'rotate'               | [angle: '90', pages: '1']                  | { c, r -> c.rotate(r, Stage.rotate(90).pages('1')) }
        'stamp-text'           | [text: 'DRAFT', x: '72.0', y: '72.0']      | { c, r -> c.stampText(r, Stage.stampText('DRAFT').at(72, 72)) }
        'set-metadata'         | [title: 'Report']                          | { c, r -> c.setMetadata(r, Stage.setMetadata().title('Report')) }
        'flatten-form'         | [:]                                        | { c, r -> c.flattenForm(r) }
        'flatten-transparency' | [:]                                        | { c, r -> c.flattenTransparency(r) }
        'convert-colors'       | [target: 'gray']                           | { c, r -> c.convertColors(r, ColorTarget.GRAY) }
        'autotag'              | [:]                                        | { c, r -> c.autotag(r) }
        'detach'               | [name: 'data.xml']                         | { c, r -> c.detach(r, 'data.xml') }
        'extract-attachments'  | [:]                                        | { c, r -> c.extractAttachments(r, Stage.extractAttachments()) }
        'optimize'             | [dedup: 'false']                           | { c, r -> c.optimize(r, Stage.optimize().dedup(false)) }
        'encrypt'              | [userPassword: 'open']                     | { c, r -> c.encrypt(r, Stage.encrypt().userPassword('open')) }
        'decrypt'              | [password: 'open']                         | { c, r -> c.decrypt(r, Stage.decrypt().password('open')) }
        'remediate'            | [profile: 'PDF/A-3b']                      | { c, r -> c.remediate(r, Stage.remediate(ConformanceProfile.PDF_A_3B)) }
        'timestamp'            | [timestampUrl: 'https://tsa.example.com']  | { c, r -> c.timestamp(r, 'https://tsa.example.com') }
        'render'               | [format: 'jpeg', quality: '80']            | { c, r -> c.render(r, Stage.render().format(RenderFormat.JPEG).quality(80)) }
    }

    def "should upload the files a single stage names, next to the PDFs"() {
        given:
        def cert = dir.resolve('company.p12')
        Files.write(cert, 'p12'.bytes)

        when:
        pdf.sign(PdfRequest.of(report), Stage.sign(StageFile.of(cert)).certificatePassword('secret').pades(true))

        then:
        stub.last.path == '/forms/pdf/sign'
        stub.last.fileNames() as Set == ['report.pdf', 'company.p12'] as Set
        stub.last.fields() == [certificate: 'company.p12', certificatePassword: 'secret', pades: 'true']
    }

    def "should refuse an input PDF and a stage file under one name"() {
        when:
        pdf.insert(PdfRequest.of(report), Stage.insert(StageFile.of('%PDF other'.bytes, 'report.pdf')))

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains('Two uploaded files are named report.pdf')
        stub.requests.isEmpty()
    }

    def "should name in-memory PDFs as given, and refuse non-PDF inputs"() {
        when:
        pdf.merge(PdfRequest.of(FileInput.of('%PDF a'.bytes, '1-cover.pdf'), FileInput.of('%PDF b'.bytes, '2-body.pdf')))

        then:
        stub.last.fileNames() == ['1-cover.pdf', '2-body.pdf']

        when:
        PdfRequest.of(FileInput.of('x'.bytes, 'notes.txt'))

        then:
        thrown(IllegalArgumentException)
    }

    def "should send the request options"() {
        when:
        pdf.merge(PdfRequest.of(report)
                .archive(FileInput.of('PK'.bytes, 'more.zip'))
                .downloadFrom(DownloadFrom.of('https://files.example.com/a.pdf').filename('a.pdf').extraHttpHeader('Authorization', 'Bearer t'))
                .webhook(Webhook.of('https://example.com/upload', 'https://example.com/events').method(Webhook.Method.PUT).extraHttpHeader('X-Token', 't'))
                .outputFilename('merged')
                .trace('trace-1'))

        then:
        stub.last.parts.find { it.name == 'archives' }.filename == 'more.zip'
        new JSONArray(stub.last.fields().downloadFrom).toList() ==
                [[url: 'https://files.example.com/a.pdf', filename: 'a.pdf', extraHttpHeaders: [Authorization: 'Bearer t']]]
        stub.last.header('Gotenberg-Webhook-Url') == 'https://example.com/upload'
        stub.last.header('Gotenberg-Webhook-Events-Url') == 'https://example.com/events'
        stub.last.header('Gotenberg-Webhook-Method') == 'PUT'
        stub.last.header('Gotenberg-Webhook-Extra-Http-Headers') == '{"X-Token":"t"}'
        stub.last.header('Gotenberg-Output-Filename') == 'merged'
        stub.last.header('Gotenberg-Trace') == 'trace-1'
    }

    def "should read info, keyed by file name"() {
        given:
        stub.answer(200, '{"report.pdf":{"pages":3,"version":"1.7","encrypted":true,"tagged":false,"linearized":false,"formFields":true}}')

        when:
        def info = pdf.info(PdfRead.of(report).password('open').trace('t'))

        then:
        stub.last.path == '/forms/pdf/info'
        stub.last.fields() == [password: 'open']
        stub.last.header('Gotenberg-Trace') == 't'
        info == ['report.pdf': new PdfInfo(3, '1.7', true, false, false, true)]
    }

    def "should read metadata, bookmarks and attachments"() {
        when:
        stub.answer(200, '{"report.pdf":{"title":"Report","creationDate":"D:20260101120000Z"}}')
        def metadata = pdf.metadata(PdfRead.of(report))['report.pdf']

        then:
        metadata.title() == 'Report'
        metadata.author() == null
        metadata.creationDate() == 'D:20260101120000Z'

        when:
        stub.answer(200, '{"report.pdf":[{"title":"Summary","pageNumber":1,"children":[{"title":"Figures","pageNumber":2}]}]}')
        def bookmarks = pdf.bookmarks(PdfRead.of(report))['report.pdf']

        then:
        bookmarks == [Bookmark.of('Summary', 1, Bookmark.of('Figures', 2))]

        when:
        stub.answer(200, '{"report.pdf":[{"name":"factur-x.xml","mimeType":"text/xml","relationship":"Data"},{"name":"notes.txt"}]}')
        def attachments = pdf.attachments(PdfRead.of(report))['report.pdf']

        then:
        attachments == [new PdfAttachment('factur-x.xml', 'text/xml', AttachmentRelationship.DATA),
                        new PdfAttachment('notes.txt', null, null)]
    }

    def "should read form fields"() {
        given:
        stub.answer(200, '{"report.pdf":[' +
                '{"name":"agree","type":"checkbox","value":"Off","pages":[1],"states":["Yes"]},' +
                '{"name":"colors","type":"list","value":["Red","Green"],"pages":[2],"options":["Red","Green","Blue"],"multiSelect":true,"required":true}]}')

        when:
        def fields = pdf.form(PdfRead.of(report), '1-2')['report.pdf']

        then:
        stub.last.fields() == [pages: '1-2']
        fields[0].values() == ['Off']
        fields[0].states() == ['Yes']
        fields[1].values() == ['Red', 'Green']
        fields[1].pages() == [2]
        fields[1].multiSelect()
        fields[1].required()
        fields[1].maxLength() == null
    }

    def "should validate against a profile or a pair"() {
        given:
        stub.answer(200, '{"report.pdf":{"profile":["PDF/A-2b","PDF/UA-1"],"compliant":false,"violations":[' +
                '{"specification":"ISO_19005_2","clause":"6.1.7.1","testNumber":2,"description":"d","context":"c","severity":"fatal"}]}}')

        when:
        def result = pdf.validate(PdfRead.of(report), ConformanceProfile.PDF_A_2B, ConformanceProfile.PDF_UA_1)['report.pdf']

        then:
        new JSONArray(stub.last.fields().profile).toList() == ['PDF/A-2b', 'PDF/UA-1']
        result.profiles() == [ConformanceProfile.PDF_A_2B, ConformanceProfile.PDF_UA_1]
        !result.compliant()
        result.violations()[0].isFatal()
        result.violations()[0].clause() == '6.1.7.1'

        when:
        stub.answer(200, '{"report.pdf":{"profile":"PDF/A-3b","compliant":true,"violations":[]}}')
        pdf.validate(PdfRead.of(report), ConformanceProfile.PDF_A_3B)

        then:
        stub.last.fields() == [profile: 'PDF/A-3b']
    }

    def "should return extracted text as a string"() {
        given:
        stub.answer(200, 'Page one\fPage two')

        expect:
        pdf.extractText(PdfRequest.of(report), Stage.extractText().pages('1-2')) == 'Page one\fPage two'
    }
}
