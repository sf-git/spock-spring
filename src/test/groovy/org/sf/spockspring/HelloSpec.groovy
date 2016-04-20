package org.sf.spockspring

import groovyx.net.http.RESTClient
import org.springframework.aop.target.HotSwappableTargetSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

@WebAppConfiguration
@SpringApplicationConfiguration(TestApp)
@IntegrationTest('server.port:0')

class HelloSpec extends Specification {

    @Value('${local.server.port}')
    int port

    @Autowired
    @Qualifier('swappableHelloService')
    HotSwappableTargetSource swappableHelloService

    def originalHelloService

    RESTClient client

    def setup() {
        //cannot get 'port' in setupSpec
        if (!client) {
            client = new RESTClient("http://localhost:$port", JSON)
        }
    }

    def cleanup() {
        //restore original hello service
        if (originalHelloService) {
            swappableHelloService.swap(originalHelloService)
            originalHelloService = null
        }
    }

    def "should return 'Original Bean Says: Hello, #postfix' if hello service is not mocked"() {
        when:
        def response = client.get(path: '/hello', query: [postfix: postfix])

        then:
        assert response.data.reply == "Original Bean Says: Hello, $postfix" as String

        where:
        postfix              | _
        randomAlphabetic(10) | _
    }

    def "should return 'Mocked, #postfix' if hello service is mocked"() {
        given: 'hello service is mocked'
        def mockedHelloService = Mock(HelloService)
        and: 'keep reference to the original hello service bean'
        originalHelloService = swappableHelloService.swap(mockedHelloService)

        when:
        def response = client.get(path: '/hello', query: [postfix: postfix])

        then:
        assert response.data.reply == "Mocked, $postfix" as String

        and: 'check interactions'
        interaction {
            1 * mockedHelloService.hello(postfix) >> { "Mocked, $postfix" as String }
        }

        where:
        postfix              | _
        randomAlphabetic(10) | _
    }
}
