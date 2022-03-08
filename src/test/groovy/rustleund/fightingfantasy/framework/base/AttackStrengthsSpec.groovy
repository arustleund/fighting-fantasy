package rustleund.fightingfantasy.framework.base

import spock.lang.Specification

import java.util.function.Supplier

class AttackStrengthsSpec extends Specification {

    def diceRoller = Stub(Supplier)

    def "one enemy, data driven"() {
        given:
        diceRoller.get() >>> [pR, eR]
        def playerState = new PlayerState("player", pSk, pSt, 0, 0, 0, 0, 0, 0, [])
        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("enemy1", eSk, eSt, ePR))

        expect:
        def testee = AttackStrengthsKt.createAttackStrengths(playerState, enemies, false, diceRoller)
        testee.playerHit == pHit
        testee.playerWon == pWon
        testee.enemyHasHighestAttackStrength(0) == eHiAtSt
        testee.winningEnemyHasPoisonedWeapon(2) == wEPW

        where:
        pSk | pSt | eSk | eSt | ePR | pR | eR || pHit  | pWon  | eHiAtSt | wEPW
        1   | 1   | 1   | 1   | 0   | 2  | 2  || false | false | true    | false
        2   | 1   | 1   | 1   | 0   | 2  | 2  || false | true  | false   | false
        1   | 1   | 1   | 1   | 0   | 3  | 2  || false | true  | false   | false

        1   | 1   | 2   | 1   | 0   | 2  | 2  || true  | false | true    | false
        1   | 1   | 1   | 1   | 0   | 2  | 3  || true  | false | true    | false
        1   | 1   | 2   | 1   | 1   | 2  | 2  || true  | false | true    | false
        1   | 1   | 1   | 1   | 1   | 2  | 3  || true  | false | true    | false
        1   | 1   | 2   | 1   | 2   | 2  | 2  || true  | false | true    | true
        1   | 1   | 1   | 1   | 2   | 2  | 3  || true  | false | true    | true
    }

    def "two enemies, data driven"() {
        given:
        diceRoller.get() >>> [pR, eR1, eR2]
        def playerState = new PlayerState("player", pSk, pSt, 0, 0, 0, 0, 0, 0, [])
        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("enemy1", e1Sk, e1St, e1PR))
        enemies.addEnemy(new EnemyState("enemy2", e2Sk, e2St, e2PR))

        expect:
        def testee = AttackStrengthsKt.createAttackStrengths(playerState, enemies, tog, diceRoller)
        testee.playerHit == pHit
        testee.playerWon == pWon
        testee.enemyHasHighestAttackStrength(0) == e1HiAtSt
        if (tog) {
            testee.enemyHasHighestAttackStrength(1) == e2HiAtSt
        }
        testee.winningEnemyHasPoisonedWeapon(2) == wEPW

        where:
        pSk | pSt | e1Sk | e1St | e1PR | e2Sk | e2St | e2PR | pR | eR1 | eR2 | tog   || pHit  | pWon  | e1HiAtSt | e2HiAtSt | wEPW
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || false | false | true     | true     | false
        2   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || false | true  | false    | false    | false
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 3  | 2   | 2   | false || false | true  | false    | false    | false

        1   | 1   | 2    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || true  | false | true     | true     | false
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 3   | 2   | false || true  | false | true     | true     | false
        1   | 1   | 2    | 1    | 1    | 1    | 1    | 0    | 2  | 2   | 2   | false || true  | false | true     | true     | false
        1   | 1   | 1    | 1    | 1    | 1    | 1    | 0    | 2  | 3   | 2   | false || true  | false | true     | true     | false
        1   | 1   | 2    | 1    | 2    | 1    | 1    | 0    | 2  | 2   | 2   | false || true  | false | true     | true     | true
        1   | 1   | 1    | 1    | 2    | 1    | 1    | 0    | 2  | 3   | 2   | false || true  | false | true     | true     | true

        1   | 1   | 1    | 1    | 0    | 2    | 1    | 0    | 2  | 2   | 2   | false || false | false | true     | true     | false
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 3   | false || false | false | true     | true     | false
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 1    | 2  | 2   | 2   | false || false | false | true     | true     | false
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 1    | 2  | 2   | 3   | false || false | false | true     | true     | false
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 2    | 2  | 2   | 2   | false || false | false | true     | true     | false
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 2    | 2  | 2   | 3   | false || false | false | true     | true     | false

        2   | 1   | 2    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || false | false | true     | false    | false
        2   | 1   | 3    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | false || true  | false | true     | false    | false
        2   | 1   | 1    | 1    | 0    | 2    | 1    | 0    | 2  | 2   | 2   | false || false | true  | false    | false    | false
        2   | 1   | 1    | 1    | 0    | 3    | 1    | 0    | 2  | 2   | 2   | false || false | true  | false    | false    | false

//      pSk | pSt | e1Sk | e1St | e1PR | e2Sk | e2St | e2PR | pR | eR1 | eR2 | tog   || pHit  | pWon  | e1HiAtSt | e2HiAtSt | wEPW
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || false | false | true     | true     | false
        2   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || false | true  | false    | false    | false
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 3  | 2   | 2   | true  || false | true  | false    | false    | false

        1   | 1   | 2    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | true     | false
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 3   | 2   | true  || true  | false | true     | true     | false
        1   | 1   | 2    | 1    | 1    | 1    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | true     | false
        1   | 1   | 1    | 1    | 1    | 1    | 1    | 0    | 2  | 3   | 2   | true  || true  | false | true     | true     | false
        1   | 1   | 2    | 1    | 2    | 1    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | true     | true
        1   | 1   | 1    | 1    | 2    | 1    | 1    | 0    | 2  | 3   | 2   | true  || true  | false | true     | true     | true

//      pSk | pSt | e1Sk | e1St | e1PR | e2Sk | e2St | e2PR | pR | eR1 | eR2 | tog   || pHit  | pWon  | e1HiAtSt | e2HiAtSt | wEPW
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | false    | true     | false // 28
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 3   | true  || true  | false | false    | true     | false // 29
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 1    | 2  | 2   | 2   | true  || true  | false | false    | true     | false // 30
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 1    | 2  | 2   | 3   | true  || true  | false | false    | true     | false // 31
        1   | 1   | 1    | 1    | 0    | 2    | 1    | 2    | 2  | 2   | 2   | true  || true  | false | false    | true     | true // 32
        1   | 1   | 1    | 1    | 0    | 1    | 1    | 2    | 2  | 2   | 3   | true  || true  | false | false    | true     | true // 33

        2   | 1   | 2    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || false | false | true     | false    | false
        2   | 1   | 3    | 1    | 0    | 1    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | false    | false
        2   | 1   | 1    | 1    | 0    | 2    | 1    | 0    | 2  | 2   | 2   | true  || false | false | false    | false    | false
        2   | 1   | 1    | 1    | 0    | 3    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | false    | true     | false
        2   | 1   | 3    | 1    | 0    | 3    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | true     | false
        2   | 1   | 4    | 1    | 0    | 3    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | true     | false    | false
        2   | 1   | 3    | 1    | 0    | 4    | 1    | 0    | 2  | 2   | 2   | true  || true  | false | false    | true     | false
    }

    def "enemy attack strength out of bounds"() {
        given:
        diceRoller.get() >>> [2, 2]
        def playerState = new PlayerState("player", 1, 1, 0, 0, 0, 0, 0, 0, [])
        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("enemy1", 1, 1, 0))

        when:
        def testee = AttackStrengthsKt.createAttackStrengths(playerState, enemies, false, diceRoller)

        then:
        testee.getEnemyAttackStrength(1) == null
    }

    def "enemy highest attack strength out of bounds"() {
        given:
        diceRoller.get() >>> [2, 2]
        def playerState = new PlayerState("player", 1, 1, 0, 0, 0, 0, 0, 0, [])
        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("enemy1", 1, 1, 0))

        when:
        def testee = AttackStrengthsKt.createAttackStrengths(playerState, enemies, false, diceRoller)

        then:
        !testee.enemyHasHighestAttackStrength(1)
    }
}
