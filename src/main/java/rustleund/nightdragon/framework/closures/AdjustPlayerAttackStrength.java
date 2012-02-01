package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.Closure;
import rustleund.nightdragon.framework.GameState;

public class AdjustPlayerAttackStrength implements Closure {

	private int amount = 0;

	public AdjustPlayerAttackStrength(Element element) {
		if (element.hasAttribute("amount")) {
			this.amount = Integer.valueOf(element.getAttribute("amount"));
		}
	}

	public void execute(GameState gameState) {
		gameState.getPlayerState().setAttackStrengthModifier(gameState.getPlayerState().getAttackStrengthModifier() + this.amount);
	}

	public boolean executeWasSuccessful() {
		return true;
	}

}
