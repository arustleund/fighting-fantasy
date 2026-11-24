package rustleund.fightingfantasy.framework.base

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.impl.AdjustEnemyScaleClosure
import rustleund.fightingfantasy.framework.closures.impl.DoBattleClosure
import spock.lang.Specification

class BattleStateSpec extends Specification {

    def pageState = Stub(PageState)

    def "Start round effect ends battle"() {
        given:
        def playerState = new PlayerState("player", 12, 24, 12, 12, 0, 0, 0, 0, [])
        def gameState = new GameState()
        gameState.setPlayerState(playerState)
        gameState.setPageState(pageState)
        pageState.getGameState() >> gameState

        def enemies = new Enemies()
        def enemy = new EnemyState("one", 1, 1, null, 0, 0, [].<String> toSet(), null, null)
        enemies.addEnemy(enemy)
        def mainBattleEffects = new BattleEffects()
        def adjustEnemyScaleElement = Stub(Element)
        adjustEnemyScaleElement.getAttribute("type") >> "stamina"
        adjustEnemyScaleElement.getAttribute("amount") >> "-1"
        Closure startRound = new AdjustEnemyScaleClosure(adjustEnemyScaleElement)
        mainBattleEffects.setStartRound(startRound)
        def testee = new BattleState(pageState, 0, true, false, null, enemies, mainBattleEffects)
        pageState.getBattle(0) >> testee

        when:
        new DoBattleClosure(0).execute(gameState)

        then:
        !testee.battleIsNotOver()
        !gameState.isBattleInProgress()
    }

    def "End battle effects run when the player wins"() {
        given:
        def gameState = gameStateWithPlayerStamina(24)
        def endBattleRan = false
        def testee = battleWhereStartRoundDoes(gameState, { GameState gs -> killFirstEnemy(gs) }, { endBattleRan = true })

        when:
        new DoBattleClosure(0).execute(gameState)

        then:
        !testee.battleIsNotOver()
        endBattleRan
    }

    def "End battle effects do not run when the player dies"() {
        given:
        def gameState = gameStateWithPlayerStamina(1)
        def endBattleRan = false
        def testee = battleWhereStartRoundDoes(gameState, { GameState gs ->
            gs.playerState.stamina.adjustCurrentValueNoException(-1)
            gs.battleState.doAfterPossibleStaminaChange()
            true
        }, { endBattleRan = true })

        when:
        new DoBattleClosure(0).execute(gameState)

        then:
        gameState.playerState.isDead()
        !endBattleRan
    }

    private GameState gameStateWithPlayerStamina(int stamina) {
        def playerState = new PlayerState("player", 12, stamina, 12, 12, 0, 0, 0, 0, [])
        def gameState = new GameState()
        gameState.setPlayerState(playerState)
        gameState.setPageState(pageState)
        pageState.getGameState() >> gameState
        gameState
    }

    private BattleState battleWhereStartRoundDoes(GameState gameState, Closure startRound, Closure endBattle) {
        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("one", 1, 1, null, 0, 0, [].<String> toSet(), null, null))
        def mainBattleEffects = new BattleEffects()
        mainBattleEffects.setStartRound(startRound)
        mainBattleEffects.setEndBattle(endBattle)
        def testee = new BattleState(pageState, 0, true, false, null, enemies, mainBattleEffects)
        pageState.getBattle(0) >> testee
        testee
    }

    private static boolean killFirstEnemy(GameState gameState) {
        gameState.battleState.enemies.firstNonDeadEnemy.stamina.adjustCurrentValueNoException(-1)
        gameState.battleState.doAfterPossibleStaminaChange()
        true
    }
}
