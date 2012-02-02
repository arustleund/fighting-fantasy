/*
 * Created on Oct 15, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class RollDiceClosure extends AbstractClosure {

	private int number = 1;
	private Map<Integer, Closure> rollMappings;

	public RollDiceClosure(Element element, ClosureLoader closureLoader) {
		if (element.hasAttribute("number")) {
			this.number = Integer.parseInt(element.getAttribute("number"));
		}

		this.rollMappings = new HashMap<Integer, Closure>();
		NodeList doActionsElements = element.getElementsByTagName("doActions");
		for (int i = 0; i < doActionsElements.getLength(); i++) {
			Element doActionsElement = (Element) doActionsElements.item(i);

			Closure actionsForElement = closureLoader.loadClosureFromChildren(doActionsElement);

			String rolls = doActionsElement.getAttribute("rolls");
			StringTokenizer rollTokenizer = new StringTokenizer(rolls, ",");
			while (rollTokenizer.hasMoreTokens()) {
				Integer roll = Integer.valueOf(rollTokenizer.nextToken());
				rollMappings.put(roll, actionsForElement);
			}
		}
	}

	@Override
	public boolean execute(GameState gameState) {
		int diceRollResult = DiceRoller.rollDice(this.number);
		if (this.rollMappings.containsKey(diceRollResult)) {
			this.rollMappings.get(diceRollResult).execute(gameState);
		}
		return true;
	}

}