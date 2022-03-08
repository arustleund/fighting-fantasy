package rustleund.fightingfantasy.framework.base

/**
 * @property total The total of the die roll, skill rating, and modifier
 */
data class AttackStrength(
    private val dieRoll: Int,
    private val skill: Int,
    private val modifier: Int
) : Comparable<AttackStrength> {

    val total = dieRoll + skill + modifier

    override fun compareTo(other: AttackStrength): Int {
        return total.compareTo(other.total)
    }

    override fun equals(other: Any?) =
        this === other || (other is AttackStrength && total == other.total)

    override fun hashCode(): Int {
        return total
    }

    override fun toString(): String {
        return "$total ($skill skill + $modifier modifier + $dieRoll rolled)"
    }
}