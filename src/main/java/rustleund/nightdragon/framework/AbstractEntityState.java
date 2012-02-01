/*
 * Created on Mar 8, 2004
 */
package rustleund.nightdragon.framework;

/**
 * @author rustlea
 */
public abstract class AbstractEntityState {

	protected String name = null;

	protected Scale skill = null;

	protected Scale stamina = null;

	protected int attackStrengthModifier = 0;

	public AbstractEntityState() {
		// Does nothing
	}

	protected AbstractEntityState(String name, int skill, int stamina) {
		this.name = name;
		this.skill = new Scale(new Integer(0), new Integer(skill), new Integer(skill), true);
		this.stamina = new Scale(new Integer(0), new Integer(stamina), new Integer(stamina), true);
	}

	public int getBaseAttackStrength() {
		return skill.getCurrentValue() + attackStrengthModifier;
	}

	public boolean isDead() {
		return stamina.isEmpty();
	}

	public void takeHit(int hitPoints) {
		stamina.adjustCurrentValueNoException(-hitPoints);
	}

	/**
	 * @return
	 */
	public Scale getStamina() {
		return stamina;
	}

	/**
	 * @param scale
	 */
	public void setStamina(Scale scale) {
		stamina = scale;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public Scale getSkill() {
		return skill;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param scale
	 */
	public void setSkill(Scale scale) {
		skill = scale;
	}

	/**
	 * @return
	 */
	public int getAttackStrengthModifier() {
		return attackStrengthModifier;
	}

	/**
	 * @param i
	 */
	public void setAttackStrengthModifier(int i) {
		attackStrengthModifier = i;
	}

	public String toString() {
		return (name + ": SKILL: " + skill + " STAMINA: " + stamina);
	}

}