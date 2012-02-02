/*
 * Created on Oct 10, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class TestSkillClosure extends AbstractClosure {

	private int diceRollAdjustment = 0;
	private Closure successful;
	private Closure unsuccessful;

	public TestSkillClosure(Element element) {
		if (element.hasAttribute("diceRollAdjustment")) {
			this.diceRollAdjustment = Integer.parseInt(element.getAttribute("diceRollAdjustment"));
		}

		this.successful = DefaultClosureLoader.loadClosureFromChildTag(element, "successful");
		this.unsuccessful = DefaultClosureLoader.loadClosureFromChildTag(element, "unsuccessful");
	}

	@Override
	public boolean execute(GameState gameState) {
		PlayerState playerState = gameState.getPlayerState();

		if ((DiceRoller.rollDice(2) + diceRollAdjustment) <= playerState.getSkill().getCurrentValue()) {
			return successful.execute(gameState);
		}
		return unsuccessful.execute(gameState);
	}

}