package rustleund.fightingfantasy.gamesave

import com.google.gson.JsonIOException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.BattleEffects
import rustleund.fightingfantasy.framework.base.GameController
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.PlayerState
import rustleund.fightingfantasy.framework.base.impl.DefaultBattleEffectsLoader
import rustleund.fightingfantasy.framework.base.parseElement
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.impl.AddBattleEffectsForNextBattleClosure
import rustleund.fightingfantasy.framework.closures.impl.AdjustScaleClosure
import rustleund.fightingfantasy.framework.closures.impl.ChainedClosure
import rustleund.fightingfantasy.framework.closures.impl.DefaultClosureLoader
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import strikt.assertions.single
import java.util.function.Function

/**
 * Battle effects for the player's next battle (issue #25) have to survive a save and a load.
 */
class SavedGameBattleEffectsTest {

    private val closureLoader = DefaultClosureLoader(
        mapOf<String, Function<Element, Closure>>("adjustScale" to Function { AdjustScaleClosure(it) })
    )
    private val battleEffectsLoader = DefaultBattleEffectsLoader(closureLoader)
    private val gameController = GameController(closureLoader, battleEffectsLoader, null)

    private fun newPlayer() = PlayerState("Player", 10, 20, 10, 10, 0, 0, 5, 0, emptyList())

    private fun saveAndLoad(playerState: PlayerState): PlayerState {
        val json = SavedGameGson.forSaving().toJson(SavedGame("1", playerState, "text", null, emptyList()))
        return load(json)
    }

    private fun load(json: String): PlayerState =
        SavedGameGson.forLoading(gameController, closureLoader, battleEffectsLoader)
            .fromJson(json, SavedGame::class.java)
            .playerState()

    @Test
    fun `battle effects for the next battle work after loading a saved game`() {
        val command = parseElement(
            """
            <addBattleEffectsForNextBattle xmlns="http://www.rustleund.com/fighting-fantasy/page">
                <battleEffects>
                    <startBattle>
                        <adjustScale type="skill" amount="-2"/>
                    </startBattle>
                </battleEffects>
            </addBattleEffectsForNextBattle>
            """.trimIndent()
        )
        val gameState = GameState()
        gameState.playerState = newPlayer()
        AddBattleEffectsForNextBattleClosure(command, battleEffectsLoader).execute(gameState)

        val loadedPlayer = saveAndLoad(gameState.playerState)

        val loadedEffects = loadedPlayer.nextBattleBattleEffects.single()
        expectThat(loadedEffects.endBattle).isNull()
        val startBattle = expectThat(loadedEffects.startBattle).isNotNull().isA<ChainedClosure>().subject

        val battleGameState = GameState()
        battleGameState.playerState = loadedPlayer
        startBattle.execute(battleGameState)
        expectThat(loadedPlayer.skill.currentValue).isEqualTo(8)
    }

    @Test
    fun `no battle effects for the next battle stays that way`() {
        expectThat(saveAndLoad(newPlayer()).nextBattleBattleEffects).isNull()
    }

    @Test
    fun `saves from before battle effects were saved as XML still load, without the effects`() {
        val json = SavedGameGson.forSaving().toJson(SavedGame("1", newPlayer(), "text", null, emptyList()))
            .replace("\"savedEnemyStamina\":{}", "\"savedEnemyStamina\":{},\"nextBattleBattleEffects\":[{\"startBattle\":{}}]")

        val loadedEffects = load(json).nextBattleBattleEffects

        expectThat(loadedEffects).hasSize(1)
        expectThat(loadedEffects.single().startBattle).isNull()
    }

    @Test
    fun `battle effects that weren't loaded from XML can't be saved`() {
        val playerState = newPlayer()
        playerState.nextBattleBattleEffects = listOf(BattleEffects())

        assertThrows<JsonIOException> {
            SavedGameGson.forSaving().toJson(SavedGame("1", playerState, "text", null, emptyList()))
        }
    }
}
