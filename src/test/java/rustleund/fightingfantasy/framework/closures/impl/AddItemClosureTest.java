package rustleund.fightingfantasy.framework.closures.impl;

import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import rustleund.fightingfantasy.framework.base.*;

import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddItemClosureTest extends EasyMockSupport {

	@Test
	public void testExecute() {
		Element addItemElement = createMock(Element.class);
		ItemUtil itemUtil = createMock(ItemUtil.class);

		GameState gameState = new GameState();
		PlayerState playerState = createMock(PlayerState.class);
		gameState.setPlayerState(playerState);
		PageState pageState = createMock(PageState.class);
		gameState.setPageState(pageState);

		//

		int defaultPrice = 4;
		Item testItem = new Item(1, "Test Item", defaultPrice);
		int startGoldValue = 10;

		Scale gold = new Scale(0, startGoldValue, null, false);

		expect(addItemElement.hasAttribute("id")).andStubReturn(true);
		expect(addItemElement.getAttribute("id")).andStubReturn("1");
		expect(addItemElement.hasAttribute("price")).andStubReturn(false);
		expect(addItemElement.hasAttribute("quantity")).andStubReturn(false);
		expect(addItemElement.hasAttribute("quantityFormula")).andStubReturn(false);
		expect(addItemElement.hasAttribute("pageLimit")).andStubReturn(false);
		expect(itemUtil.getItem(1)).andReturn(testItem);
		expect(pageState.hasKeepMinimumForScale("gold")).andStubReturn(false);
		expect(playerState.getGold()).andStubReturn(gold);
		playerState.addItem(testItem);

		replayAll();

		AddItemClosure testee = new AddItemClosure(addItemElement, itemUtil);

		assertTrue(testee.execute(gameState));
		assertEquals(startGoldValue - defaultPrice, gold.getCurrentValue());

		verifyAll();
		resetAll();
	}

}
