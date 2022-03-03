package rustleund.fightingfantasy.framework.closures

import org.w3c.dom.Element

interface ClosureLoader {

    fun loadClosureFromElement(element: Element): Closure

    fun loadClosureFromChildren(element: Element): Closure

    fun loadClosureFromChild(element: Element, childName: String): Closure
}