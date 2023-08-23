/*
 * Created on Oct 7, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.asElementSequence
import rustleund.fightingfantasy.framework.base.getChildElementByName
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import java.lang.IllegalArgumentException
import rustleund.fightingfantasy.framework.closures.Closure
import java.util.function.Function

/**
 * @author rustlea
 */
class DefaultClosureLoader(private val mappings: Map<String, Function<Element, Closure>>) : ClosureLoader {

    override fun loadClosureFromElement(element: Element): Closure {
        val commandTagType = element.localName
        return mappings[commandTagType]?.apply(element)
            ?: throw IllegalArgumentException("No closure mapping found for $commandTagType")
    }

    override fun loadClosureFromChildren(element: Element): Closure {
        val closures = element.childNodes.asElementSequence().map { loadClosureFromElement(it) }.toList()
        return ChainedClosure(closures)
    }

    override fun loadClosureFromChild(element: Element, childName: String): Closure =
        element.getChildElementByName(childName)?.let { loadClosureFromChildren(it) } ?: ChainedClosure()
}