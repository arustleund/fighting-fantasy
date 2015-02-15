package rustleund.fightingfantasy.framework.closures.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.Enemies;
import rustleund.fightingfantasy.framework.base.EnemyState;
import rustleund.fightingfantasy.framework.base.GameState;

import com.google.common.base.Predicate;

public class TestEnemyTypesPredicate implements Predicate<GameState> {

	private Collection<String> equalAny = new ArrayList<>();
	private boolean alwaysCheckFirst;
	private int enemyId;

	public TestEnemyTypesPredicate(Element element) {
		if (element.hasAttribute("equalAny")) {
			this.equalAny.addAll(Arrays.asList(element.getAttribute("equalAny").split(",")));
		}
		String configuredId = element.getAttribute("enemyId");
		if ("first".equals(configuredId)) {
			this.alwaysCheckFirst = true;
		} else {
			this.enemyId = Integer.valueOf(configuredId);
		}
	}

	@Override
	public boolean apply(GameState input) {
		Enemies enemies = input.getBattleState().getEnemies();
		EnemyState enemyStateToCheck = alwaysCheckFirst ? enemies.getFirstNonDeadEnemy() : enemies.getEnemies().get(enemyId);
		return equalAny.stream().anyMatch(enemyStateToCheck::isOfType);
	}
}
