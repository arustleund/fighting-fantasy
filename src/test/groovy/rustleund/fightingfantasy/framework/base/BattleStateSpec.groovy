package rustleund.fightingfantasy.framework.base

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.impl.AdjustEnemyScaleClosure
import rustleund.fightingfantasy.framework.closures.impl.AdjustScaleClosure
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

    def "A playerHit effect does not handle the player's death before Test Your Luck can save them"() {
        given: "a player on 2 stamina who is always lucky, against an enemy who always hits"
        def gameState = Spy(GameState)
        def testee = enemyAlwaysHitsBattle(gameState, 2, 12)

        when:
        new DoBattleClosure(0).execute(gameState)

        then: "the hit takes them to 0, the playerHit effect runs, then the luck test saves them"
        0 * gameState.playerHasDied()
        !gameState.playerState.isDead()
        gameState.playerState.skill.currentValue == 0
        testee.battleIsNotOver()
    }

    def "A playerHit effect does not handle the player's death twice"() {
        given: "a player on 2 stamina with no luck, against an enemy who always hits"
        def gameState = Spy(GameState)
        enemyAlwaysHitsBattle(gameState, 2, 0)

        when:
        new DoBattleClosure(0).execute(gameState)

        then:
        1 * gameState.playerHasDied()
        gameState.playerState.isDead()
    }

    private BattleState enemyAlwaysHitsBattle(GameState gameState, int playerStamina, int playerLuck) {
        def playerState = new PlayerState("player", 1, playerStamina, playerLuck, 12, 0, 0, 0, 0, [])
        gameState.setPlayerState(playerState)
        gameState.setPageState(pageState)
        pageState.getGameState() >> gameState

        def enemies = new Enemies()
        enemies.addEnemy(new EnemyState("one", 30, 10, null, 0, 0, [].<String> toSet(), null, null))
        def loseSkillElement = Stub(Element)
        loseSkillElement.getAttribute("type") >> "skill"
        loseSkillElement.getAttribute("amount") >> "-1"
        def mainBattleEffects = new BattleEffects()
        mainBattleEffects.setPlayerHit(new AdjustScaleClosure(loseSkillElement))
        def testee = new BattleState(pageState, 0, true, false, null, enemies, mainBattleEffects)
        pageState.getBattle(0) >> testee
        testee
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
