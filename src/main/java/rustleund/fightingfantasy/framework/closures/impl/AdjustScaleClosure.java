/*
 * Created on Jun 27, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.AbstractEntityState;
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.Scale;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

/**
 * @author rustlea
 */
public class AdjustScaleClosure extends AbstractClosure {

	private ClosureLoader closureLoader;
	private BattleEffectsLoader battleEffectsLoader;

	private String scaleName = null;
	private String stringAmount;
	private boolean promptOnFail;
	private boolean useAmountAsValue;
	private boolean useAmountAsPercent;
	private String round;
	private boolean adjustInitialValue;

	public AdjustScaleClosure(Element element, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		this.closureLoader = closureLoader;
		this.battleEffectsLoader = battleEffectsLoader;

		this.scaleName = element.getAttribute("type");
		this.stringAmount = element.getAttribute("amount");
		this.promptOnFail = attributeValue(element, "promptOnFail");
		this.useAmountAsValue = attributeValue(element, "useAmountAsValue");
		this.useAmountAsPercent = attributeValue(element, "useAmountAsPercent");
		this.round = element.getAttribute("round");
		this.adjustInitialValue = attributeValue(element, "adjustInitialValue");
	}

	@Override
	public boolean execute(GameState gameState) {
		Scale scale = null;

		try {
			scale = (Scale) PropertyUtils.getProperty(entity(gameState), scaleName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		int amountToAdjust;

		if (useAmountAsPercent) {
			if (scale.getUpperBound() == null) {
				throw new IllegalArgumentException("Scale must have an upper bound");
			}
			double percentAdjustment = Double.parseDouble(this.stringAmount);
			double percentResult = scale.getUpperBound().intValue() * percentAdjustment;
			if ("up".equalsIgnoreCase(this.round)) {
				amountToAdjust = (int) Math.ceil(percentResult);
			} else if ("down".equalsIgnoreCase(this.round)) {
				amountToAdjust = (int) Math.floor(percentResult);
			} else {
				amountToAdjust = (int) percentResult;
			}
		} else {
			amountToAdjust = Integer.parseInt(this.stringAmount);
			if (this.useAmountAsValue) {
				if (this.adjustInitialValue) {
					if (scale.getUpperBound() != null) {
						amountToAdjust -= scale.getUpperBound().intValue();
					}
				} else {
					amountToAdjust -= scale.getCurrentValue();
				}
			}
		}

		if (promptOnFail) {
			try {
				scale.adjustCurrentValue(amountToAdjust);
				return true;
			} catch (IndexOutOfBoundsException e1) {
				gameState.setMessage("You cannot perform this action");
				return false;
			}
		}
		if (this.adjustInitialValue) {
			scale.adjustUpperBound(amountToAdjust);
		} else {
			scale.adjustCurrentValueNoException(amountToAdjust);
		}

		if (gameState.getPlayerState().isDead()) {
			new LinkClosure("0", closureLoader, battleEffectsLoader).execute(gameState);
		}

		return true;

	}

	protected AbstractEntityState entity(GameState gameState) {
		return gameState.getPlayerState();
	}

}