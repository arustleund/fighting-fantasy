package rustleund.fightingfantasy.framework.closures.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.AbstractEntityState;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.Scale;

public class TestStatPredicate implements Predicate<GameState> {

	private static final Map<String, List<Boolean>> VALUES_MAPPINGS = new HashMap<>();

	static {
		VALUES_MAPPINGS.put("lessThan", Arrays.asList(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE));
		VALUES_MAPPINGS.put("greaterThan", Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE));
		VALUES_MAPPINGS.put("atLeast", Arrays.asList(Boolean.FALSE, Boolean.TRUE, Boolean.TRUE));
		VALUES_MAPPINGS.put("atMost", Arrays.asList(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE));
		VALUES_MAPPINGS.put("equal", Arrays.asList(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE));
		VALUES_MAPPINGS.put("notEqual", Arrays.asList(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE));
	}

	private List<Boolean> acceptableValues;
	private Integer valueToCompare;
	private final String stat;
	private final boolean useInitialValue;

	public TestStatPredicate(Element element) {
		this.stat = element.getAttribute("stat");

		for (String attributeTest : VALUES_MAPPINGS.keySet()) {
			if (element.hasAttribute(attributeTest)) {
				this.acceptableValues = VALUES_MAPPINGS.get(attributeTest);
				this.valueToCompare = Integer.valueOf(element.getAttribute(attributeTest));
				break;
			}
		}

		this.useInitialValue = element.hasAttribute("useInitialValue") && "true".equals(element.getAttribute("useInitialValue"));
	}

	@Override
	public boolean test(GameState gameState) {
		try {
			AbstractEntityState entityStateToTest = getEntityStateToTest(gameState);

			Integer statValue = getStatValue(entityStateToTest, gameState);

			int compareResult = statValue.compareTo(this.valueToCompare);

			boolean valueIsAcceptable = acceptableValues.get(0) && (compareResult < 0);
			valueIsAcceptable |= acceptableValues.get(1) && (compareResult == 0);
			valueIsAcceptable |= acceptableValues.get(2) && (compareResult > 0);

			if (valueIsAcceptable) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private Integer getStatValue(AbstractEntityState entityStateToTest, GameState gameState) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if ("attackStrength".equals(this.stat)) {
			return getAttackStrength(gameState);
		}
		if ("hitCount".equals(this.stat)) {
			return getHitCount(gameState);
		}
		if ("battleRound".equals(this.stat)) {
			return gameState.getBattleState().getBattleRound();
		}
		return getNonAttackStrengthStatValue(entityStateToTest);
	}

	private Integer getNonAttackStrengthStatValue(AbstractEntityState entityStateToTest) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Scale statScale = (Scale) PropertyUtils.getProperty(entityStateToTest, this.stat);
		if (this.useInitialValue) {
			return statScale.getUpperBound();
		}
		return statScale.getCurrentValue();
	}

	protected int getAttackStrength(GameState gameState) {
		return gameState.getBattleState().getCurrentAttackStrengths().getPlayerAttackStrength().getTotal();
	}

	protected int getHitCount(GameState gameState) {
		return gameState.getBattleState().getPlayerHitCount();
	}

	protected AbstractEntityState getEntityStateToTest(GameState gameState) {
		return gameState.getPlayerState();
	}
}
