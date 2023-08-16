package rustleund.fightingfantasy.framework.closures.impl

import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.PlayerState
import rustleund.fightingfantasy.framework.base.Scale
import spock.lang.Specification

class TestStatPredicateSpec extends Specification {

    def "Test with and without formula"() {
        given:
        def comparison = new Comparison(Operator.AT_LEAST, 4)
        def testee = new TestStatPredicate(comparison, "honor", false, formula)
        def honorScale = new Scale(null, honorValue, null, false)
        def playerState = Stub(PlayerState) {
            getHonor() >> honorScale
        }
        def gameState = Stub(GameState) {
            getPlayerState() >> playerState
        }

        expect:
        testee.test(gameState) == expectedResult

        where:
        honorValue | formula || expectedResult
        1          | "AMT+2" || false
        2          | "AMT+2" || true
        3          | "AMT+2" || true
        1          | null    || false
        3          | null    || false
        4          | null    || true
        5          | null    || true
    }
}
