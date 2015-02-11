package rustleund.fightingfantasy.framework.closures.impl;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.Item;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

public class AdjustScaleClosureTest extends EasyMockSupport {

	@Test
	public void testExecute() {
		Element element = createMock(Element.class);
		ClosureLoader closureLoader = createMock(ClosureLoader.class);
		BattleEffectsLoader battleEffectsLoader = createMock(BattleEffectsLoader.class);

		//

		expect(element.getAttribute("type")).andStubReturn("time");
		expect(element.getAttribute("amount")).andStubReturn("2");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		AdjustScaleClosure testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		GameState gameState = new GameState();
		PlayerState playerState = newPlayer();
		gameState.setPlayerState(playerState);

		int oldAmount = playerState.getTime().getCurrentValue();

		assertTrue(testee.execute(gameState));

		assertEquals(oldAmount + 2, playerState.getTime().getCurrentValue());

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("time");
		expect(element.getAttribute("amount")).andStubReturn("4");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getTime().getCurrentValue();

		assertTrue(testee.execute(gameState));

		assertEquals(oldAmount + 4, playerState.getTime().getCurrentValue());

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("luck");
		expect(element.getAttribute("amount")).andStubReturn("-1");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getLuck().getCurrentValue();

		assertTrue(testee.execute(gameState));

		assertEquals(oldAmount - 1, playerState.getLuck().getCurrentValue());

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("time");
		expect(element.getAttribute("amount")).andStubReturn("-1");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(true);
		expect(element.getAttribute("promptOnFail")).andStubReturn("false");
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getTime().getCurrentValue();

		assertTrue(testee.execute(gameState));

		assertEquals(oldAmount, playerState.getTime().getCurrentValue());

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("time");
		expect(element.getAttribute("amount")).andStubReturn("-1");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(true);
		expect(element.getAttribute("promptOnFail")).andStubReturn("true");
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getTime().getCurrentValue();

		assertFalse(testee.execute(gameState));

		assertEquals(oldAmount, playerState.getTime().getCurrentValue());
		assertEquals("You cannot perform this action", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("time");
		expect(element.getAttribute("amount")).andStubReturn("1");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(true);
		expect(element.getAttribute("promptOnFail")).andStubReturn("true");
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getTime().getCurrentValue();

		assertTrue(testee.execute(gameState));

		assertEquals(oldAmount + 1, playerState.getTime().getCurrentValue());
		assertEquals("-", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("provisions");
		expect(element.getAttribute("amount")).andStubReturn("3");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(true);
		expect(element.getAttribute("promptOnFail")).andStubReturn("true");
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(true);
		expect(element.getAttribute("useAmountAsValue")).andStubReturn("true");
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertTrue(testee.execute(gameState));

		assertEquals(3, playerState.getProvisions().getCurrentValue());
		assertEquals("-", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("provisions");
		expect(element.getAttribute("amount")).andStubReturn("13");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(true);
		expect(element.getAttribute("promptOnFail")).andStubReturn("true");
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(true);
		expect(element.getAttribute("useAmountAsValue")).andStubReturn("true");
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertFalse(testee.execute(gameState));

		assertEquals(12, playerState.getProvisions().getCurrentValue());
		assertEquals("You cannot perform this action", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("provisions");
		expect(element.getAttribute("amount")).andStubReturn("13");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(true);
		expect(element.getAttribute("promptOnFail")).andStubReturn("true");
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(true);
		expect(element.getAttribute("useAmountAsValue")).andStubReturn("true");
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(true);
		expect(element.getAttribute("adjustInitialValue")).andStubReturn("true");
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertFalse(testee.execute(gameState));

		assertEquals(12, playerState.getProvisions().getCurrentValue());
		assertEquals("You cannot perform this action", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("provisions");
		expect(element.getAttribute("amount")).andStubReturn("13");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(true);
		expect(element.getAttribute("useAmountAsValue")).andStubReturn("true");
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(true);
		expect(element.getAttribute("adjustInitialValue")).andStubReturn("true");
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertTrue(testee.execute(gameState));

		assertEquals(12, playerState.getProvisions().getCurrentValue());
		assertEquals(13, playerState.getProvisions().getUpperBound().intValue());
		assertEquals("-", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("provisions");
		expect(element.getAttribute("amount")).andStubReturn("13");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(true);
		expect(element.getAttribute("adjustInitialValue")).andStubReturn("true");
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertTrue(testee.execute(gameState));

		assertEquals(12, playerState.getProvisions().getCurrentValue());
		assertEquals(25, playerState.getProvisions().getUpperBound().intValue());
		assertEquals("-", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("provisions");
		expect(element.getAttribute("amount")).andStubReturn("-.375");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(true);
		expect(element.getAttribute("useAmountAsPercent")).andStubReturn("true");
		expect(element.getAttribute("round")).andStubReturn(null);
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertTrue(testee.execute(gameState));

		assertEquals(8, playerState.getProvisions().getCurrentValue());
		assertEquals("-", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("provisions");
		expect(element.getAttribute("amount")).andStubReturn("-.375");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(true);
		expect(element.getAttribute("useAmountAsPercent")).andStubReturn("true");
		expect(element.getAttribute("round")).andStubReturn("up");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertTrue(testee.execute(gameState));

		assertEquals(8, playerState.getProvisions().getCurrentValue());
		assertEquals("-", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("provisions");
		expect(element.getAttribute("amount")).andStubReturn("-.375");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(true);
		expect(element.getAttribute("useAmountAsPercent")).andStubReturn("true");
		expect(element.getAttribute("round")).andStubReturn("down");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertTrue(testee.execute(gameState));

		assertEquals(7, playerState.getProvisions().getCurrentValue());
		assertEquals("-", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("time");
		expect(element.getAttribute("amount")).andStubReturn(".5");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(true);
		expect(element.getAttribute("useAmountAsPercent")).andStubReturn("true");
		expect(element.getAttribute("round")).andStubReturn("down");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		try {
			testee.execute(gameState);
			fail("Should have thrown exception");
		} catch (IllegalArgumentException e) {
			// expected
		}

		assertEquals(12, playerState.getProvisions().getCurrentValue());
		assertEquals("-", gameState.getMessage());

		gameState.clearMessage();

		verifyAll();
		resetAll();

		//

		expect(element.getAttribute("type")).andStubReturn("figglebutt");
		expect(element.getAttribute("amount")).andStubReturn(".5");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(true);
		expect(element.getAttribute("useAmountAsPercent")).andStubReturn("true");
		expect(element.getAttribute("round")).andStubReturn("down");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, closureLoader, battleEffectsLoader);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		assertFalse(testee.execute(gameState));

		verifyAll();
		resetAll();
	}

	private PlayerState newPlayer() {
		return new PlayerState("test", new ArrayList<Item>());
	}

}
