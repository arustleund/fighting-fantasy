/*
 * Created on Oct 10, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.Closure;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;
import rustleund.nightdragon.framework.util.DiceRoller;

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