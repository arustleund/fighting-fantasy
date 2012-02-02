/*
 * Created on Oct 25, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PlayerState;

/**
 * @author rustlea
 */
public class InitPlayerStateClosure extends AbstractClosure {

	public InitPlayerStateClosure(@SuppressWarnings("unused") Element element) {
		// Must be here to satisfy contract
	}

	@Override
	public boolean execute(GameState gameState) {
		gameState.setPlayerState(new PlayerState(gameState.getPlayerState().getName()));
		return true;
	}

}