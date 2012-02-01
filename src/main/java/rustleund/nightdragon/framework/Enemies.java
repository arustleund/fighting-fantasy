/*
 * Created on Mar 8, 2004
 */
package rustleund.nightdragon.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author rustlea
 */
public class Enemies {

	protected boolean fightTogether = false;
	protected List<EnemyState> enemies = null;

	public Enemies() {
		enemies = new ArrayList<EnemyState>();
	}

	public void addEnemy(EnemyState enemy) {
		if (enemies == null) {
			enemies = new ArrayList<EnemyState>();
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

}
