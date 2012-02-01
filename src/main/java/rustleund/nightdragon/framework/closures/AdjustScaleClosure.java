/*
 * Created on Jun 27, 2004
 */
package rustleund.nightdragon.framework.closures;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.AbstractEntityState;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.Scale;

/**
 * @author rustlea
 */
public class AdjustScaleClosure extends AbstractCommand {

	private String scaleName = null;
	private String amount;
	private boolean promptOnFail;
	private boolean useAmountAsValue;
	private boolean useAmountAsPercent;
	private String round;
	private boolean adjustInitialValue;

	public AdjustScaleClosure(Element element) {
		this.scaleName = element.getAttribute("type");
		this.amount = element.getAttribute("amount");
		this.promptOnFail = attributeValue(element, "promptOnFail");
		this.useAmountAsValue = attributeValue(element, "useAmountAsValue");
		this.useAmountAsPercent = attributeValue(element, "useAmountAsPercent");
		this.round = element.getAttribute("round");
		this.adjustInitialValue = attributeValue(element, "adjustInitialValue");
		this.executeSuccessful = false;
	}

	public void execute(GameState gameState) {
		Scale scale = null;

		try {
			scale = (Scale) PropertyUtils.getProperty(entity(gameState), scaleName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int amountToAdjust;

		if (useAmountAsPercent) {
			if (scale.getUpperBound() == null) {
				throw new IllegalArgumentException("Scale must have an upper bound");
			}
			double percentAdjustment = Double.parseDouble(this.amount);
			double percentResult = scale.getUpperBound().intValue() * percentAdjustment;
			if ("up".equalsIgnoreCase(this.round)) {
				amountToAdjust = (int) Math.ceil(percentResult);
			} else if ("down".equalsIgnoreCase(this.round)) {
				amountToAdjust = (int) Math.floor(percentResult);
			} else {
				amountToAdjust = (int) percentResult;
			}
		} else {
			amountToAdjust = Integer.parseInt(this.amount);
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
				this.executeSuccessful = true;
			} catch (IndexOutOfBoundsException e1) {
				gameState.setMessage("You cannot perform this action");
				this.executeSuccessful = false;
			}
		} else {
			if (this.adjustInitialValue) {
				scale.adjustUpperBound(amountToAdjust);
			} else {
				scale.adjustCurrentValueNoException(amountToAdjust);
			}
			this.executeSuccessful = true;
		}

	}

	protected AbstractEntityState entity(GameState gameState) {
		return gameState.getPlayerState();
	}

}