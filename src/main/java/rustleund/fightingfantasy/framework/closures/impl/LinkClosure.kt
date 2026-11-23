/*
 * Created on Jul 7, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import org.apache.commons.logging.LogFactory
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.audio.AudioFile
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.gamesave.SavedGame
import javax.xml.parsers.DocumentBuilderFactory
import rustleund.fightingfantasy.framework.base.PageState
import rustleund.fightingfantasy.gamesave.SerializableClosure
import java.nio.file.Files
import kotlin.io.path.exists

/**
 * @author rustlea
 */
class LinkClosure(
    private val pageName: String,
    private val closureLoader: ClosureLoader,
    private val battleEffectsLoader: BattleEffectsLoader
) : SerializableClosure {

    constructor(element: Element, closureLoader: ClosureLoader, battleEffectsLoader: BattleEffectsLoader) : this(
        element.getAttribute("page"),
        closureLoader,
        battleEffectsLoader
    )

    override fun execute(gameState: GameState): Boolean {
        if (gameState.playerState.isDead) {
            gameState.message = "Links do not work after the player has died"
            return false
        }

        endBattle(gameState)

        val pageState = enterPage(gameState) ?: return true
        loadAudioFile(gameState)
        gameState.showImageIfExists("$pageName.png")
        pageState.immediateCommands.forEach { it.execute(gameState) }
        gameState.isPageLoaded = false

        // Save our state once the immediate commands have run, so that restoring it doesn't run them again. Unless it's
        // the special "DEAD" page, or an immediate command already sent the player on to another page (which saved itself).
        if ("0" != pageName && gameState.pageState === pageState) {
            gameState.addGameProgress(
                SavedGame(
                    pageName,
                    gameState.playerState.deepCopy(),
                    pageState.pagetext,
                    pageState.currentImage?.fileName?.toString(),
                    pageState.narrationFiles.map { it.path.fileName.toString() }
                )
            )
        }
        return true
    }

    /**
     * Puts the player back on the page exactly as it was when [savedGame] was taken, without running the page's immediate
     * commands. The caller is responsible for restoring the player state.
     */
    fun restore(savedGame: SavedGame, gameState: GameState) {
        endBattle(gameState)

        val pageState = enterPage(gameState) ?: return
        savedGame.pageText()?.let { pageState.pagetext = it }
        savedGame.image()?.let { gameState.showImageIfExists(it) }
        savedGame.narration()?.forEachIndexed { index, fileName ->
            val narrationFile = gameState.soundsDirectory.resolve(fileName)
            if (narrationFile.exists()) {
                gameState.queueNarration(AudioFile(narrationFile, index == 0))
            }
        }
        gameState.isPageLoaded = false
        gameState.addGameProgress(savedGame)
    }

    private fun endBattle(gameState: GameState) {
        if (gameState.isBattleInProgress) gameState.battleState?.doEndBattle()
    }

    /**
     * Loads the page and makes it the current one, or returns `null` (leaving the current page alone) if it can't be loaded.
     */
    private fun enterPage(gameState: GameState): PageState? {
        val pageLocation = gameState.pagesDirectory.resolve("$pageName.xml")
        return runCatching {
            Files.newInputStream(pageLocation).use { pageIs ->
                val documentBuilderFactory = DocumentBuilderFactory.newInstance()
                documentBuilderFactory.isNamespaceAware = true
                val documentBuilder = documentBuilderFactory.newDocumentBuilder()
                documentBuilder.parse(pageIs)
            }
        }.onFailure { log.error("Unable to load page $pageName", it) }.getOrNull()?.let { targetPageDocument ->
            PageState(pageName, closureLoader, battleEffectsLoader, targetPageDocument, gameState)
                .also { gameState.pageState = it }
        }
    }

    private fun loadAudioFile(gameState: GameState) {
        val audioFileLocation = gameState.soundsDirectory.resolve("$pageName.mp3")
        if (audioFileLocation.exists()) {
            gameState.queueNarration(AudioFile(audioFileLocation, true))
        }
    }

    override fun serialize(context: JsonSerializationContext): JsonElement {
        val result = JsonObject()
        result.addProperty("page", pageName)
        return result
    }

    private companion object {
        private val log = LogFactory.getLog(LinkClosure::class.java)
    }
}
