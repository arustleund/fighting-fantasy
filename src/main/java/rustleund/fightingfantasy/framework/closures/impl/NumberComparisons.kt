package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.optionalIntAttribute

enum class Operator(
    val label: String,
    val operation: Int.(Int) -> Boolean
) {

    LESS_THAN("lessThan", { this < it }),
    GREATER_THAN("greaterThan", { this > it }),
    AT_LEAST("atLeast", { this >= it }),
    AT_MOST("atMost", { this <= it }),
    EQUAL("equal", { this == it }),
    NOT_EQUAL("notEqual", { this != it })
}

data class Comparison(val operator: Operator, val compareAgainst: Int)

fun Element.toComparison(): Comparison {
    val operator = Operator.values().firstOrNull { this.hasAttribute(it.label) } ?: Operator.AT_LEAST
    return Comparison(operator, this.optionalIntAttribute(operator.label) ?: 1)
}

/**
 * @return The result of running this value against the given [Comparison], always `false` if this value is `null`
 */
fun Int?.testAgainst(comparison: Comparison) = this != null && comparison.operator.operation(this, comparison.compareAgainst)