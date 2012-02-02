/*
 * Created on Oct 27, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

/**
 * @author rustlea
 */
public class MustEatMealClosure extends AbstractClosure {

	private int number = 1;

	private ClosureLoader closureLoader;
	private BattleEffectsLoader battleEffectsLoader;

	public MustEatMealClosure(Element element, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		this.closureLoader = closureLoader;
		this.battleEffectsLoader = battleEffectsLoader;
		if (element.hasAttribute("amount")) {
			this.number = Integer.parseInt(element.getAttribute("amount"));
		}
	}

	@Override
	public boolean execute(GameState gameState) {
		PlayerState playerState = gameState.getPlayerState();

		for (int i = 0; i < this.number; i++) {
			if (playerState.getProvisions().getCurrentValue() > 0) {
				playerState.getProvisions().adjustCurrentValueNoException(-1);
			} else {
				gameState.setMessage("You lost 2 Stamina because you were out of provisions");
				playerState.getStamina().adjustCurrentValueNoException(-2);
				if (playerState.isDead()) {
					new LinkClosure("0", closureLoader, battleEffectsLoader).execute(gameState);
					return false;
				}
			}
		}

		return true;
	}

}