/*
 * Created on Mar 8, 2004
 */
package rustleund.fightingfantasy.framework.base;

import org.w3c.dom.Element;

/**
 * @author rustlea
 */
public class EnemyState extends AbstractEntityState {

	private boolean poisonedWeapon = false;

	public EnemyState(Element enemyTag) {
		this.name = enemyTag.getAttribute("name");

		Integer skillInteger = new Integer(enemyTag.getAttribute("skill"));
		this.skill = new Scale(new Integer(0), skillInteger, skillInteger, true);

		Integer staminaInteger = new Integer(enemyTag.getAttribute("stamina"));
		this.stamina = new Scale(new Integer(0), staminaInteger, staminaInteger, true);

		if (enemyTag.hasAttribute("poisonedWeapon")) {
			this.poisonedWeapon = Boolean.valueOf(enemyTag.getAttribute("poisonedWeapon"));
		}
	}

	public boolean isPoisonedWeapon() {
		return this.poisonedWeapon;
	}

}