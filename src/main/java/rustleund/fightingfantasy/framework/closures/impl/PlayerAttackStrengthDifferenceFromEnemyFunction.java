package rustleund.fightingfantasy.framework.closures.impl;

import java.util.function.Function;

import rustleund.fightingfantasy.framework.base.AttackStrength;
import rustleund.fightingfantasy.framework.base.AttackStrengths;
import rustleund.fightingfantasy.framework.base.BattleState;
import rustleund.fightingfantasy.framework.base.GameState;

public class PlayerAttackStrengthDifferenceFromEnemyFunction implements Function<GameState, Integer> {

	@Override
	public Integer apply(GameState t) {
		BattleState battleState = t.getBattleState();
		AttackStrengths currentAttackStrengths = battleState.getCurrentAttackStrengths();
		int enemyIndex = battleState.getEnemies().getEnemies().indexOf(battleState.getEnemies().getFirstNonDeadEnemy());
		AttackStrength enemyAttackStrength = currentAttackStrengths.getEnemyAttackStrength(enemyIndex);
		return currentAttackStrengths.getPlayerAttackStrength().getTotal() - enemyAttackStrength.getTotal();
	}
}
