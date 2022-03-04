package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import java.util.function.Predicate

/**
 * @param predicate The [Predicate] to use to determine which [Closure] to execute
 * @param closureLoader The [ClosureLoader] to use for loading the true and false [Closure]s
 * @param element The [Element] that represents the main test [Element], should have one each of `<successful />` and `<unsuccessful />` child elements
 */
class TestClosure(
    private val predicate: Predicate<in GameState>,
    closureLoader: ClosureLoader,
    element: Element
) : Closure {

    private val trueClosure = closureLoader.loadClosureFromChild(element, "successful")
    private val falseClosure = closureLoader.loadClosureFromChild(element, "unsuccessful")

    override fun execute(gameState: GameState): Boolean =
        (if (predicate.test(gameState)) trueClosure else falseClosure).execute(gameState)
}