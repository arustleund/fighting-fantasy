package rustleund.fightingfantasy.framework.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rustleund.fightingfantasy.framework.util.DiceRoller;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Represents the current Attack Strengths for the current round for a Battle.
 */
public class AttackStrengths {

	private Multimap<AttackStrength, AbstractEntityState> attackStrengthMap;
	private List<AttackStrength> attackStrengths;
	private AttackStrength highestAttackStrength;

	private AttackStrengths(Multimap<AttackStrength, AbstractEntityState> attackStrengthMap, List<AttackStrength> attackStrengths, AttackStrength highestAttackStrength) {
		this.attackStrengthMap = attackStrengthMap;
		this.attackStrengths = attackStrengths;
		this.highestAttackStrength = highestAttackStrength;
	}

	public AttackStrength getPlayerAttackStrength() {
		return this.attackStrengths.get(0);
	}

	/**
	 * @return <code>true</code> when the player has a higher attack strength than all enemies
	 */
	public boolean playerWon() {
		Collection<AbstractEntityState> winners = this.attackStrengthMap.get(this.highestAttackStrength);
		return winners.size() == 1 && winners.iterator().next() instanceof PlayerState;
	}

	public AttackStrength getEnemyAttackStrength(int enemyId) {
		return this.attackStrengths.get(enemyId + 1);
	}

	/**
	 * @return <code>true</code> if Player has the highest attack strength among all participants (including ties), <code>false</code> otherwise
	 */
	public boolean playerHasHighestAttackStrength() {
		return this.highestAttackStrength.getTotal() == getPlayerAttackStrength().getTotal();
	}

	/**
	 * @return <code>true</code> if the Enemy with the given ID has the highest attack strength among all participants (including ties), <code>false</code> otherwise
	 */
	public boolean enemyHasHighestAttackStrength(int enemyId) {
		return this.highestAttackStrength.getTotal() == this.getEnemyAttackStrength(enemyId).getTotal();
	}

	/**
	 * @return <code>true</code> if at least one enemy has a higher attack strength than the player
	 */
	public boolean playerHit() {
		Collection<AbstractEntityState> winners = this.attackStrengthMap.get(this.highestAttackStrength);
		return Iterables.all(winners, winner -> {
			return winner instanceof EnemyState;
		});
	}

	/**
	 * @param currentRound The current battle round (1-indexed)
	 * @return <code>true</code> if the player was hit, and it was by at least one enemy with a poisoned weapon
	 */
	public boolean winningEnemyHasPoisonedWeapon(int currentRound) {
		if (playerHit()) {
			Collection<AbstractEntityState> winners = this.attackStrengthMap.get(this.highestAttackStrength);
			return winners.stream().anyMatch(winner -> winner instanceof EnemyState && ((EnemyState) winner).getPoisonedWeaponRounds() >= currentRound);
		}
		return false;
	}

	public static AttackStrengths init(PlayerState playerState, Enemies enemies, boolean fightEnemiesTogether) {
		List<AttackStrength> attackStrengths = new ArrayList<>(enemies.getEnemies().size() + 1);
		Multimap<AttackStrength, AbstractEntityState> attackStrengthMap = ArrayListMultimap.create();
		AttackStrength highestAttackStrength = getAttackStrengthFor(playerState);
		attackStrengths.add(highestAttackStrength);
		attackStrengthMap.put(highestAttackStrength, playerState);
		boolean addedAttackStrengthForEnemy = false;
		for (EnemyState enemy : enemies) {
			if (enemy.isDead()) {
				attackStrengths.add(null);
			} else if (fightEnemiesTogether || !addedAttackStrengthForEnemy) {
				AttackStrength attackStrengthForEnemy = getAttackStrengthFor(enemy);
				if (attackStrengthForEnemy.compareTo(highestAttackStrength) > 0) {
					highestAttackStrength = attackStrengthForEnemy;
				}
				attackStrengths.add(attackStrengthForEnemy);
				attackStrengthMap.put(attackStrengthForEnemy, enemy);
				addedAttackStrengthForEnemy = true;
			} else {
				attackStrengths.add(null);
			}
		}
		return new AttackStrengths(attackStrengthMap, attackStrengths, highestAttackStrength);
	}

	private static AttackStrength getAttackStrengthFor(AbstractEntityState aState) {
		int dieRoll = DiceRoller.rollDice(2);
		int skill = aState.getSkill().getCurrentValue();
		int modifier = aState.getAttackStrengthModifier();
		return new AttackStrength(dieRoll, skill, modifier);
	}
}
