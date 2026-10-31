package io.github.gbolahan507.giveaway

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class SmokeSpec extends Specification {

    def "the application context loads"() {
        expect:
        true
    }
}