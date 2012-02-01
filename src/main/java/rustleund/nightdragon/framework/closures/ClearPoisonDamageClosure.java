package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.Closure;
import rustleund.nightdragon.framework.GameState;

public class ClearPoisonDamageClosure implements Closure {

	public ClearPoisonDamageClosure(@SuppressWarnings("unused") Element element) {
		// Nothing needed
	}

	public void execute(GameState gameState) {
		gameState.getPlayerState().clearPoisonDamage();
	}

	public boolean executeWasSuccessful() {
		return true;
	}

}
