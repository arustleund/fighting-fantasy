package rustleund.nightdragon.framework.closures;

import java.util.List;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.BattleEffects;
import rustleund.nightdragon.framework.BattleEffectsLoaderUtil;
import rustleund.nightdragon.framework.Closure;
import rustleund.nightdragon.framework.GameState;

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
