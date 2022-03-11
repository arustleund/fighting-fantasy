package rustleund.fightingfantasy.framework.base

import kotlin.jvm.functions.Function0
import spock.lang.Specification

class AttackStrengthsSpec extends Specification {

    def diceRoller = Stub(Function0)

    def "one enemy, data driven"() {
        given:
        diceRoller.invoke() >>> [[pR], [eR]]
        def playerState = new PlayerState("player", pSk, pSt, 0, 0, 0, 0, 0, 0, [])
        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("enemy1", eSk, eSt, ePR, 2))

        expect:
        def testee = AttackStrengthsKt.createAttackStrengths(playerState, enemies, false, diceRoller)
        testee.playerHit == pHit
        testee.playerWon == pWon
        testee.enemyHasHighestAttackStrength(0) == eHiAtSt
        testee.winningEnemyPoisonDamage(2) == wEPD

        where:
        pSk | pSt | eSk | eSt | ePR | pR | eR || pHit  | pWon  | eHiAtSt | wEPD
        1   | 1   | 1   | 1   | 0   | 2  | 2  || false | false | true    | 0
        2   | 1   | 1   | 1   | 0   | 2  | 2  || false | true  | false   | 0
        1   | 1   | 1   | 1   | 0   | 3  | 2  || false | true  | false   | 0

        1   | 1   | 2   | 1   | 0   | 2  | 2  || true  | false | true    | 0
        1   | 1   | 1   | 1   | 0   | 2  | 3  || true  | false | true    | 0
        1   | 1   | 2   | 1   | 1   | 2  | 2  || true  | false | true    | 0
        1   | 1   | 1   | 1   | 1   | 2  | 3  || true  | false | true    | 0
        1   | 1   | 2   | 1   | 2   | 2  | 2  || true  | false | true    | 2
        1   | 1   | 1   | 1   | 2   | 2  | 3  || true  | false | true    | 2
    }

    def "two enemies, data driven"() {
        given:
        diceRoller.invoke() >>> [[pR], [eR1], [eR2]]
        def playerState = new PlayerState("player", pSk, pSt, 0, 0, 0, 0, 0, 0, [])
        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("enemy1", e1Sk, e1St, e1PR, 2))
        enemies.addEnemy(new EnemyState("enemy2", e2Sk, e2St, e2PR, 3))

        expect:
        def testee = AttackStrengthsKt.createAttackStrengths(playerState, enemies, tog, diceRoller)
        testee.playerHit == pHit
        testee.playerWon == pWon
        testee.enemyHasHighestAttackStrength(0) == e1HiAtSt
        if (tog) {
            testee.enemyHasHighestAttackStrength(1) == e2HiAtSt
        }
        testee.winningEnemyPoisonDamage(2) == wEPD

        where:
        pSk | pSt | e1Sk | e1St | e1PR | e2Sk | e2St | e2PR | pR | eR1 | eR2 | tog   || pHit  | pWon  | e1HiAtSt | e2HiAtSt | wEPD
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || false | false | true     | true     | 0
        2   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || false | true  | false    | false    | 0
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 3  | 2   | 2   | false || false | true  | false    | false    | 0

        1   | 1   | 2    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || true  | false | true     | true     | 0
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 3   | 2   | false || true  | false | true     | true     | 0
        1   | 1   | 2    | 1    | 1    | 1    | 1    | 0    | 2  | 2   | 2   | false || true  | false | true     | true     | 0
        1   | 1   | 1    | 1    | 1    | 1    | 1    | 0    | 2  | 3   | 2   | false || true  | false | true     | true     | 0
        1   | 1   | 2    | 1    | 2    | 1    | 1    | 0    | 2  | 2   | 2   | false || true  | false | true     | true     | 2
        1   | 1   | 1    | 1    | 2    | 1    | 1    | 0    | 2  | 3   | 2   | false || true  | false | true     | true     | 2

        1   | 1   | 1    | 1    | 0    | 2    | 1    | 0    | 2  | 2   | 2   | false || false | false | true     | true     | 0
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 3   | false || false | false | true     | true     | 0
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 1    | 2  | 2   | 2   | false || false | false | true     | true     | 0
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 1    | 2  | 2   | 3   | false || false | false | true     | true     | 0
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 2    | 2  | 2   | 2   | false || false | false | true     | true     | 0
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 2    | 2  | 2   | 3   | false || false | false | true     | true     | 0

        2   | 1   | 2    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || false | false | true     | false    | 0
        2   | 1   | 3    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || true  | false | true     | false    | 0
        2   | 1   | 1    | 1    | 0    | 2    | 1    | 0    | 2  | 2   | 2   | false || false | true  | false    | false    | 0
        2   | 1   | 1    | 1    | 0    | 3    | 1    | 0    | 2  | 2   | 2   | false || false | true  | false    | false    | 0

//      pSk | pSt | e1Sk | e1St | e1PR | e2Sk | e2St | e2PR | pR | eR1 | eR2 | tog   || pHit  | pWon  | e1HiAtSt | e2HiAtSt | wEPD
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || false | false | true     | true     | 0
        2   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || false | true  | false    | false    | 0
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 3  | 2   | 2   | true  || false | true  | false    | false    | 0

        1   | 1   | 2    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | true     | 0
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 3   | 2   | true  || true  | false | true     | true     | 0
        1   | 1   | 2    | 1    | 1    | 1    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | true     | 0
        1   | 1   | 1    | 1    | 1    | 1    | 1    | 0    | 2  | 3   | 2   | true  || true  | false | true     | true     | 0
        1   | 1   | 2    | 1    | 2    | 1    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | true     | 2
        1   | 1   | 1    | 1    | 2    | 1    | 1    | 0    | 2  | 3   | 2   | true  || true  | false | true     | true     | 2

//      pSk | pSt | e1Sk | e1St | e1PR | e2Sk | e2St | e2PR | pR | eR1 | eR2 | tog   || pHit  | pWon  | e1HiAtSt | e2HiAtSt | wEPD
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | false    | true     | 0 // 28
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 3   | true  || true  | false | false    | true     | 0 // 29
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 1    | 2  | 2   | 2   | true  || true  | false | false    | true     | 0 // 30
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 1    | 2  | 2   | 3   | true  || true  | false | false    | true     | 0 // 31
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 2    | 2  | 2   | 2   | true  || true  | false | false    | true     | 3 // 32
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 2    | 2  | 2   | 3   | true  || true  | false | false    | true     | 3 // 33

        2   | 1   | 2    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || false | false | true     | false    | 0
        2   | 1   | 3    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | false    | 0
        2   | 1   | 1    | 1    | 0    | 2    | 1    | 0    | 2  | 2   | 2   | true  || false | false | false    | false    | 0
        2   | 1   | 1    | 1    | 0    | 3    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | false    | true     | 0
        2   | 1   | 3    | 1    | 0    | 3    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | true     | 0
        2   | 1   | 4    | 1    | 0    | 3    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | false    | 0
        2   | 1   | 3    | 1    | 0    | 4    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | false    | true     | 0
    }

    def "enemy attack strength out of bounds"() {
        given:
        diceRoller.invoke() >>> [[2], [2]]
        def playerState = new PlayerState("player", 1, 1, 0, 0, 0, 0, 0, 0, [])
        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("enemy1", 1, 1, 0, 0))

        when:
        def testee = AttackStrengthsKt.createAttackStrengths(playerState, enemies, false, diceRoller)

        then:
        testee.getEnemyAttackStrength(1) == null
    }

    def "enemy highest attack strength out of bounds"() {
        given:
        diceRoller.invoke() >>> [[2], [2]]
        def playerState = new PlayerState("player", 1, 1, 0, 0, 0, 0, 0, 0, [])
        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("enemy1", 1, 1, 0, 0))

        when:
        def testee = AttackStrengthsKt.createAttackStrengths(playerState, enemies, false, diceRoller)

        then:
        !testee.enemyHasHighestAttackStrength(1)
    }
}
