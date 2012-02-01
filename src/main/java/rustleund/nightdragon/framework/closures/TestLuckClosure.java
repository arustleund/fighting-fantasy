/*
 * Created on Oct 10, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PageState;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class TestLuckClosure extends AbstractCommand {

	private int testLuckId = -1;

	public TestLuckClosure(Element element) {
		this.testLuckId = Integer.parseInt(element.getAttribute("id"));
		this.executeSuccessful = true;
	}

	public TestLuckClosure(int testLuckId) {
		this.testLuckId = testLuckId;
		this.executeSuccessful = true;
	}

	public void execute(GameState gameState) {
		PlayerState playerState = gameState.getPlayerState();
		PageState pageState = gameState.getPageState();

		String luckText = null;
		if (DiceRoller.rollDice(2) <= playerState.getLuck().getCurrentValue()) {
			luckText = pageState.getSuccessfulLuckText(testLuckId);
		} else {
			luckText = pageState.getUnsuccessfulLuckText(testLuckId);
		}

		pageState.addToPagetext(luckText);
		playerState.getLuck().adjustCurrentValueNoException(-1);
	}

}