/*
 * Created on Oct 8, 2005
 */
package rustleund.nightdragon.framework.closures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.BattleState;
import rustleund.nightdragon.framework.Enemies;
import rustleund.nightdragon.framework.EnemyState;
import rustleund.nightdragon.framework.GameState;

/**
 * @author rustlea
 */
public class AddEnemiesClosure extends AbstractCommand {

	private List enemiesToAdd = null;

	private int battleId = 0;

	private int waitTime = 0;

	public AddEnemiesClosure(Element element) {
		this.executeSuccessful = true;
		this.enemiesToAdd = new ArrayList();

		Element battleElement = (Element) element.getParentNode().getParentNode().getParentNode();
		this.battleId = Integer.parseInt(battleElement.getAttribute("id"));

		this.waitTime = Integer.parseInt(element.getAttribute("wait"));

		NodeList enemyTags = element.getElementsByTagName("enemy");
		for (int i = 0; i < enemyTags.getLength(); i++) {
			Element enemyTag = (Element) enemyTags.item(i);
			enemiesToAdd.add(new EnemyState(enemyTag));
		}
	}

	public void execute(GameState gameState) {
		if (waitTime == 0) {
			BattleState battleState = gameState.getPageState().getBattle(battleId);
			Enemies enemies = battleState.getEnemies();
			for (Iterator iter = enemiesToAdd.iterator(); iter.hasNext();) {
				EnemyState enemy = (EnemyState) iter.next();
				enemies.addEnemy(enemy);
			}
		}
		waitTime--;
	}

}