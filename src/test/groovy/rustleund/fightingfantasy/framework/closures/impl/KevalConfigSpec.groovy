package rustleund.fightingfantasy.framework.closures.impl

import rustleund.fightingfantasy.framework.base.PlayerState
import spock.lang.Specification


class KevalConfigSpec extends Specification {

    def "port old formula"() {
        expect:
        KevalConfigKt.portOldFormula(oldFormula) == newFormula

        where:
        oldFormula    || newFormula
        null          || null
        "1"           || "AMT+1"
        "AMT+4"       || "AMT+4"
        "AMT+roll(3)" || "AMT+roll(3)"
    }

    def "player state vars and AMT are available to formulas"() {
        given:
        def playerState = new PlayerState("name", 12, 20, 9, 4, 0, 0, 7, 0, [])
        def keval = KevalConfigKt.withPlayerStateVars(KevalConfigKt.keval { 3 }, playerState)
                .withConstant("AMT", 5d)

        expect:
        keval.eval(formula) == expected

        where:
        formula                                    || expected
        "AMT+player_stamina_currentValue"          || 25d
        "player_skill_upperBound-player_luck_currentValue" || 3d
        "player_gold_currentValue*2+roll(1)"       || 17d
        "max(AMT, player_provisions_currentValue)" || 5d
    }

    def "formulas used by game pages evaluate as expected"() {
        given:
        def playerState = new PlayerState("name", 12, 20, 9, 4, 0, 9, 7, 73, [])
        def keval = KevalConfigKt.withPlayerStateVars(KevalConfigKt.keval { 3 }, playerState)
                .withConstant("AMT", 5d)

        expect:
        keval.eval(KevalConfigKt.portOldFormula(formula)) == expected

        where:
        formula                                                  || expected
        "2"                                                      || 7d
        "0"                                                      || 5d
        "-1"                                                     || 4d
        "AMT+roll(1)"                                            || 8d
        "AMT-(roll(1)*2)"                                        || -1d
        "AMT-(ceil(roll(1)/2)*2)"                                || 1d
        "AMT+max(0,ceil(player_time_currentValue/10)-6)"         || 7d
        "AMT+ceil(player_time_currentValue/10)-6"                || 7d
        "AMT+max(0,player_nemesis_currentValue-7)"               || 7d
        "min(roll(1)+1,6)"                                       || 4d
    }
}
