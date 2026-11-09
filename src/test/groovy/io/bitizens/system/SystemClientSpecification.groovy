package io.bitizens.system

import io.bitizens.GotenbergStub
import io.bitizens.Jotenberg
import io.bitizens.core.GotenbergException
import spock.lang.AutoCleanup
import spock.lang.Specification

class SystemClientSpecification extends Specification {
    @AutoCleanup
    GotenbergStub stub = new GotenbergStub()
    SystemClient system

    def setup() {
        system = new Jotenberg(stub.endpoint).system()
    }

    def "should read the health report"() {
        given:
        stub.answer(200, '{"status":"up","details":{"chromium":{"status":"up"},"libreoffice":{"status":"up"}}}')

        when:
        def health = system.health()

        then:
        stub.last.method == 'GET'
        stub.last.path == '/health'
        health.up()
        health.engines() == [chromium: 'up', libreoffice: 'up']
    }

    def "should report an engine that is down instead of throwing"() {
        given:
        stub.answer(503, '{"status":"down","details":{"chromium":{"status":"down"},"libreoffice":{"status":"up"}}}')

        when:
        def health = system.health()

        then:
        !health.up()
        health.engines().chromium == 'down'
    }

    def "should check health with HEAD"() {
        when:
        def up = system.isUp()

        then:
        up
        stub.last.method == 'HEAD'

        when:
        stub.answer(503, '')

        then:
        !system.isUp()
    }

    def "should report false, not throw, when Gotenberg can't be reached"() {
        given:
        def port = stub.server.address.port
        stub.close()

        expect:
        !new Jotenberg("http://127.0.0.1:${port}").system().isUp()
    }

    def "should turn a health answer that isn't JSON into a JotenbergException"() {
        given:
        stub.answer(503, 'Service Unavailable')

        when:
        system.health()

        then:
        thrown(io.bitizens.core.JotenbergException)
    }

    def "should read the version"() {
        given:
        stub.answer(200, '9.2.0\n')

        expect:
        system.version() == '9.2.0'
        stub.last.path == '/version'
    }

    def "should read the debug report, or throw when the route is off"() {
        when:
        stub.answer(200, '{"version":"9.2.0","modules":["api"]}')

        then:
        system.debug() == [version: '9.2.0', modules: ['api']]

        when:
        stub.answer(404, 'Not Found')
        system.debug()

        then:
        def e = thrown(GotenbergException)
        e.status() == 404
    }
}
