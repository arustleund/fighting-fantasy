/*
 * Created on Mar 8, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author rustlea
 */
public class Enemies implements Iterable<EnemyState> {

	private List<EnemyState> enemies;

	public Enemies() {
		enemies = new ArrayList<>();
	}

	public void addEnemy(EnemyState enemy) {
		if (enemies == null) {
			enemies = new ArrayList<>();
		}
		enemies.add(enemy);
	}

	public boolean areDead() {
		for (EnemyState thisEnemy : enemies) {
			if (!thisEnemy.isDead()) {
				return false;
			}
		}
		return true;
	}

	public EnemyState getFirstNonDeadEnemy() {
		EnemyState result = null;

		boolean foundNonDead = false;
		Iterator<EnemyState> i = enemies.iterator();
		while (!foundNonDead && i.hasNext()) {
			EnemyState thisEnemy = i.next();
			if (!thisEnemy.isDead()) {
				result = thisEnemy;
				foundNonDead = true;
			}
		}

		return result;
	}

	public List<EnemyState> getEnemies() {
		return this.enemies;
	}

	@Override
	public Iterator<EnemyState> iterator() {
		return this.enemies.iterator();
	}

}
