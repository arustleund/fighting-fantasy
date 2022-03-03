package rustleund.fightingfantasy.framework.base.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory

class DefaultItemUtil(
    private val closureLoader: ClosureLoader,
    private val battleEffectsLoader: BattleEffectsLoader
) : ItemUtil {

    private lateinit var items: Map<Int, Item>

    override fun init(itemConfiguration: Path) {
        items = Files.newInputStream(itemConfiguration).use { itemIs ->
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            documentBuilderFactory.isNamespaceAware = true
            val itemDocument = documentBuilderFactory.newDocumentBuilder().parse(itemIs)
            getChildElementsByName(itemDocument.documentElement, "item")
                .map { loadItemTag(it) }
                .associateBy { it.id }
        }
    }

    private fun loadItemTag(itemElement: Element): Item {
        val id = itemElement.getAttribute("id").toInt()
        val itemName = itemElement.getAttribute("name")
        val defaultPrice = itemElement.getAttribute("price").toInt()
        val item = Item(id, itemName, defaultPrice)
        if (itemElement.hasAttribute("limit")) {
            item.limit = itemElement.getAttribute("limit").toInt()
        }
        loadOnUse(itemElement, item)
        loadBattleEffects(itemElement, item)
        if (itemElement.hasAttribute("canUseInBattle")) {
            item.isCanUseInBattle = itemElement.getAttribute("canUseInBattle").toBoolean()
        } else {
            item.isCanUseInBattle = true
        }
        return item
    }

    private fun loadOnUse(itemElement: Element, item: Item) {
        getChildElementByName(itemElement, "onUse")?.let {
            item.useItem = closureLoader.loadClosureFromChildren(it)
        }
    }

    private fun loadBattleEffects(itemElement: Element, item: Item) {
        getChildElementByName(itemElement, "battleEffects")?.let { battleEffectsElement ->
            val battleEffects = BattleEffects()
            battleEffectsLoader.loadBattleEffectsFromTag(battleEffects, battleEffectsElement)
            item.battleEffects = battleEffects
        }
    }

    override fun getItem(itemId: Int): Item =
        requireNotNull(items[itemId]) { "Item ID $itemId not found" }
}