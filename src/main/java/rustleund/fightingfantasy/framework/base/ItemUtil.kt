package rustleund.fightingfantasy.framework.base

import java.nio.file.Path

interface ItemUtil {

    fun init(itemConfiguration: Path)

    fun getItem(itemId: Int): Item
}