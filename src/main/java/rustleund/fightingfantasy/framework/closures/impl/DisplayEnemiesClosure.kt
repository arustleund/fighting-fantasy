package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import java.lang.StringBuilder
import rustleund.fightingfantasy.framework.closures.Closure

class DisplayEnemiesClosure(element: Element) : Closure {

    private val battleId = element.getAttribute("battleId").toInt()

    override fun execute(gameState: GameState): Boolean {
        val pageState = gameState.pageState
        val sb = StringBuilder("<p><table border=\"1\">")
        sb.append("<tr><td> </td><td>SKILL</td><td>STAMINA</td></tr>")
        for (e in pageState.getBattle(battleId).enemies) {
            sb.append("<tr><td>")
            sb.append(e.name)
            sb.append("</td><td>")
            sb.append(e.skill.upperBound)
            sb.append("</td><td>")
            sb.append(e.stamina.upperBound)
            sb.append("</td></tr>")
        }
        sb.append("</table></p>")
        pageState.addToPagetext(sb.toString())
        return true
    }
}