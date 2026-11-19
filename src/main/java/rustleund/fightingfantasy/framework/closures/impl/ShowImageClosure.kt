package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

/**
 * Switches the picture to `images/<page>.<name>.png`. If that file doesn't exist the current picture stays.
 */
class ShowImageClosure(private val name: String) : Closure {

    @Suppress("unused")
    constructor(element: Element) : this(element.getAttribute("name"))

    init {
        require(name.isNotBlank()) { "showImage requires a name" }
    }

    override fun execute(gameState: GameState): Boolean {
        val fileName = "${gameState.pageState.pageName}.$name.png"
        if (!gameState.showImageIfExists(fileName)) {
            System.err.println("showImage: missing image $fileName")
        }
        return true
    }
}
