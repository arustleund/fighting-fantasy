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
        def enemy = new EnemyState("one", 1, 1, 0, 0)
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
}
