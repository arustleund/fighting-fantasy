package rustleund.fightingfantasy.framework.closures.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.base.Scale;

import com.google.common.base.Predicate;

public class TestStatPredicate implements Predicate<GameState> {

	private static final Map<String, List<Boolean>> VALUES_MAPPINGS = new HashMap<>();

	static {
		VALUES_MAPPINGS.put("lessThan", Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.FALSE }));
		VALUES_MAPPINGS.put("greaterThan", Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.FALSE, Boolean.TRUE }));
		VALUES_MAPPINGS.put("atLeast", Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE, Boolean.TRUE }));
		VALUES_MAPPINGS.put("atMost", Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.TRUE, Boolean.FALSE }));
		VALUES_MAPPINGS.put("equal", Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE, Boolean.FALSE }));
		VALUES_MAPPINGS.put("notEqual", Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.TRUE }));
	}

	private List<Boolean> acceptableValues;
	private Integer valueToCompare;
	private String stat;
	private boolean useInitialValue;

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
	public boolean apply(GameState gameState) {
		try {
			PlayerState playerState = gameState.getPlayerState();

			Scale statScale = (Scale) PropertyUtils.getProperty(playerState, this.stat);
			Integer statValue;
			if (this.useInitialValue) {
				statValue = statScale.getUpperBound();
			} else {
				statValue = statScale.getCurrentValue();
			}

			boolean valueIsAcceptable = false;
			int compareResult = statValue.compareTo(this.valueToCompare);

			valueIsAcceptable |= acceptableValues.get(0) && (compareResult < 0);
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

}
