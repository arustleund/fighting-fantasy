package rustleund.fightingfantasy.framework.closures.impl

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.PageState
import rustleund.test.TestElement
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.isTrue
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile

class ShowImageClosureTest {

    @TempDir
    lateinit var baseDirectory: Path

    private val gameState = GameState()

    @BeforeEach
    fun setUp() {
        baseDirectory.resolve("images").createDirectories()
        val factory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
        val document = factory.newDocumentBuilder()
            .parse("<page><pagetext><html></html></pagetext></page>".byteInputStream())
        gameState.setBaseDirectory(baseDirectory)
        gameState.pageState = PageState("78", null, null, document, gameState)
    }

    private fun image(name: String) = baseDirectory.resolve("images").resolve(name).also { it.createFile() }

    @Test
    fun showsPageScopedImage() {
        val dragon = image("78.dragon.png")

        expectThat(ShowImageClosure(TestElement(mapOf("name" to "dragon"))).execute(gameState)).isTrue()

        expectThat(gameState.pageState.currentImage).isEqualTo(dragon)
    }

    @Test
    fun keepsCurrentImageWhenFileIsMissing() {
        val pageImage = image("78.png")
        gameState.showImageIfExists("78.png")

        expectThat(ShowImageClosure("dragon").execute(gameState)).isTrue()

        expectThat(gameState.pageState.currentImage).isEqualTo(pageImage)
    }

    @Test
    fun imageForAnotherPageIsNotUsed() {
        image("79.dragon.png")

        ShowImageClosure("dragon").execute(gameState)

        expectThat(gameState.pageState.currentImage).isNull()
    }

    @Test
    fun displayTextSwitchesToTextImage() {
        val pageImage = image("78.png")
        val textImage = image("78.3.png")
        gameState.pageState.texts[3] = "<p>text</p>"
        gameState.pageState.pagetext = "<html></html>"
        gameState.showImageIfExists("78.png")
        expectThat(gameState.pageState.currentImage).isEqualTo(pageImage)

        DisplayTextClosure(3).execute(gameState)

        expectThat(gameState.pageState.currentImage).isEqualTo(textImage)
    }

    @Test
    fun displayTextWithoutImageKeepsCurrentImage() {
        val pageImage = image("78.png")
        gameState.pageState.texts[4] = "<p>text</p>"
        gameState.pageState.pagetext = "<html></html>"
        gameState.showImageIfExists("78.png")

        DisplayTextClosure(4).execute(gameState)

        expectThat(gameState.pageState.currentImage).isEqualTo(pageImage)
    }
}
