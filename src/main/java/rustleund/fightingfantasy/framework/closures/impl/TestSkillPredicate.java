package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.util.DiceRoller;

import com.google.common.base.Predicate;

public class TestSkillPredicate implements Predicate<GameState> {

	private int diceRollAdjustment;

	public TestSkillPredicate(Element element) {
		if (element.hasAttribute("diceRollAdjustment")) {
			this.diceRollAdjustment = Integer.parseInt(element.getAttribute("diceRollAdjustment"));
		}
	}

	@Override
	public boolean apply(GameState gameState) {
		return (DiceRoller.rollDice(2) + diceRollAdjustment) <= gameState.getPlayerState().getSkill().getCurrentValue();
	}

}
