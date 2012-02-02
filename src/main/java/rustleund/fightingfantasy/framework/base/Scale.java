/*
 * Created on Mar 7, 2004
 */
package rustleund.fightingfantasy.framework.base;

/**
 * @author rustlea
 */
public class Scale {

	private Integer lowerBound = null;

	private Integer currentValue = null;

	private Integer previousValue = null;

	private Integer upperBound = null;

	private boolean doOperationOnFail = false;

	public Scale(Integer lowerBound, Integer currentValue, Integer upperBound, boolean doOperationOnFail) {
		this.lowerBound = lowerBound;
		if (currentValue == null) {
			this.currentValue = new Integer(0);
			this.previousValue = new Integer(0);
		} else {
			this.currentValue = currentValue;
			this.previousValue = currentValue;
		}
		this.upperBound = upperBound;
		this.doOperationOnFail = doOperationOnFail;
	}

	public void adjustCurrentValue(int amount) throws IndexOutOfBoundsException {
		int tempResult = currentValue.intValue() + amount;
		if (adjustmentIsWithinBounds(amount, tempResult)) {
			previousValue = currentValue;
			currentValue = new Integer(tempResult);
		} else {
			if (doOperationOnFail) {
				previousValue = currentValue;
				if (amount > 0) {
					currentValue = new Integer(upperBound.intValue());
				} else {
					currentValue = new Integer(lowerBound.intValue());
				}
			}
			throw new IndexOutOfBoundsException();
		}
	}

	public void restorePreviousValue() {
		Integer oldPrevious = this.previousValue;
		this.previousValue = this.currentValue;
		this.currentValue = oldPrevious;
	}

	public boolean adjustmentIsWithinBounds(int amount, int tempResult) {
		return (upperBound == null && amount >= 0) || (amount <= 0 && lowerBound == null) || (upperBound != null && amount >= 0 && tempResult <= upperBound.intValue()) || (lowerBound != null && amount <= 0 && tempResult >= lowerBound.intValue());
	}

	public void adjustUpperBound(int amount) {
		if (upperBound == null) {
			// Assume null means zero in this case
			upperBound = new Integer(amount);
		} else {
			upperBound = new Integer(upperBound.intValue() + amount);
		}
		if (currentValue.intValue() > upperBound.intValue()) {
			previousValue = currentValue;
			currentValue = new Integer(upperBound.intValue());
		}
	}

	/**
	 * Utility method that automatically swallows the Exception for you.
	 * 
	 * @param amount
	 */
	public void adjustCurrentValueNoException(int amount) {
		try {
			this.adjustCurrentValue(amount);
		} catch (IndexOutOfBoundsException ioobe) {
			// Swallow exception
		}
	}

	public int getCurrentValue() {
		return currentValue.intValue();
	}

	public Integer getLowerBound() {
		return lowerBound;
	}

	public Integer getUpperBound() {
		return upperBound;
	}

	public boolean isFull() {
		return currentValue.equals(upperBound);
	}

	public boolean isEmpty() {
		return currentValue.intValue() == 0;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(currentValue.toString());
		if (upperBound != null) {
			result.append("/");
			result.append(upperBound.toString());
		}
		return result.toString();
	}

}