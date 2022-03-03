/*
 * Created on Oct 25, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.ItemUtil;
import rustleund.fightingfantasy.framework.base.PlayerState;

import com.google.common.collect.Lists;

/**
 * @author rustlea
 */
public class InitPlayerStateClosure extends AbstractClosure {

	private ItemUtil itemUtl;

	public InitPlayerStateClosure(ItemUtil itemUtil) {
		this.itemUtl = itemUtil;
	}

	@Override
	public boolean execute(GameState gameState) {
		gameState.setPlayerState(new PlayerState(gameState.getPlayerState().getName(), Lists.newArrayList(itemUtl.getItem(0), itemUtl.getItem(1), itemUtl.getItem(2), itemUtl.getItem(3))));
		return true;
	}

}