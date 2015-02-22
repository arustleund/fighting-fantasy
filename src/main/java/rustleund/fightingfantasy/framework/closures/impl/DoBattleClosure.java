package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleState;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PageState;
import rustleund.fightingfantasy.framework.closures.Closure;

public class DoBattleClosure implements Closure {

	private int id;

	public DoBattleClosure(Element element) {
		this.id = Integer.valueOf(element.getAttribute("id"));
	}

	public DoBattleClosure(int id) {
		this.id = id;
	}

	@Override
	public boolean execute(GameState gameState) {
		PageState pageState = gameState.getPageState();
		BattleState battleState = pageState.getBattle(id);

		gameState.setBattleInProgress(true);
		gameState.setBattleState(battleState);

		if (!battleState.battleIsOver()) {
			battleState.incrementGameState();
			if (battleState.getCurrentBattleMessage() != null) {
				pageState.replacePagetext(BattleState.START_STRING, BattleState.END_STRING, battleState.getCurrentBattleMessage());
			}
			if (battleState.getEnemies().areDead()) {
				gameState.getPlayerState().setNextBattleBattleEffects(null);
				gameState.setBattleInProgress(false);
				gameState.setBattleState(null);
				battleState.doEndBattle();
			}
			return true;
		}
		return false;
	}
}
