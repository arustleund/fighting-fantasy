package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.AbstractEntityState;
import rustleund.fightingfantasy.framework.base.GameState;

public class TestEnemyStatPredicate extends TestStatPredicate {

	private int enemyId;

	public TestEnemyStatPredicate(Element element) {
		super(element);
		this.enemyId = Integer.valueOf(element.getAttribute("enemyId"));
	}

	@Override
	protected AbstractEntityState getEntityStateToTest(GameState gameState) {
		return gameState.getBattleState().getEnemies().getEnemies().get(enemyId);
	}
}
