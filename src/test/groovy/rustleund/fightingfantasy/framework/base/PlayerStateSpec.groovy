package rustleund.fightingfantasy.framework.base

import kotlin.jvm.functions.Function1
import spock.lang.Specification


class PlayerStateSpec extends Specification {

    def diceRoller = Mock(Function1)

    def "testLuck with integer"() {
        given:
        def testee = new PlayerState("name", 12, 24, 12, 12, 0, 0, 0, 0, [])
        diceRoller.invoke(2) >> 10

        when:
        def result = testee.testLuck(diceRoller, "2")

        then:
        result
        testee.getLuck().getCurrentValue() == 11
    }

    def "testLuck with formula"() {
        given:
        def testee = new PlayerState("name", 12, 24, 10, 12, 0, nemesis, 0, 0, [])
        diceRoller.invoke(2) >> diceRollResult

        expect:
        def result = testee.testLuck(diceRoller, "AMT+max(0,player_nemesis_currentValue-7)")
        result == expectedResult
        testee.getLuck().getCurrentValue() == 9

        where:
        nemesis | diceRollResult || expectedResult
        0       | 11             || false
        0       | 10             || true
        0       | 9              || true
        5       | 11             || false
        5       | 10             || true
        8       | 11             || false
        8       | 10             || false
        8       | 9              || true
    }
}