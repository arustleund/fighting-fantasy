package rustleund.fightingfantasy.framework.closures.impl;

import java.util.List;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleEffects;
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

public class AddBattleEffectsToNextBattleRoundClosure implements Closure {

	private List<BattleEffects> battleEffects;

	public AddBattleEffectsToNextBattleRoundClosure(Element element, BattleEffectsLoader battleEffectsLoader) {
		this.battleEffects = battleEffectsLoader.loadAllBattleEffectsFromTag(element);
	}

	@Override
	public boolean execute(GameState gameState) {
		gameState.getBattleState().addBattleEffectsForNextRound(battleEffects);
		return true;
	}
}
