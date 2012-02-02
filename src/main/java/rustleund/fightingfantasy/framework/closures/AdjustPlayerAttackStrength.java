package rustleund.fightingfantasy.framework.closures;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.Closure;
import rustleund.fightingfantasy.framework.GameState;

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
