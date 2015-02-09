/*
 * Created on Oct 8, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.base.BattleState;
import rustleund.fightingfantasy.framework.base.Enemies;
import rustleund.fightingfantasy.framework.base.EnemyState;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

/**
 * @author rustlea
 */
public class AddEnemiesClosure extends AbstractClosure {

	private List<EnemyState> enemiesToAdd;
	private int battleId;
	private int waitTime;

	public AddEnemiesClosure(Element element, ClosureLoader closureLoader) {
		this.enemiesToAdd = new ArrayList<>();

		Element battleElement = (Element) element.getParentNode();
		while (!battleElement.getNodeName().equals("battle")) {
			battleElement = (Element) battleElement.getParentNode();
		}

		this.battleId = Integer.parseInt(battleElement.getAttribute("id"));
		this.waitTime = Integer.parseInt(element.getAttribute("wait"));

		NodeList enemyTags = element.getElementsByTagName("enemy");
		for (int i = 0; i < enemyTags.getLength(); i++) {
			Element enemyTag = (Element) enemyTags.item(i);
			enemiesToAdd.add(new EnemyState(enemyTag, closureLoader));
		}
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