package rustleund.fightingfantasy.framework.closures.impl

import org.easymock.EasyMock
import org.easymock.EasyMockSupport
import org.junit.jupiter.api.Test
import rustleund.fightingfantasy.framework.base.*
import rustleund.test.TestElement
import strikt.api.expect
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

class AddItemClosureTest : EasyMockSupport() {

    @Test
    fun testExecute() {
        val addItemElement = TestElement(mapOf("id" to "1"))
        val itemUtil: ItemUtil = createMock(ItemUtil::class.java)

        val gameState = GameState()
        val playerState: PlayerState = createMock(PlayerState::class.java)
        gameState.playerState = playerState
        val pageState: PageState = createMock(PageState::class.java)
        gameState.pageState = pageState

        val defaultPrice = 4
        val testItem = Item(1, "Test Item", defaultPrice)
        val startGoldValue = 10

        val gold = Scale(0, startGoldValue, null, false)

        EasyMock.expect(itemUtil.getItem(1)).andReturn(testItem)
        EasyMock.expect(pageState.hasKeepMinimumForScale("gold")).andStubReturn(false)
        EasyMock.expect(playerState.gold).andStubReturn(gold)
        playerState.addItem(testItem)

        replayAll()

        val testee = AddItemClosure(addItemElement, itemUtil)

        expect {
            that(testee.execute(gameState)).isTrue()
            that(gold.currentValue).isEqualTo(startGoldValue - defaultPrice)
        }

        verifyAll()
        resetAll()
    }
}