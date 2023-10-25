package rustleund.test

import org.w3c.dom.*

class TestElement(
    private val attributes: Map<String, String> = mapOf()
) : Element {

    override fun getNodeName(): String {
        TODO("Not yet implemented")
    }

    override fun getNodeValue(): String {
        TODO("Not yet implemented")
    }

    override fun setNodeValue(nodeValue: String?) {
        TODO("Not yet implemented")
    }

    override fun getNodeType(): Short {
        TODO("Not yet implemented")
    }

    override fun getParentNode(): Node {
        TODO("Not yet implemented")
    }

    override fun getChildNodes(): NodeList {
        TODO("Not yet implemented")
    }

    override fun getFirstChild(): Node {
        TODO("Not yet implemented")
    }

    override fun getLastChild(): Node {
        TODO("Not yet implemented")
    }

    override fun getPreviousSibling(): Node {
        TODO("Not yet implemented")
    }

    override fun getNextSibling(): Node {
        TODO("Not yet implemented")
    }

    override fun getAttributes(): NamedNodeMap {
        TODO("Not yet implemented")
    }

    override fun getOwnerDocument(): Document {
        TODO("Not yet implemented")
    }

    override fun insertBefore(newChild: Node?, refChild: Node?): Node {
        TODO("Not yet implemented")
    }

    override fun replaceChild(newChild: Node?, oldChild: Node?): Node {
        TODO("Not yet implemented")
    }

    override fun removeChild(oldChild: Node?): Node {
        TODO("Not yet implemented")
    }

    override fun appendChild(newChild: Node?): Node {
        TODO("Not yet implemented")
    }

    override fun hasChildNodes(): Boolean {
        TODO("Not yet implemented")
    }

    override fun cloneNode(deep: Boolean): Node {
        TODO("Not yet implemented")
    }

    override fun normalize() {
        TODO("Not yet implemented")
    }

    override fun isSupported(feature: String?, version: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getNamespaceURI(): String {
        TODO("Not yet implemented")
    }

    override fun getPrefix(): String {
        TODO("Not yet implemented")
    }

    override fun setPrefix(prefix: String?) {
        TODO("Not yet implemented")
    }

    override fun getLocalName(): String {
        TODO("Not yet implemented")
    }

    override fun hasAttributes(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBaseURI(): String {
        TODO("Not yet implemented")
    }

    override fun compareDocumentPosition(other: Node?): Short {
        TODO("Not yet implemented")
    }

    override fun getTextContent(): String {
        TODO("Not yet implemented")
    }

    override fun setTextContent(textContent: String?) {
        TODO("Not yet implemented")
    }

    override fun isSameNode(other: Node?): Boolean {
        TODO("Not yet implemented")
    }

    override fun lookupPrefix(namespaceURI: String?): String {
        TODO("Not yet implemented")
    }

    override fun isDefaultNamespace(namespaceURI: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun lookupNamespaceURI(prefix: String?): String {
        TODO("Not yet implemented")
    }

    override fun isEqualNode(arg: Node?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFeature(feature: String?, version: String?): Any {
        TODO("Not yet implemented")
    }

    override fun setUserData(key: String?, data: Any?, handler: UserDataHandler?): Any {
        TODO("Not yet implemented")
    }

    override fun getUserData(key: String?): Any {
        TODO("Not yet implemented")
    }

    override fun getTagName(): String {
        TODO("Not yet implemented")
    }

    override fun getAttribute(name: String?) = attributes[name] ?: ""

    override fun setAttribute(name: String?, value: String?) {
        TODO("Not yet implemented")
    }

    override fun removeAttribute(name: String?) {
        TODO("Not yet implemented")
    }

    override fun getAttributeNode(name: String?): Attr {
        TODO("Not yet implemented")
    }

    override fun setAttributeNode(newAttr: Attr?): Attr {
        TODO("Not yet implemented")
    }

    override fun removeAttributeNode(oldAttr: Attr?): Attr {
        TODO("Not yet implemented")
    }

    override fun getElementsByTagName(name: String?): NodeList {
        TODO("Not yet implemented")
    }

    override fun getAttributeNS(namespaceURI: String?, localName: String?): String {
        TODO("Not yet implemented")
    }

    override fun setAttributeNS(namespaceURI: String?, qualifiedName: String?, value: String?) {
        TODO("Not yet implemented")
    }

    override fun removeAttributeNS(namespaceURI: String?, localName: String?) {
        TODO("Not yet implemented")
    }

    override fun getAttributeNodeNS(namespaceURI: String?, localName: String?): Attr {
        TODO("Not yet implemented")
    }

    override fun setAttributeNodeNS(newAttr: Attr?): Attr {
        TODO("Not yet implemented")
    }

    override fun getElementsByTagNameNS(namespaceURI: String?, localName: String?): NodeList {
        TODO("Not yet implemented")
    }

    override fun hasAttribute(name: String?) = attributes.containsKey(name)

    override fun hasAttributeNS(namespaceURI: String?, localName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSchemaTypeInfo(): TypeInfo {
        TODO("Not yet implemented")
    }

    override fun setIdAttribute(name: String?, isId: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setIdAttributeNS(namespaceURI: String?, localName: String?, isId: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setIdAttributeNode(idAttr: Attr?, isId: Boolean) {
        TODO("Not yet implemented")
    }
}