package rustleund.fightingfantasy.framework.closures.impl;

import kotlin.jvm.functions.Function1;
import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PlayerState;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Objects;

import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.*;

public class AdjustScaleClosureTest extends EasyMockSupport {

	private final static DocumentBuilder documentBuilder;

	static {
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testExecute() {
		Element element = createMock(Element.class);

		//

		expect(element.getAttribute("type")).andStubReturn("time");
		expect(element.getAttribute("amount")).andStubReturn("2");
		expect(element.hasAttribute("promptOnFail")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsValue")).andStubReturn(false);
		expect(element.hasAttribute("useAmountAsPercent")).andStubReturn(false);
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		AdjustScaleClosure testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(true);
		expect(element.getAttribute("adjustInitialValue")).andStubReturn("true");
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(true);
		expect(element.getAttribute("adjustInitialValue")).andStubReturn("true");
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertTrue(testee.execute(gameState));

		assertEquals(12, playerState.getProvisions().getCurrentValue());
		assertEquals(13, Objects.requireNonNull(playerState.getProvisions().getUpperBound()).intValue());
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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(true);
		expect(element.getAttribute("adjustInitialValue")).andStubReturn("true");
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		oldAmount = playerState.getProvisions().getCurrentValue();
		assertEquals(12, oldAmount);

		assertTrue(testee.execute(gameState));

		assertEquals(12, playerState.getProvisions().getCurrentValue());
		assertEquals(25, Objects.requireNonNull(playerState.getProvisions().getUpperBound()).intValue());
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
		expect(element.getAttribute("round")).andStubReturn("");
		expect(element.hasAttribute("adjustInitialValue")).andStubReturn(false);
		expect(element.hasAttribute("rollDiceAmount")).andStubReturn(false);
		expect(element.hasAttribute("negate")).andStubReturn(false);

		replayAll();

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

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

		testee = new AdjustScaleClosure(element, GameState::getPlayerState);

		playerState = newPlayer();
		gameState.setPlayerState(playerState);

		assertFalse(testee.execute(gameState));

		verifyAll();
		resetAll();
	}

	@Test
	public void testFormula() {
		String testElementString = """
				<adjustScale type="stamina" amount="0" formula="AMT-(ceil(roll(1)/2)*2)" />
				""";
		Function1<Integer, Integer> diceRoller = createMock(Function1.class);
		GameState gameState = new GameState();
		PlayerState playerState = newPlayer();
		int startingStamina = playerState.getStamina().getCurrentValue();
		gameState.setPlayerState(playerState);

		AdjustScaleClosure testee = new AdjustScaleClosure(loadElement(testElementString), GameState::getPlayerState, diceRoller);

		expect(diceRoller.invoke(1)).andReturn(3);

		replayAll();

		assertTrue(testee.execute(gameState));
		assertEquals(startingStamina - 4, playerState.getStamina().getCurrentValue());

		verifyAll();
		resetAll();

		//

		playerState.getStamina().adjustCurrentValue(4);

		expect(diceRoller.invoke(1)).andReturn(2);

		replayAll();

		assertTrue(testee.execute(gameState));
		assertEquals(startingStamina - 2, playerState.getStamina().getCurrentValue());

		verifyAll();
		resetAll();
	}

	private PlayerState newPlayer() {
		return new PlayerState("test", new ArrayList<>());
	}

	private Element loadElement(String elementString) {
		try {
			return documentBuilder.parse(new ByteArrayInputStream(elementString.getBytes())).getDocumentElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
