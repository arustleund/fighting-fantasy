package rustleund.fightingfantasy.framework.closures.impl;

import java.util.Optional;
import java.util.function.Function;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class TestNumberPredicate implements Predicate<GameState> {

	private static final ImmutableMap<String, Boolean[]> VALUES_MAPPINGS;
	static {
		Builder<String, Boolean[]> builder = ImmutableMap.builder();
		builder.put("lessThan", new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.FALSE });
		builder.put("greaterThan", new Boolean[] { Boolean.FALSE, Boolean.FALSE, Boolean.TRUE });
		builder.put("atLeast", new Boolean[] { Boolean.FALSE, Boolean.TRUE, Boolean.TRUE });
		builder.put("atMost", new Boolean[] { Boolean.TRUE, Boolean.TRUE, Boolean.FALSE });
		builder.put("equal", new Boolean[] { Boolean.FALSE, Boolean.TRUE, Boolean.FALSE });
		builder.put("notEqual", new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.TRUE });
		VALUES_MAPPINGS = builder.build();
	}

	private Boolean[] acceptableValues;
	private int valueToCompare;
	private Function<? super GameState, ? extends Integer> gameStateToNumberFunction;

	public TestNumberPredicate(Element element, Function<? super GameState, ? extends Integer> gameStateToNumberFunction) {
		Optional<String> firstOperator = VALUES_MAPPINGS.keySet().stream().filter(element::hasAttribute).findFirst();
		if (firstOperator.isPresent()) {
			String operator = firstOperator.get();
			this.acceptableValues = VALUES_MAPPINGS.get(operator);
			this.valueToCompare = Integer.valueOf(element.getAttribute(operator));
		} else {
			this.acceptableValues = VALUES_MAPPINGS.get("atLeast");
			this.valueToCompare = 1;
		}
		this.gameStateToNumberFunction = gameStateToNumberFunction;
	}

	@Override
	public boolean apply(GameState input) {
		boolean valueIsAcceptable = false;
		int compareResult = this.gameStateToNumberFunction.apply(input).compareTo(this.valueToCompare);

		valueIsAcceptable |= acceptableValues[0] && (compareResult < 0);
		valueIsAcceptable |= acceptableValues[1] && (compareResult == 0);
		valueIsAcceptable |= acceptableValues[2] && (compareResult > 0);

		return valueIsAcceptable;
	}
}
