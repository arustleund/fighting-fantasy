package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractEntityState;
import rustleund.nightdragon.framework.Enemies;
import rustleund.nightdragon.framework.GameState;

public class AdjustEnemyScaleClosure extends AdjustScaleClosure {

	private int enemyId;

	public AdjustEnemyScaleClosure(Element element) {
		super(element);
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
