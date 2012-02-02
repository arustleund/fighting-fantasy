package rustleund.fightingfantasy.framework.closures;

import java.util.List;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.BattleEffects;
import rustleund.fightingfantasy.framework.BattleEffectsLoaderUtil;
import rustleund.fightingfantasy.framework.Closure;
import rustleund.fightingfantasy.framework.GameState;

public class AddBattleEffectsToCurrentBattleClosure implements Closure {

	private List<BattleEffects> battleEffects;

	public AddBattleEffectsToCurrentBattleClosure(Element element) {
		this.battleEffects = BattleEffectsLoaderUtil.loadAllBattleEffectsFromTag(element);
	}

	@Override
	public boolean execute(GameState gameState) {
		if (this.battleEffects != null) {
			gameState.getBattleState().getAllBattleEffects().addAll(this.battleEffects);
		}
		return true;
	}

}
