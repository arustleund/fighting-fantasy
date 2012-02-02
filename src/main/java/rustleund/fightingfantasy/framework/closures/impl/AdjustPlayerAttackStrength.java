package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

public class AdjustPlayerAttackStrength implements Closure {

	private int amount = 0;

	public AdjustPlayerAttackStrength(Element element) {
		if (element.hasAttribute("amount")) {
			this.amount = Integer.valueOf(element.getAttribute("amount"));
		}
	}

	@Override
	public boolean execute(GameState gameState) {
		gameState.getPlayerState().setAttackStrengthModifier(gameState.getPlayerState().getAttackStrengthModifier() + this.amount);
		return true;
	}

}
