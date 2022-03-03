/*
 * Created on Oct 8, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.*;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

/**
 * @author rustlea
 */
public class AddEnemiesClosure implements Closure {

	private final List<EnemyState> enemiesToAdd;
	private final int battleId;
	private int waitTime;

	public AddEnemiesClosure(Element element, ClosureLoader closureLoader) {
		this.enemiesToAdd = new ArrayList<>();

		Element battleElement = (Element) element.getParentNode();
		while (!battleElement.getLocalName().equals("battle")) {
			battleElement = (Element) battleElement.getParentNode();
		}

		this.battleId = Integer.parseInt(battleElement.getAttribute("id"));
		this.waitTime = Integer.parseInt(element.getAttribute("wait"));

		XMLUtilKt.getChildElementsByName(element, "enemy").iterator()
				.forEachRemaining(enemyTag -> enemiesToAdd.add(new EnemyState(enemyTag, closureLoader)));
	}

	@Override
	public boolean execute(GameState gameState) {
		if (waitTime == 0) {
			BattleState battleState = gameState.getPageState().getBattle(battleId);
			Enemies enemies = battleState.getEnemies();
			for (EnemyState enemy : enemiesToAdd) {
				enemies.addEnemy(enemy);
			}
		}
		waitTime--;
		return true;
	}

}