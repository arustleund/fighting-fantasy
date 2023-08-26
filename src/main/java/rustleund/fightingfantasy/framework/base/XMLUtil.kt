package rustleund.fightingfantasy.framework.base

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.StringWriter
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * @return A [Sequence] of all [Element]s with the name [childName] that are direct children of this [Element]
 */
fun Element.getChildElementsByName(childName: String): Sequence<Element> =
    getElementsByTagNameNS("*", childName)
        .asElementSequence()
        .filter { it.parentNode.isSameNode(this) }

fun Element.getChildElementByName(childName: String): Element? {
    val childrenNodeList = getChildElementsByName(childName).iterator()
    return if (childrenNodeList.hasNext()) {
        val result = childrenNodeList.next()
        require(!childrenNodeList.hasNext()) { "There was more than one child for parent: $localName with child name: $childName" }
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

fun Element.intAttribute(name: String) = getAttribute(name).toInt()

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

fun Element.hasChildElements(): Boolean = childNodes.asElementSequence().none()

fun writeTag(tag: Node?): String {
    return if (tag == null) "" else try {
        val tFactory = TransformerFactory.newInstance()
        val transformer = tFactory.newTransformer()
        transformer.setOutputProperty("omit-xml-declaration", "yes")
        val source = DOMSource(tag)
        val writer = StringWriter()
        val streamResult = StreamResult(writer)
        transformer.transform(source, streamResult)
        writer.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

