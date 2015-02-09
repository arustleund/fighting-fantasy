package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.AbstractEntityState;
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.Enemies;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

public class AdjustEnemyScaleClosure extends AdjustScaleClosure {

	private int enemyId;

	public AdjustEnemyScaleClosure(Element element, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		super(element, closureLoader, battleEffectsLoader);
		if (element.hasAttribute("enemyId")) {
			this.enemyId = Integer.valueOf(element.getAttribute("enemyId"));
		} else {
			this.enemyId = -1;
		}
	}

	@Override
	protected AbstractEntityState entity(GameState gameState) {
		Enemies enemies = gameState.getBattleState().getEnemies();
		return this.enemyId >= 0 && this.enemyId < enemies.getEnemies().size() ? enemies.getEnemies().get(this.enemyId) : enemies.getFirstNonDeadEnemy();
	}
}
