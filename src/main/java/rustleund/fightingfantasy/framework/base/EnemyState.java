/*
 * Created on Mar 8, 2004
 */
package rustleund.fightingfantasy.framework.base;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

/**
 * @author rustlea
 */
public class EnemyState extends AbstractEntityState {

	private ClosureLoader closureLoader;

	private boolean poisonedWeapon = false;
	private Closure enemyKilled;

	public EnemyState(Element enemyTag, ClosureLoader closureLoader) {
		this.closureLoader = closureLoader;

		this.name = enemyTag.getAttribute("name");

		Integer skillInteger = Integer.valueOf(enemyTag.getAttribute("skill"));
		this.skill = new Scale(0, skillInteger, skillInteger, true);

		Integer staminaInteger = Integer.valueOf(enemyTag.getAttribute("stamina"));
		this.stamina = new Scale(0, staminaInteger, staminaInteger, true);

		if (enemyTag.hasAttribute("poisonedWeapon")) {
			this.poisonedWeapon = Boolean.valueOf(enemyTag.getAttribute("poisonedWeapon"));
		}

		NodeList onKilledElements = enemyTag.getElementsByTagName("onKilled");
		if (onKilledElements.getLength() > 0) {
			this.enemyKilled = this.closureLoader.loadClosureFromChildren((Element) onKilledElements.item(0));
		}
	}

	public boolean isPoisonedWeapon() {
		return this.poisonedWeapon;
	}

	public Closure getEnemyKilled() {
		return this.enemyKilled;
	}
}