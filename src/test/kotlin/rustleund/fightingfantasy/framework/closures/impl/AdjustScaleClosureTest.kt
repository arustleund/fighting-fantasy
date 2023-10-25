package rustleund.fightingfantasy.framework.closures.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.PlayerState
import rustleund.fightingfantasy.framework.base.Scale
import rustleund.test.TestElement
import strikt.api.expect
import strikt.assertions.*
import java.lang.IllegalArgumentException

class AdjustScaleClosureTest : FunSpec({
    context("Adjust Scales With Different Attributes") {
        withData(
            mapOf(
                "time + 2" to TestData(mapOf("type" to "time", "amount" to "2"), expectedValueAfterChange = 2),
                "time + 4" to TestData(mapOf("type" to "time", "amount" to "4"), expectedValueAfterChange = 4),
                "time - 1 no prompt" to TestData(mapOf("type" to "time", "amount" to "-1", "promptOnFail" to "false")),
                "time - 1 prompt fail" to TestData(
                    attributes = mapOf("type" to "time", "amount" to "-1", "promptOnFail" to "true"),
                    expectFailure = true,
                    expectedGameMessage = "You cannot perform this action"
                ),
                "time + 1 prompt fail" to TestData(
                    attributes = mapOf("type" to "time", "amount" to "1", "promptOnFail" to "true"),
                    expectedValueAfterChange = 1,
                ),
                "provisions = 3 prompt fail use amount" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "3",
                        "promptOnFail" to "true",
                        "useAmountAsValue" to "true"
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 3,
                ),
                "provisions = 13 prompt fail use amount" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "13",
                        "promptOnFail" to "true",
                        "useAmountAsValue" to "true"
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 12,
                    expectFailure = true,
                    expectedGameMessage = "You cannot perform this action"
                ),
                "provisions = 13 prompt fail use amount adjust init" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "13",
                        "promptOnFail" to "true",
                        "useAmountAsValue" to "true",
                        "adjustInitialValue" to "true"
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 12,
                    expectFailure = true,
                    expectedGameMessage = "You cannot perform this action"
                ),
                "provisions = 13 no prompt fail use amount adjust init" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "13",
                        "useAmountAsValue" to "true",
                        "adjustInitialValue" to "true"
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 12,
                    expectedUpperBoundAfterChange = 13,
                ),
                "provisions = 13 no prompt fail adjust init" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "13",
                        "adjustInitialValue" to "true"
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 12,
                    expectedUpperBoundAfterChange = 25,
                ),
                "provisions = -0.375 no prompt fail amount as percent" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "-.375",
                        "useAmountAsPercent" to "true",
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 8,
                ),
                "provisions = -0.375 no prompt fail amount as percent round up" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "-.375",
                        "useAmountAsPercent" to "true",
                        "round" to "up"
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 8,
                ),
                "provisions = -0.375 no prompt fail amount as percent round down" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "-.375",
                        "useAmountAsPercent" to "true",
                        "round" to "down"
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 7,
                ),
                "time = 0.5 no prompt fail amount as percent round down" to TestData(
                    attributes = mapOf(
                        "type" to "time",
                        "amount" to ".5",
                        "useAmountAsPercent" to "true",
                        "round" to "down"
                    ),
                    expectIllegalArgument = true,
                ),
                "unknown type = 0.5 no prompt fail amount as percent round down" to TestData(
                    attributes = mapOf(
                        "type" to "figglebutt",
                        "amount" to ".5",
                        "useAmountAsPercent" to "true",
                        "round" to "down"
                    ),
                    expectFailure = true
                ),
                "provisions = 0 no prompt fail formula die roll 3" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "0",
                        "formula" to "AMT-(ceil(roll(1)/2)*2)"
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 8,
                    diceRoller = { 3 }
                ),
                "provisions = 0 no prompt fail formula die roll 2" to TestData(
                    attributes = mapOf(
                        "type" to "provisions",
                        "amount" to "0",
                        "formula" to "AMT-(ceil(roll(1)/2)*2)"
                    ),
                    scaleSelector = { it.provisions },
                    expectedValueBeforeChange = 12,
                    expectedValueAfterChange = 10,
                    diceRoller = { 2 }
                ),
            )
        ) { testData ->
            val element = TestElement(testData.attributes)
            val testee = AdjustScaleClosure(element, { it.playerState }, testData.diceRoller)

            val gameState = GameState()
            val playerState = PlayerState("test", listOf())
            gameState.playerState = playerState

            val scale = testData.scaleSelector(playerState)

            expect {
                that(scale.currentValue).isEqualTo(testData.expectedValueBeforeChange)

                if (testData.expectIllegalArgument) {
                    catching { testee.execute(gameState) }.isFailure().isA<IllegalArgumentException>()
                } else {
                    that(testee.execute(gameState)).isNotEqualTo(testData.expectFailure)
                    that(scale.currentValue).isEqualTo(testData.expectedValueAfterChange)

                    if (testData.expectedUpperBoundAfterChange != null) {
                        that(scale.upperBound).isNotNull().isEqualTo(testData.expectedUpperBoundAfterChange)
                    }

                    if (testData.expectedGameMessage != null) {
                        that(gameState.message).isEqualTo(testData.expectedGameMessage)
                        gameState.clearMessage()
                    }
                }
            }
        }
    }
})

private data class TestData(
    val attributes: Map<String, String>,
    val scaleSelector: (PlayerState) -> Scale = { it.time },
    val expectedValueBeforeChange: Int = 0,
    val expectedValueAfterChange: Int = 0,
    val expectedUpperBoundAfterChange: Int? = null,
    val expectIllegalArgument: Boolean = false,
    val expectFailure: Boolean = false,
    val expectedGameMessage: String? = null,
    val diceRoller: (Int) -> Int = { 1 },
)