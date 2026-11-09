package io.bitizens.core

import io.bitizens.GotenbergStub
import io.bitizens.Jotenberg
import io.bitizens.pdf.ConformanceProfile
import io.bitizens.pdf.PdfRequest
import io.bitizens.pdf.Stage
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.net.http.HttpHeaders
import java.nio.file.Files

class GotenbergResultSpecification extends Specification {
    @AutoCleanup
    GotenbergStub stub = new GotenbergStub()

    static GotenbergResult result(Map<String, String> headers, byte[] body = 'content'.bytes) {
        new GotenbergResult(body, HttpHeaders.of(headers.collectEntries { [(it.key): [it.value]] }, { a, b -> true }))
    }

    def "should read the file name Gotenberg gives the result"() {
        expect:
        GotenbergResult.filename(disposition) == Optional.ofNullable(name)

        where:
        disposition                                                                             || name
        'attachment; filename="report.pdf"'                                                     || 'report.pdf'
        'attachment; filename=report.pdf'                                                       || 'report.pdf'
        'attachment; filename="say \\"hi\\".pdf"'                                               || 'say "hi".pdf'
        'attachment; filename="facture-ete.pdf"; filename*=UTF-8\'\'facture-%C3%A9t%C3%A9.pdf' || 'facture-été.pdf'
        'attachment; filename*=UTF-8\'\'a%2Bb.pdf'                                              || 'a+b.pdf'
        'attachment'                                                                            || null
    }

    def "should describe the result from its headers"() {
        given:
        def result = result([
                'Content-Type'                     : 'application/pdf',
                'Content-Disposition'              : 'attachment; filename="report.pdf"',
                'Gotenberg-Trace'                  : 'trace-1',
                'Gotenberg-Version'                : '9.2.0',
                'Gotenberg-Remediation-Residual'   : '3',
                'Gotenberg-Remediation-Rolled-Back': '1'
        ])

        expect:
        result.contentType() == Optional.of('application/pdf')
        !result.isZip()
        result.filename() == Optional.of('report.pdf')
        result.trace() == Optional.of('trace-1')
        result.gotenbergVersion() == Optional.of('9.2.0')
        result.remediationResidual() == OptionalInt.of(3)
        result.remediationRolledBack() == OptionalInt.of(1)
        result.header('Gotenberg-Trace') == ['trace-1']
        result.text() == 'content'
    }

    def "should tell a ZIP apart, and leave absent headers empty"() {
        given:
        def result = result(['Content-Type': 'application/zip; charset=binary'])

        expect:
        result.isZip()
        result.filename().isEmpty()
        result.remediationResidual().isEmpty()
        result.header('Nope') == []
    }

    def "should not let callers change the result's bytes"() {
        given:
        def result = result([:], 'abc'.bytes)

        when:
        result.bytes()[0] = (byte) 'z'

        then:
        result.text() == 'abc'
    }

    def "should write the file to disk"() {
        given:
        def dir = Files.createTempDirectory('result')

        when:
        def path = result([:], '%PDF'.bytes).writeTo(dir.resolve('out.pdf'))

        then:
        Files.readString(path) == '%PDF'

        cleanup:
        dir.toFile().deleteDir()
    }

    def "should carry what Gotenberg answered, remediation counts included"() {
        given:
        stub.answer(200, '%PDF-1.7 remediated', [
                'Content-Type'                     : 'application/pdf',
                'Content-Disposition'              : 'attachment; filename="report.pdf"',
                'Gotenberg-Remediation-Residual'   : '0',
                'Gotenberg-Remediation-Rolled-Back': '2'
        ])
        def dir = Files.createTempDirectory('remediate')
        def report = dir.resolve('report.pdf')
        Files.write(report, '%PDF'.bytes)

        when:
        def result = new Jotenberg(stub.endpoint).pdf().remediate(PdfRequest.of(report), Stage.remediate(ConformanceProfile.PDF_A_2B))

        then:
        result.text() == '%PDF-1.7 remediated'
        result.filename() == Optional.of('report.pdf')
        result.remediationResidual() == OptionalInt.of(0)
        result.remediationRolledBack() == OptionalInt.of(2)

        cleanup:
        dir.toFile().deleteDir()
    }
}
