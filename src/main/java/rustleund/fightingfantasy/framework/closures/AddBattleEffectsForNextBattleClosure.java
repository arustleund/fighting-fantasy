package rustleund.fightingfantasy.framework.closures;

import java.util.List;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.BattleEffects;
import rustleund.fightingfantasy.framework.BattleEffectsLoaderUtil;
import rustleund.fightingfantasy.framework.Closure;
import rustleund.fightingfantasy.framework.GameState;

public class AddBattleEffectsForNextBattleClosure implements Closure {

	private List<BattleEffects> nextBattleBattleEffects;

	public AddBattleEffectsForNextBattleClosure(Element element) {
		this.nextBattleBattleEffects = BattleEffectsLoaderUtil.loadAllBattleEffectsFromTag(element);
	}

	public void execute(GameState gameState) {
		if (this.nextBattleBattleEffects != null) {
			if (gameState.getPlayerState().getNextBattleBattleEffects() == null) {
				gameState.getPlayerState().setNextBattleBattleEffects(this.nextBattleBattleEffects);
			} else {
				gameState.getPlayerState().getNextBattleBattleEffects().addAll(this.nextBattleBattleEffects);
			}
		}
	}

	public boolean executeWasSuccessful() {
		return true;
	}

}
