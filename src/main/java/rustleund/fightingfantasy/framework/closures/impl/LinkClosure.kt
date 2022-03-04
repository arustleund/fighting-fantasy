/*
 * Created on Jul 7, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.gamesave.SavedGame
import javax.xml.parsers.DocumentBuilderFactory
import rustleund.fightingfantasy.framework.base.PageState
import rustleund.fightingfantasy.framework.closures.Closure
import java.nio.file.Files

/**
 * @author rustlea
 */
class LinkClosure(
    private val pageName: String,
    private val closureLoader: ClosureLoader,
    private val battleEffectsLoader: BattleEffectsLoader
) : Closure {

    constructor(element: Element, closureLoader: ClosureLoader, battleEffectsLoader: BattleEffectsLoader) : this(
        element.getAttribute("page"),
        closureLoader,
        battleEffectsLoader
    )

    override fun execute(gameState: GameState): Boolean {
        // first save our state until this point, unless it's the special "DEAD" state
        if ("0" != pageName) {
            gameState.addGameProgress(SavedGame(pageName, gameState.playerState.deepCopy()))
        }
        val pageLocation = gameState.pagesDirectory.resolve("$pageName.xml")
        runCatching {
            Files.newInputStream(pageLocation).use { pageIs ->
                val documentBuilderFactory = DocumentBuilderFactory.newInstance()
                documentBuilderFactory.isNamespaceAware = true
                val documentBuilder = documentBuilderFactory.newDocumentBuilder()
                documentBuilder.parse(pageIs)
            }
        }.onFailure { it.printStackTrace() }.getOrNull()?.let { targetPageDocument ->
            gameState.pageState =
                PageState(pageName, closureLoader, battleEffectsLoader, targetPageDocument, gameState)
            gameState.pageState.immediateCommands.forEach { it.execute(gameState) }
            gameState.isPageLoaded = false
        }
        return true
    }
}