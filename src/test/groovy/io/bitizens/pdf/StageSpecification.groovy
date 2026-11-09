package io.bitizens.pdf

import io.bitizens.common.PaperSize
import io.bitizens.internal.Multipart
import org.json.JSONArray
import org.json.JSONObject
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class StageSpecification extends Specification {
    Path dir = Files.createTempDirectory('stages')

    def cleanup() {
        dir.toFile().deleteDir()
    }

    Path file(String name, String content = name) {
        def path = dir.resolve(name)
        Files.createDirectories(path.parent)
        Files.write(path, content.bytes)
        path
    }

    static JSONArray stagesJson(List<Stage> stages) {
        def form = new Multipart()
        StageUploads.addStagesTo(form, stages)
        // the stages field is the first part added
        new JSONArray(fieldValue(form, 'stages'))
    }

    static String fieldValue(Multipart form, String name) {
        def out = new ByteArrayOutputStream()
        def body = form.body()
        def subscriber = java.net.http.HttpResponse.BodySubscribers.ofByteArray()
        body.subscribe(new java.util.concurrent.Flow.Subscriber<java.nio.ByteBuffer>() {
            void onSubscribe(java.util.concurrent.Flow.Subscription s) { s.request(Long.MAX_VALUE) }
            void onNext(java.nio.ByteBuffer item) { byte[] b = new byte[item.remaining()]; item.get(b); out.write(b) }
            void onError(Throwable t) { throw t }
            void onComplete() {}
        })
        def text = out.toString('UTF-8')
        def matcher = text =~ /(?s)name="${name}"\r\nContent-Type: text\/plain; charset=UTF-8\r\n\r\n(.*?)\r\n--/
        matcher.find() ? matcher.group(1) : null
    }

    def "should serialize stages in order, with their options"() {
        when:
        def json = stagesJson([
                Stage.merge(),
                Stage.stampText('DRAFT').at(72, 72).fontSize(24).font(StandardFont.HELVETICA_BOLD).color('#ff0000').opacity(0.5),
                Stage.remediate(ConformanceProfile.PDF_A_2B, ConformanceProfile.PDF_UA_1).language('en-US')
        ])

        then:
        json.length() == 3
        json.getJSONObject(0).similar(new JSONObject([op: 'merge']))
        json.getJSONObject(1).similar(new JSONObject([op: 'stamp-text', text: 'DRAFT', x: 72.0, y: 72.0, fontSize: 24.0,
                                                      font: 'Helvetica-Bold', color: '#ff0000', opacity: 0.5]))
        json.getJSONObject(2).similar(new JSONObject([op: 'remediate', profile: ['PDF/A-2b', 'PDF/UA-1'], language: 'en-US']))
    }

    def "should serialize every stage under the op Gotenberg names it"() {
        expect:
        stage.op() == op

        where:
        stage                                                   || op
        Stage.merge()                                           || 'merge'
        Stage.split().every(2)                                  || 'split'
        Stage.select('1')                                       || 'select'
        Stage.delete('1')                                       || 'delete'
        Stage.insert(StageFile.named('a.pdf'))                  || 'insert'
        Stage.compose(ComposePlacement.create())                || 'compose'
        Stage.rotate(90)                                        || 'rotate'
        Stage.stampText('x')                                    || 'stamp-text'
        Stage.stampImage(StageFile.named('a.png'))              || 'stamp-image'
        Stage.stampPdf(StageFile.named('a.pdf'))                || 'stamp-pdf'
        Stage.setMetadata()                                     || 'set-metadata'
        Stage.setBookmarks()                                    || 'set-bookmarks'
        Stage.setForm()                                         || 'set-form'
        Stage.flattenForm()                                     || 'flatten-form'
        Stage.flattenTransparency()                             || 'flatten-transparency'
        Stage.convertColors(ColorTarget.CMYK)                   || 'convert-colors'
        Stage.redact(new RedactRegion(1, 0, 0, 1, 1))           || 'redact'
        Stage.autotag()                                         || 'autotag'
        Stage.attach(StageFile.named('a.xml'))                  || 'attach'
        Stage.detach('a.xml')                                   || 'detach'
        Stage.extractAttachments()                              || 'extract-attachments'
        Stage.extractText()                                     || 'extract-text'
        Stage.optimize()                                        || 'optimize'
        Stage.encrypt()                                         || 'encrypt'
        Stage.decrypt()                                         || 'decrypt'
        Stage.remediate(ConformanceProfile.PDF_A_3B)            || 'remediate'
        Stage.sign(StageFile.named('cert.p12'))                 || 'sign'
        Stage.timestamp('https://tsa.example.com')              || 'timestamp'
        Stage.render()                                          || 'render'
    }

    def "should serialize nested options"() {
        when:
        def json = stagesJson([
                Stage.split().ranges('1', '2-3'),
                Stage.setBookmarks(Bookmark.of('Summary', 1, Bookmark.of('Figures', 2))),
                Stage.setForm().field('name', 'Jane').field('colors', 'Red', 'Green'),
                Stage.encrypt().userPassword('open').permissions(EncryptPermissions.create().print(false)),
                Stage.redact(new RedactRegion(1, 72, 700, 200, 20)),
                Stage.compose(ComposePlacement.create().source('a.pdf').at(10, 20).scale(0.5)).paperSize(PaperSize.A4)
        ])

        then:
        json.getJSONObject(0).getJSONArray('ranges').toList() == ['1', '2-3']
        json.getJSONObject(1).getJSONArray('bookmarks').similar(new JSONArray([
                [title: 'Summary', pageNumber: 1, children: [[title: 'Figures', pageNumber: 2]]]]))
        json.getJSONObject(2).getJSONObject('fields').similar(new JSONObject([name: 'Jane', colors: ['Red', 'Green']]))
        json.getJSONObject(3).getJSONObject('permissions').similar(new JSONObject([print: false]))
        json.getJSONObject(4).getJSONArray('regions').similar(new JSONArray([[page: 1, x: 72.0, y: 700.0, width: 200.0, height: 20.0]]))
        json.getJSONObject(5).similar(new JSONObject([op: 'compose', paperSize: 'A4', placements: [[source: 'a.pdf', x: 10.0, y: 20.0, scale: 0.5]]]))
    }

    def "should upload the files stages name, once, and refer to them by name"() {
        given:
        def logo = file('logo.png')
        def form = new Multipart()

        when:
        StageUploads.addStagesTo(form, [
                Stage.stampImage(StageFile.of(logo)),
                Stage.stampImage(StageFile.of(logo)).pages('2'),
                Stage.attach(StageFile.of('<xml/>'.bytes, 'invoice.xml')),
                Stage.attach(StageFile.named('downloaded.csv'))
        ])

        then:
        form.fileNames() == ['logo.png', 'invoice.xml'] as Set
        def json = new JSONArray(fieldValue(form, 'stages'))
        json.getJSONObject(0).getString('image') == 'logo.png'
        json.getJSONObject(2).getString('file') == 'invoice.xml'
        json.getJSONObject(3).getString('file') == 'downloaded.csv'
    }

    def "should refuse two different stage files under one name"() {
        when:
        StageUploads.addStagesTo(new Multipart(), [
                Stage.stampImage(StageFile.of(file('a/logo.png', 'one'))),
                Stage.attach(StageFile.of(file('b/logo.png', 'two')))
        ])

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains('Two different stage files are named logo.png')
    }

    def "should send a stage's options as form fields on its own route"() {
        given:
        def form = new Multipart()

        when:
        StageUploads.addStageFieldsTo(form, Stage.encrypt().userPassword('open').permissions(EncryptPermissions.create().print(false)))

        then:
        fieldValue(form, 'userPassword') == 'open'
        new JSONObject(fieldValue(form, 'permissions')).similar(new JSONObject([print: false]))
        fieldValue(form, 'op') == null
    }

    def "should validate options Gotenberg would refuse"() {
        when:
        stagesJson([stage])

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains(message)

        where:
        stage                                                                     || message
        Stage.split()                                                             || 'exactly one of every or ranges'
        Stage.split().every(2).ranges('1')                                        || 'exactly one of every or ranges'
        Stage.encrypt()                                                           || 'userPassword, an ownerPassword'
        Stage.compose(ComposePlacement.create())                                  || 'paperSize, or both'
        Stage.render().quality(80)                                                || 'JPEG format only'
        Stage.setForm()                                                           || 'at least one field'
    }

    def "should refuse invalid values when they are set"() {
        when:
        build()

        then:
        thrown(IllegalArgumentException)

        where:
        build << [
                { Stage.rotate(45) },
                { Stage.stampText('x').opacity(1.5) },
                { Stage.render().quality(0) },
                { Stage.redact() },
                { Stage.remediate(ConformanceProfile.PDF_A_4, ConformanceProfile.PDF_UA_1) },
                { Stage.remediate(ConformanceProfile.PDF_A_2B, ConformanceProfile.PDF_UA_2) },
                { Stage.remediate(ConformanceProfile.PDF_UA_1, ConformanceProfile.PDF_UA_2) },
                { StageFile.named(' ') }
        ]
    }

    def "should accept the profile pairs Gotenberg takes"() {
        expect:
        Stage.remediate(archival, accessible).op() == 'remediate'

        where:
        archival                       | accessible
        ConformanceProfile.PDF_A_1B    | ConformanceProfile.PDF_UA_1
        ConformanceProfile.PDF_A_3U    | ConformanceProfile.PDF_UA_1
        ConformanceProfile.FACTUR_X    | ConformanceProfile.PDF_UA_1
        ConformanceProfile.PDF_A_4F    | ConformanceProfile.PDF_UA_2
    }

    def "should embed a Factur-X invoice"() {
        given:
        def form = new Multipart()

        when:
        StageUploads.addStagesTo(form, [
                Stage.remediate(ConformanceProfile.FACTUR_X).facturX(StageFile.of('<xml/>'.bytes, 'factur-x.xml'), FacturXLevel.EN_16931)
        ])

        then:
        def json = new JSONArray(fieldValue(form, 'stages')).getJSONObject(0)
        json.similar(new JSONObject([op: 'remediate', profile: 'Factur-X', xml: 'factur-x.xml', level: 'EN 16931']))
        form.fileNames() == ['factur-x.xml'] as Set
    }
}
