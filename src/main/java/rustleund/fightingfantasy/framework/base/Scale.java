/*
 * Created on Mar 7, 2004
 */
package rustleund.fightingfantasy.framework.base;

import com.google.common.collect.Range;

/**
 * @author rustlea
 */
public class Scale {

	private int currentValue;
	private int previousValue;

	private Range<Integer> range;

	private boolean doOperationOnFail = false;

	public Scale(Integer lowerBound, Integer currentValue, Integer upperBound, boolean doOperationOnFail) {
		if (lowerBound == null && upperBound == null) {
			this.range = Range.all();
		} else if (lowerBound == null) {
			this.range = Range.atMost(upperBound);
		} else if (upperBound == null) {
			this.range = Range.atLeast(lowerBound);
		} else {
			this.range = Range.closed(lowerBound, upperBound);
		}

		if (currentValue == null) {
			this.currentValue = 0;
			this.previousValue = 0;
		} else {
			this.currentValue = currentValue;
			this.previousValue = currentValue;
		}
		this.doOperationOnFail = doOperationOnFail;
	}

	public void adjustCurrentValue(int amount) throws IndexOutOfBoundsException {
		int tempResult = currentValue + amount;
		if (adjustmentIsWithinBounds(tempResult)) {
			previousValue = currentValue;
			currentValue = tempResult;
		} else {
			if (doOperationOnFail) {
				previousValue = currentValue;
				if (amount > 0) {
					currentValue = this.range.upperEndpoint();
				} else {
					currentValue = this.range.lowerEndpoint();
				}
			}
			throw new IndexOutOfBoundsException();
		}
	}

	public void restorePreviousValue() {
		int oldPrevious = this.previousValue;
		this.previousValue = this.currentValue;
		this.currentValue = oldPrevious;
	}

	private boolean adjustmentIsWithinBounds(int tempResult) {
		return this.range.contains(tempResult);
	}

	public void adjustUpperBound(int amount) {
		int newUpperBound;
		if (this.range.hasUpperBound()) {
			newUpperBound = this.range.upperEndpoint() + amount;
		} else {
			newUpperBound = amount;
		}

		if (this.range.hasLowerBound()) {
			this.range = Range.closed(this.range.lowerEndpoint(), newUpperBound);
		} else {
			this.range = Range.atMost(newUpperBound);
		}

		if (currentValue > this.range.upperEndpoint()) {
			previousValue = currentValue;
			currentValue = this.range.upperEndpoint();
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
		return this.currentValue;
	}

	public Integer getUpperBound() {
		return this.range.hasUpperBound() ? this.range.upperEndpoint() : null;
	}

	public boolean isFull() {
		return this.range.hasUpperBound() && this.range.upperEndpoint().equals(this.currentValue);
	}

	public boolean isEmpty() {
		return this.currentValue == 0;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("" + this.currentValue);
		if (this.range.hasUpperBound()) {
			result.append("/");
			result.append(this.range.upperEndpoint());
		}
		return result.toString();
	}

}