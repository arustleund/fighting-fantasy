package rustleund.fightingfantasy.framework.closures;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.Closure;
import rustleund.fightingfantasy.framework.GameState;

public class ClearPoisonDamageClosure implements Closure {

	public ClearPoisonDamageClosure(@SuppressWarnings("unused") Element element) {
		// Nothing needed
	}

	@Override
	public boolean execute(GameState gameState) {
		gameState.getPlayerState().clearPoisonDamage();
		return true;
	}

}
