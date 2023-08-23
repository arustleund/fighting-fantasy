package rustleund.fightingfantasy.framework.base

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader

class EnemyState(
    name: String,
    skill: Int,
    stamina: Int,
    val id: Int?,
    val poisonedWeaponRounds: Int,
    val poisonDamage: Int,
    private val types: Set<String> = setOf(),
    val enemyKilled: Closure? = null,
    val enemyHit: Closure? = null
) : AbstractEntityState(name, skill, stamina) {

    constructor(enemyTag: Element, closureLoader: ClosureLoader) : this(
        name = enemyTag.getAttribute("name"),
        skill = enemyTag.intAttribute("skill", 0),
        stamina = enemyTag.intAttribute("stamina", 0),
        id = enemyTag.optionalIntAttribute("id"),
        poisonedWeaponRounds = enemyTag.intAttribute("poisonedWeaponRounds", 0),
        poisonDamage = enemyTag.intAttribute("poisonDamage", 2),
        types = enemyTag.optionalAttribute("types")?.split(",")?.toSet().orEmpty(),
        enemyKilled = enemyTag.getChildElementByName("onKilled")?.let { closureLoader.loadClosureFromChildren(it) },
        enemyHit = enemyTag.getChildElementByName("onHit")?.let { closureLoader.loadClosureFromChildren(it) }
    )

    fun isOfType(type: String) = types.contains(type)
}