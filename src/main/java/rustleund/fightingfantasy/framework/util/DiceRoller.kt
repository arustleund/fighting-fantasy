/*
 * Created on Mar 10, 2004
 */
package rustleund.fightingfantasy.framework.util

object DiceRoller {

    @JvmStatic
    fun rollOneDie() = (Math.random() * 6.0).toInt() + 1

    @JvmStatic
    fun rollDice(numberOfDie: Int) = roll(numberOfDie).sum()

    private fun roll(numberOfDie: Int) = generateSequence { rollOneDie() }.take(numberOfDie)

    @JvmStatic
    fun rollDiceResult(numberOfDie: Int) = roll(numberOfDie).toList()
}