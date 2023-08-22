package rustleund.fightingfantasy.framework.closures.impl

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
}