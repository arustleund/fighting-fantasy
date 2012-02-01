/*
 * Created on Oct 10, 2005
 */
package rustleund.fightingfantasy.framework.closures;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.AbstractCommand;
import rustleund.fightingfantasy.framework.Closure;
import rustleund.fightingfantasy.framework.GameState;
import rustleund.fightingfantasy.framework.PlayerState;
import rustleund.fightingfantasy.framework.util.AbstractCommandLoader;
import rustleund.fightingfantasy.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class TestSkillClosure extends AbstractCommand {

	private int diceRollAdjustment = 0;
	private Closure successful;
	private Closure unsuccessful;

	public TestSkillClosure(Element element) {
		if (element.hasAttribute("diceRollAdjustment")) {
			this.diceRollAdjustment = Integer.parseInt(element.getAttribute("diceRollAdjustment"));
		}

		this.successful = AbstractCommandLoader.loadClosureFromChildTag(element, "successful");
		this.unsuccessful = AbstractCommandLoader.loadClosureFromChildTag(element, "unsuccessful");

		this.executeSuccessful = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(GameState gameState) {

		PlayerState playerState = gameState.getPlayerState();

		if ((DiceRoller.rollDice(2) + diceRollAdjustment) <= playerState.getSkill().getCurrentValue()) {
			successful.execute(gameState);
		} else {
			unsuccessful.execute(gameState);
		}
	}

}