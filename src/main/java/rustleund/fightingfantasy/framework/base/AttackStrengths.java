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

	private Multimap<Integer, AbstractEntityState> attackStrengthMap;
	private List<Integer> attackStrengths;
	private int highestAttackStrength;

	private AttackStrengths(Multimap<Integer, AbstractEntityState> attackStrengthMap, List<Integer> attackStrengths, int highestAttackStrength) {
		this.attackStrengthMap = attackStrengthMap;
		this.attackStrengths = attackStrengths;
		this.highestAttackStrength = highestAttackStrength;
	}

	public int getPlayerAttackStrength() {
		return this.attackStrengths.get(0);
	}

	/**
	 * @return <code>true</code> when the player has a higher attack strength than all enemies
	 */
	public boolean playerWon() {
		Collection<AbstractEntityState> winners = this.attackStrengthMap.get(this.highestAttackStrength);
		return winners.size() == 1 && winners.iterator().next() instanceof PlayerState;
	}

	public int getEnemyAttackStrength(int enemyId) {
		return this.attackStrengths.get(enemyId + 1);
	}

	/**
	 * @return <code>true</code> if at least one enemy has a higher attack strength than the player
	 */
	public boolean playerHit() {
		Collection<AbstractEntityState> winners = this.attackStrengthMap.get(this.highestAttackStrength);
		return Iterables.all(winners, (AbstractEntityState winner) -> {
			return winner instanceof EnemyState;
		});
	}

	/**
	 * @return <code>true</code> if the player was hit, and it was by at least one enemy with a poisoned weapon
	 */
	public boolean winningEnemyHasPoisonedWeapon() {
		if (playerHit()) {
			Collection<AbstractEntityState> winners = this.attackStrengthMap.get(this.highestAttackStrength);
			return Iterables.any(winners, (AbstractEntityState winner) -> {
				return winner instanceof EnemyState && ((EnemyState) winner).isPoisonedWeapon();
			});
		}
		return false;
	}

	public static AttackStrengths init(PlayerState playerState, Enemies enemies, boolean fightEnemiesTogether) {
		List<Integer> attackStrengths = new ArrayList<>(enemies.getEnemies().size() + 1);
		Multimap<Integer, AbstractEntityState> attackStrengthMap = ArrayListMultimap.create();
		int highestAttackStrength = getAttackStrengthFor(playerState);
		attackStrengths.add(highestAttackStrength);
		attackStrengthMap.put(highestAttackStrength, playerState);
		boolean addedAttackStrengthForEnemy = false;
		for (EnemyState enemy : enemies) {
			if (enemy.isDead()) {
				attackStrengths.add(-1);
			} else if (fightEnemiesTogether || !addedAttackStrengthForEnemy) {
				int attackStrengthForEnemy = getAttackStrengthFor(enemy);
				if (attackStrengthForEnemy > highestAttackStrength) {
					highestAttackStrength = attackStrengthForEnemy;
				}
				attackStrengths.add(attackStrengthForEnemy);
				attackStrengthMap.put(attackStrengthForEnemy, enemy);
				addedAttackStrengthForEnemy = true;
			} else {
				attackStrengths.add(-1);
			}
		}
		return new AttackStrengths(attackStrengthMap, attackStrengths, highestAttackStrength);
	}

	private static int getAttackStrengthFor(AbstractEntityState aState) {
		return aState.getAttackStrength() + DiceRoller.rollDice(2);
	}
}
