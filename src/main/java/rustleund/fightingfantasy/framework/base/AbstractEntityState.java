/*
 * Created on Mar 8, 2004
 */
package rustleund.fightingfantasy.framework.base;

/**
 * @author rustlea
 */
public abstract class AbstractEntityState {

	protected String name;
	protected Scale skill;
	protected Scale stamina;
	protected int attackStrengthModifier = 0;
	protected int damageModifier = 0;

	protected AbstractEntityState() {
		// Does nothing
	}

	protected AbstractEntityState(String name, int skill, int stamina) {
		this.name = name;
		this.skill = new Scale(0, skill, skill, true);
		this.stamina = new Scale(0, stamina, stamina, true);
	}

	public int getAttackStrength() {
		return this.skill.getCurrentValue() + this.attackStrengthModifier;
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

	public int getDamageModifier() {
		return this.damageModifier;
	}

	public void setDamageModifier(int damageModifier) {
		this.damageModifier = damageModifier;
	}

	@Override
	public String toString() {
		return (name + ": SKILL: " + skill + " STAMINA: " + stamina);
	}

}