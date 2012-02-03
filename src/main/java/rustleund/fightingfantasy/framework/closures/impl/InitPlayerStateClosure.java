/*
 * Created on Oct 25, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.util.ItemUtil;

import com.google.common.collect.Lists;

/**
 * @author rustlea
 */
public class InitPlayerStateClosure extends AbstractClosure {

	public InitPlayerStateClosure(@SuppressWarnings("unused") Element element) {
		// Must be here to satisfy contract
	}

	@Override
	public boolean execute(GameState gameState) {
		ItemUtil iu = ItemUtil.getInstance();
		gameState.setPlayerState(new PlayerState(gameState.getPlayerState().getName(), Lists.newArrayList(iu.getItem(0), iu.getItem(1), iu.getItem(2), iu.getItem(3))));
		return true;
	}

}