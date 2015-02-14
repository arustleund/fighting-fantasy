package rustleund.fightingfantasy.framework.base;


public class AttackStrength implements Comparable<AttackStrength> {

	private int dieRoll;
	private int skill;
	private int modifier;

	public AttackStrength(int dieRoll, int skill, int modifier) {
		this.dieRoll = dieRoll;
		this.skill = skill;
		this.modifier = modifier;
	}

	public int getTotal() {
		return dieRoll + skill + modifier;
	}

	public int getDieRoll() {
		return this.dieRoll;
	}

	public int getSkill() {
		return this.skill;
	}

	public int getModifier() {
		return this.modifier;
	}

	@Override
	public int compareTo(AttackStrength o) {
		return Integer.compare(getTotal(), o.getTotal());
	}

	@Override
	public boolean equals(Object obj) {
		return getTotal() == ((AttackStrength) obj).getTotal();
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(getTotal());
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getTotal());
		result.append(" (");
		result.append(this.skill);
		result.append(" skill + ");
		result.append(this.modifier);
		result.append(" modifier + ");
		result.append(this.dieRoll);
		result.append(" rolled)");
		return result.toString();
	}
}
