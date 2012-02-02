package rustleund.fightingfantasy.framework.closures.impl;

import java.util.List;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleEffects;
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

public class AddBattleEffectsForNextBattleClosure implements Closure {

	private List<BattleEffects> nextBattleBattleEffects;

	public AddBattleEffectsForNextBattleClosure(Element element) {
		this.nextBattleBattleEffects = BattleEffectsLoader.loadAllBattleEffectsFromTag(element);
	}

	@Override
	public boolean execute(GameState gameState) {
		if (this.nextBattleBattleEffects != null) {
			if (gameState.getPlayerState().getNextBattleBattleEffects() == null) {
				gameState.getPlayerState().setNextBattleBattleEffects(this.nextBattleBattleEffects);
			} else {
				gameState.getPlayerState().getNextBattleBattleEffects().addAll(this.nextBattleBattleEffects);
			}
		}
		return true;
	}

}
