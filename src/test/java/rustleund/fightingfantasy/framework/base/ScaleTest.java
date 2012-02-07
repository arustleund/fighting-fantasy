package rustleund.fightingfantasy.framework.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ScaleTest {

	@Test
	public void testAdjustCurrentValue() {
		Scale testee = new Scale(0, 0, 5, true);

		testee.adjustCurrentValue(2);

		assertEquals(2, testee.getCurrentValue());

		try {
			testee.adjustCurrentValue(4);
			fail();
		} catch (IndexOutOfBoundsException e) {
			// expected
		}

		assertEquals(5, testee.getCurrentValue());

		testee = new Scale(0, 1, 5, false);

		try {
			testee.adjustCurrentValue(6);
			fail();
		} catch (IndexOutOfBoundsException e) {
			// expected
		}

		assertEquals(1, testee.getCurrentValue());
		
		testee.adjustCurrentValue(-1);
		
		assertEquals(0, testee.getCurrentValue());
	}

	@Test
	public void testRestorePreviousValue() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdjustmentIsWithinBounds() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdjustUpperBound() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdjustCurrentValueNoException() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsFull() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsEmpty() {
		fail("Not yet implemented");
	}

}
