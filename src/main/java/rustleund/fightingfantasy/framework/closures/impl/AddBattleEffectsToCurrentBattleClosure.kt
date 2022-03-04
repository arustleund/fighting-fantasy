package rustleund.fightingfantasy.framework.closures.impl;

import java.util.List;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleEffects;
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

public class AddBattleEffectsToCurrentBattleClosure implements Closure {

	private List<BattleEffects> battleEffects;

	public AddBattleEffectsToCurrentBattleClosure(Element element, BattleEffectsLoader battleEffectsLoader) {
		this.battleEffects = battleEffectsLoader.loadAllBattleEffectsFromTag(element);
	}

	@Override
	public boolean execute(GameState gameState) {
		if (this.battleEffects != null) {
			gameState.getBattleState().getAllBattleEffects().addAll(this.battleEffects);
		}
		return true;
	}

}
