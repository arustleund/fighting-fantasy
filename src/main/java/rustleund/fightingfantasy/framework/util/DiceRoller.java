/*
 * Created on Mar 10, 2004
 */
package rustleund.fightingfantasy.framework.util;

/**
 * @author rustlea
 */
public class DiceRoller {

	public static int rollOneDie() {
		return rollDice(1);
	}

	public static int rollDice(int numberOfDie) {
		int result = 0;
		for (int i = 0; i < numberOfDie; i++) {
			result += (((int) (Math.random() * 6.)) + 1);
		}
		return result;
	}

}