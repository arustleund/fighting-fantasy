/*
 * Created on Oct 10, 2005
 */
package rustleund.fightingfantasy.framework.closures;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.AbstractCommand;
import rustleund.fightingfantasy.framework.GameState;
import rustleund.fightingfantasy.framework.PageState;
import rustleund.fightingfantasy.framework.PlayerState;
import rustleund.fightingfantasy.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class TestLuckClosure extends AbstractCommand {

	private int testLuckId = -1;

	public TestLuckClosure(Element element) {
		this.testLuckId = Integer.parseInt(element.getAttribute("id"));
	}

	public TestLuckClosure(int testLuckId) {
		this.testLuckId = testLuckId;
	}

	@Override
	public boolean execute(GameState gameState) {
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

		return true;
	}

}