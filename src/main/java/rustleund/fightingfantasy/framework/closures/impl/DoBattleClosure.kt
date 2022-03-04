package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleState;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PageState;
import rustleund.fightingfantasy.framework.closures.Closure;

public class DoBattleClosure implements Closure {

	private int id;

	public DoBattleClosure(Element element) {
		this.id = Integer.parseInt(element.getAttribute("id"));
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

		if (battleState.battleIsNotOver()) {
			battleState.incrementGameState();
			battleState.doAfterPossibleStaminaChange();
			return true;
		}
		return false;
	}
}
