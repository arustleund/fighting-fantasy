package rustleund.fightingfantasy.framework.closures.impl;

import java.util.function.Function;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.AbstractEntityState;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.util.DiceRoller;

import com.google.common.base.Predicate;

public class TestSkillPredicate implements Predicate<GameState> {

	private int diceRollAdjustment;
	private Function<? super GameState, ? extends AbstractEntityState> gameStateToEntity;

	public TestSkillPredicate(Element element, Function<? super GameState, ? extends AbstractEntityState> gameStateToEntity) {
		if (element.hasAttribute("diceRollAdjustment")) {
			this.diceRollAdjustment = Integer.parseInt(element.getAttribute("diceRollAdjustment"));
		}
		this.gameStateToEntity = gameStateToEntity;
	}

	@Override
	public boolean apply(GameState gameState) {
		AbstractEntityState entity = this.gameStateToEntity.apply(gameState);
		return (DiceRoller.rollDice(2) + diceRollAdjustment) <= entity.getSkill().getCurrentValue();
	}
}
