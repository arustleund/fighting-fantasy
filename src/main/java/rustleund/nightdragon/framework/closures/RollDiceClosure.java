/*
 * Created on Oct 15, 2005
 */
package rustleund.nightdragon.framework.closures;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.Closure;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;
import rustleund.nightdragon.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class RollDiceClosure extends AbstractCommand {

	private int number = 1;

	private Map rollMappings = null;

	public RollDiceClosure(Element element) {

		this.executeSuccessful = true;

		if (element.hasAttribute("number")) {
			this.number = Integer.parseInt(element.getAttribute("number"));
		}

		this.rollMappings = new HashMap();
		NodeList doActionsElements = element.getElementsByTagName("doActions");
		for (int i = 0; i < doActionsElements.getLength(); i++) {
			Element doActionsElement = (Element) doActionsElements.item(i);

			Closure actionsForElement = AbstractCommandLoader.loadChainedClosure(doActionsElement);

			String rolls = doActionsElement.getAttribute("rolls");
			StringTokenizer rollTokenizer = new StringTokenizer(rolls, ",");
			while (rollTokenizer.hasMoreTokens()) {
				Integer roll = new Integer(rollTokenizer.nextToken());
				rollMappings.put(roll, actionsForElement);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(GameState gameState) {
		int diceRollResult = DiceRoller.rollDice(this.number);
		if (this.rollMappings.containsKey(diceRollResult)) {
			((Closure) this.rollMappings.get(new Integer(diceRollResult))).execute(gameState);
		}
	}

}