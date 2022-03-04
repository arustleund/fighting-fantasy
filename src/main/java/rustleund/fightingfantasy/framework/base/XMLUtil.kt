package rustleund.fightingfantasy.framework.base

import org.w3c.dom.Element
import org.w3c.dom.NodeList

/**
 * @return A [Sequence] of all [Element]s with the name [childName] that are direct children of the given [parentElement]
 */
fun getChildElementsByName(parentElement: Element, childName: String): Sequence<Element> =
    parentElement.getElementsByTagNameNS("*", childName)
        .asElementSequence()
        .filter { it.parentNode.isSameNode(parentElement) }

fun getChildElementByName(parentElement: Element, childName: String): Element? {
    val childrenNodeList = getChildElementsByName(parentElement, childName).iterator()
    return if (childrenNodeList.hasNext()) {
        val result = childrenNodeList.next()
        require(!childrenNodeList.hasNext()) { "There was more than one child for parent: ${parentElement.localName} with child name: $childName" }
        result
    } else null
}

fun Element.getAncestorByName(name: String): Element? {
    val parent = this.parentNode
    return if (parent !is Element) null else
        if (parent.localName == name) parent else parent.getAncestorByName(name)
}

fun Element.optionalAttribute(name: String): String? =
    if (this.hasAttribute(name)) this.getAttribute(name) else null

fun Element.optionalIntAttribute(name: String) = optionalAttribute(name)?.toInt()

fun Element.intAttribute(name: String, default: Int) = optionalAttribute(name)?.toInt() ?: default

/**
 * @return The [String.toBoolean] value of the given attribute for this [Element], or the given [default] value
 * if the attribute is not present.
 */
fun Element.booleanAttribute(name: String, default: Boolean = false) = optionalAttribute(name)?.toBoolean() ?: default

fun NodeList.asElementSequence(): Sequence<Element> =
    (0 until this.length).asSequence()
        .map { this.item(it) }
        .filter { it is Element }
        .map { it as Element }

