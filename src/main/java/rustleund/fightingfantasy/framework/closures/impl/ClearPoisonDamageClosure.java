package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

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
