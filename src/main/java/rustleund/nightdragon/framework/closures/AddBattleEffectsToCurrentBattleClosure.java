package rustleund.nightdragon.framework.closures;

import java.util.List;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.BattleEffects;
import rustleund.nightdragon.framework.BattleEffectsLoaderUtil;
import rustleund.nightdragon.framework.Closure;
import rustleund.nightdragon.framework.GameState;

public class AddBattleEffectsToCurrentBattleClosure implements Closure {

	private List<BattleEffects> battleEffects;

	public AddBattleEffectsToCurrentBattleClosure(Element element) {
		this.battleEffects = BattleEffectsLoaderUtil.loadAllBattleEffectsFromTag(element);
	}

	public void execute(GameState gameState) {
		if (this.battleEffects != null) {
			gameState.getBattleState().getAllBattleEffects().addAll(this.battleEffects);
		}
	}

	public boolean executeWasSuccessful() {
		return true;
	}

}
