/*
 * Created on Mar 8, 2004
 */
package rustleund.nightdragon.framework;

import org.w3c.dom.Element;

/**
 * @author rustlea
 */
public class EnemyState extends AbstractEntityState {

	public EnemyState(Element enemyTag) {
		this.name = enemyTag.getAttribute("name");

		Integer skillInteger = new Integer(enemyTag.getAttribute("skill"));
		this.skill = new Scale(new Integer(0), skillInteger, skillInteger, true);

		Integer staminaInteger = new Integer(enemyTag.getAttribute("stamina"));
		this.stamina = new Scale(new Integer(0), staminaInteger, staminaInteger, true);
	}

}