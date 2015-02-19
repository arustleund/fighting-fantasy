package rustleund.fightingfantasy.framework.closures.impl;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

public class AdjustByAmountClosure implements Closure {

	private int amount = 0;
	private Function<? super GameState, ? extends Integer> currentValueGetter;
	private BiConsumer<? super GameState, ? super Integer> valueSetter;

	public AdjustByAmountClosure(Element element, Function<? super GameState, ? extends Integer> currentValueGetter, BiConsumer<? super GameState, ? super Integer> valueSetter) {
		if (element.hasAttribute("amount")) {
			this.amount = Integer.valueOf(element.getAttribute("amount"));
		}
		this.currentValueGetter = currentValueGetter;
		this.valueSetter = valueSetter;
	}

	@Override
	public boolean execute(GameState gameState) {
		int currentValue = this.currentValueGetter.apply(gameState);
		this.valueSetter.accept(gameState, currentValue + this.amount);
		return true;
	}
}
