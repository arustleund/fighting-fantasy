package rustleund.fightingfantasy.framework.base;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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

		testee.adjustCurrentValue(2);

		assertEquals(2, testee.getCurrentValue());

		testee = new Scale(0, 0, null, false);

		testee.adjustCurrentValue(1000);

		assertEquals(1000, testee.getCurrentValue());
	}

	@Test
	public void testRestorePreviousValue() {
		Scale testee = new Scale(0, 0, 20, true);

		assertEquals(0, testee.getCurrentValue());

		testee.adjustCurrentValue(5);

		assertEquals(5, testee.getCurrentValue());

		testee.restorePreviousValue();

		assertEquals(0, testee.getCurrentValue());

		testee.adjustCurrentValue(4);
		try {
			testee.adjustCurrentValue(20);
		} catch (IndexOutOfBoundsException e) {
			// expected
		}
		assertEquals(20, testee.getCurrentValue());

		testee.restorePreviousValue();

		assertEquals(4, testee.getCurrentValue());

		testee = new Scale(0, 4, 20, false);

		testee.adjustCurrentValue(2);
		try {
			testee.adjustCurrentValue(20);
		} catch (IndexOutOfBoundsException e) {
			// expected
		}
		assertEquals(6, testee.getCurrentValue());
		testee.restorePreviousValue();
		assertEquals(4, testee.getCurrentValue());

		testee = new Scale(0, 4, 20, false);
		testee.adjustCurrentValue(2);
		testee.adjustCurrentValue(3);
		testee.restorePreviousValue();
		assertEquals(6, testee.getCurrentValue());
	}

	@Test
	public void testAdjustUpperBound() {
		Scale testee = new Scale(0, 4, 20, false);

		testee.adjustUpperBound(2);
		assertEquals(22, Objects.requireNonNull(testee.getUpperBound()).intValue());

		testee.adjustUpperBound(-4);
		assertEquals(18, testee.getUpperBound().intValue());

		testee = new Scale(0, 4, null, false);

		testee.adjustUpperBound(2);
		assertEquals(2, Objects.requireNonNull(testee.getUpperBound()).intValue());
		assertEquals(2, testee.getCurrentValue());

		testee = new Scale(0, 4, 6, false);
		testee.adjustUpperBound(-3);
		assertEquals(3, Objects.requireNonNull(testee.getUpperBound()).intValue());
		assertEquals(3, testee.getCurrentValue());
	}

	@Test
	public void testAdjustCurrentValueNoException() {
		Scale testee = new Scale(0, 0, 5, true);

		testee.adjustCurrentValueNoException(2);

		assertEquals(2, testee.getCurrentValue());

		testee.adjustCurrentValueNoException(4);

		assertEquals(5, testee.getCurrentValue());

		testee = new Scale(0, 1, 5, false);

		testee.adjustCurrentValueNoException(6);

		assertEquals(1, testee.getCurrentValue());

		testee.adjustCurrentValueNoException(-1);

		assertEquals(0, testee.getCurrentValue());

		testee.adjustCurrentValueNoException(2);

		assertEquals(2, testee.getCurrentValue());

		testee = new Scale(0, 0, null, false);

		testee.adjustCurrentValueNoException(1000);

		assertEquals(1000, testee.getCurrentValue());
	}

	@Test
	public void testIsFull() {
		Scale testee = new Scale(0, 0, 10, false);
		assertFalse(testee.isFull());

		testee = new Scale(0, 0, null, false);
		assertFalse(testee.isFull());

		testee = new Scale(null, 0, null, false);
		assertFalse(testee.isFull());

		testee = new Scale(null, null, null, false);
		assertFalse(testee.isFull());

		testee = new Scale(null, 1000, null, false);
		assertFalse(testee.isFull());

		testee = new Scale(1000, 1000, 1000, false);
		assertTrue(testee.isFull());

		testee = new Scale(0, 1000, 1000, false);
		assertTrue(testee.isFull());
	}

	@Test
	public void testIsEmpty() {
		Scale testee = new Scale(0, 0, 10, false);
		assertTrue(testee.isEmpty());

		testee = new Scale(0, 0, null, false);
		assertTrue(testee.isEmpty());

		testee = new Scale(null, 0, null, false);
		assertTrue(testee.isEmpty());

		testee = new Scale(null, null, null, false);
		assertTrue(testee.isEmpty());

		testee = new Scale(null, 1000, null, false);
		assertFalse(testee.isEmpty());

		testee = new Scale(1000, 1000, 1000, false);
		assertFalse(testee.isEmpty());

		testee = new Scale(0, 1000, 1000, false);
		assertFalse(testee.isEmpty());

		testee = new Scale(-1000, -1, 1000, false);
		assertFalse(testee.isEmpty());
	}

	@Test
	public void testToString() {
		Scale testee = new Scale(0, 0, 10, false);
		assertEquals("0/10", testee.toString());

		testee = new Scale(null, 0, 10, false);
		assertEquals("0/10", testee.toString());

		testee = new Scale(null, 0, null, false);
		assertEquals("0", testee.toString());

		testee = new Scale(0, 5, 12, false);
		assertEquals("5/12", testee.toString());

		testee = new Scale(null, 5, 12, false);
		assertEquals("5/12", testee.toString());

		testee = new Scale(null, 5, null, false);
		assertEquals("5", testee.toString());
	}

}
