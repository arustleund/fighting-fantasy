package rustleund.fightingfantasy.framework.closures.impl

import org.junit.jupiter.api.Test
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.PlayerState
import rustleund.fightingfantasy.framework.base.hasChildElements
import rustleund.fightingfantasy.framework.base.parseElement
import rustleund.fightingfantasy.framework.closures.Closure
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import strikt.assertions.isTrue
import java.util.function.Function

class SetOnPlayerDeathClosureTest {

    private val closureLoader = DefaultClosureLoader(
        mapOf<String, Function<Element, Closure>>("adjustScale" to Function { AdjustScaleClosure(it) })
    )

    private fun gameState() = GameState().apply {
        playerState = PlayerState("Player", 10, 20, 10, 10, 0, 0, 5, 0, emptyList())
    }

    @Test
    fun `hasChildElements is true only when there are child elements`() {
        expectThat(parseElement("<onDeath><adjustScale/></onDeath>").hasChildElements()).isTrue()
        expectThat(parseElement("<onDeath>just text</onDeath>").hasChildElements()).isFalse()
        expectThat(parseElement("<onDeath/>").hasChildElements()).isFalse()
    }

    @Test
    fun `commands inside the tag run when the player dies`() {
        val element = parseElement("""<setOnPlayerDeath><adjustScale type="skill" amount="-2"/></setOnPlayerDeath>""")
        val gameState = gameState()

        SetOnPlayerDeathClosure(element, closureLoader).execute(gameState)

        val onDeath = expectThat(gameState.playerState.onPlayerDeathClosure).isNotNull().subject
        onDeath.execute(gameState)
        expectThat(gameState.playerState.skill.currentValue).isEqualTo(8)
    }

    @Test
    fun `an empty tag clears what happens when the player dies`() {
        val gameState = gameState()
        gameState.playerState.onPlayerDeathClosure = Closure { true }

        SetOnPlayerDeathClosure(parseElement("<setOnPlayerDeath/>"), closureLoader).execute(gameState)

        expectThat(gameState.playerState.onPlayerDeathClosure).isNull()
    }
}
