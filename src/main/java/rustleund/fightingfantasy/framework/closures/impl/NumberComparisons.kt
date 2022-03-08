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
    val operator = Operator.values().firstOrNull { this.hasAttribute(it.label) } ?: Operator.LESS_THAN
    return Comparison(operator, this.optionalIntAttribute(operator.label) ?: 1)
}

fun Int.testAgainst(comparison: Comparison) = comparison.operator.operation(this, comparison.compareAgainst)