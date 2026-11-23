package rustleund.fightingfantasy.framework.closures.impl

import org.easymock.EasyMock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.GameController
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.PlayerState
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.gamesave.SavedGame
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import strikt.assertions.isTrue
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.writeText

class LinkClosureTest {

    @TempDir
    lateinit var baseDirectory: Path

    private val gameState = GameState()
    private val battleEffectsLoader: BattleEffectsLoader = EasyMock.createMock(BattleEffectsLoader::class.java)

    /** Stands in for a dice roll: costs one stamina and counts how many times it ran. */
    private var rolls = 0
    private val rollClosure = Closure { state ->
        rolls++
        state.playerState.stamina.adjustCurrentValueNoException(-1)
        true
    }

    private val closureLoader: ClosureLoader = DefaultClosureLoader(
        mapOf<String, java.util.function.Function<Element, Closure>>(
            "roll" to java.util.function.Function { rollClosure },
            "displayText" to java.util.function.Function { DisplayTextClosure(it) },
            "link" to java.util.function.Function { LinkClosure(it, loader(), battleEffectsLoader) },
        )
    )

    private fun loader() = closureLoader

    private val controller = GameController(closureLoader, battleEffectsLoader, null)

    @BeforeEach
    fun setUp() {
        baseDirectory.resolve("pages").createDirectories()
        baseDirectory.resolve("images").createDirectories()
        baseDirectory.resolve("sounds").createDirectories()
        gameState.setBaseDirectory(baseDirectory)
        gameState.playerState = PlayerState("Test", 10, 20, 10, 10, 0, 0, 0, 0, listOf())
        controller.gameState = gameState
    }

    private fun page(name: String, immediate: String = "", texts: String = "") {
        baseDirectory.resolve("pages/$name.xml").writeText(
            "<page><pagetext><html>Page $name</html></pagetext><immediate>$immediate</immediate>$texts</page>"
        )
    }

    private fun link(name: String) = LinkClosure(name, closureLoader, battleEffectsLoader).execute(gameState)

    @Test
    fun savesStateAfterImmediateCommands() {
        page("1", immediate = """<roll/><displayText id="1"/>""", texts = """<text id="1">You rolled</text>""")

        expectThat(link("1")).isTrue()

        val saved = gameState.latestGameProgress
        expectThat(saved).isNotNull()
        expectThat(saved.pageId()).isEqualTo("1")
        expectThat(saved.playerState().stamina.currentValue).isEqualTo(19)
        expectThat(saved.pageText()).isNotNull().contains("You rolled")
        expectThat(rolls).isEqualTo(1)
    }

    @Test
    fun restoreDoesNotRunImmediateCommandsAgain() {
        page("1", immediate = """<roll/><displayText id="1"/>""", texts = """<text id="1">You rolled</text>""")
        link("1")
        val saved = gameState.latestGameProgress

        controller.restoreGame(saved)

        expectThat(rolls).isEqualTo(1)
        expectThat(gameState.playerState.stamina.currentValue).isEqualTo(19)
        expectThat(gameState.pageState.pageName).isEqualTo("1")
        // the text is there once, not displayed a second time
        expectThat(gameState.pageState.pagetext).isEqualTo(saved.pageText())
        expectThat(gameState.isPageLoaded).isEqualTo(false)
    }

    @Test
    fun restoredGameIsLatestProgressAndKeepsSnapshotUntouchedByLaterPlay() {
        page("1", immediate = "<roll/>")
        link("1")
        val saved = gameState.latestGameProgress

        controller.restoreGame(saved)
        gameState.playerState.stamina.adjustCurrentValueNoException(-5)

        expectThat(gameState.latestGameProgress).isEqualTo(saved)
        expectThat(saved.playerState().stamina.currentValue).isEqualTo(19)
    }

    @Test
    fun restoresImageAndNarration() {
        page("1", immediate = "<roll/>")
        baseDirectory.resolve("images/1.png").createFile()
        baseDirectory.resolve("sounds/1.mp3").createFile()
        link("1")

        controller.restoreGame(gameState.latestGameProgress)

        expectThat(gameState.pageState.currentImage).isEqualTo(baseDirectory.resolve("images/1.png"))
        expectThat(gameState.pageState.narrationFiles.map { it.path })
            .containsExactly(baseDirectory.resolve("sounds/1.mp3"))
    }

    @Test
    fun immediateLinkOnlySavesThePageThePlayerEndsUpOn() {
        page("1", immediate = """<link page="2"/>""")
        page("2", immediate = "<roll/>")

        link("1")

        expectThat(gameState.pageState.pageName).isEqualTo("2")
        val saved = gameState.latestGameProgress
        expectThat(saved.pageId()).isEqualTo("2")
        // popping the page we're on leaves nothing from the pass-through page behind
        gameState.popGameProgress()
        expectThat(gameState.latestGameProgress).isNull()
    }

    @Test
    fun deadPageIsNotSaved() {
        page("0")

        link("0")

        expectThat(gameState.pageState.pageName).isEqualTo("0")
        expectThat(gameState.latestGameProgress).isNull()
    }

    @Test
    fun savedGameFromBeforeSnapshotsRunsImmediateCommands() {
        page("1", immediate = "<roll/>")
        val legacy = SavedGame("1", gameState.playerState.deepCopy(), null, null, null)

        controller.restoreGame(legacy)

        expectThat(rolls).isEqualTo(1)
        expectThat(gameState.playerState.stamina.currentValue).isEqualTo(19)
        expectThat(gameState.pageState.pageName).isEqualTo("1")
        expectThat(gameState.pageState.pagetext).contains("Page 1")
    }
}
