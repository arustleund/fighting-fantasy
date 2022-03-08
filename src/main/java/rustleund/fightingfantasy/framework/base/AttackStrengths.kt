package rustleund.fightingfantasy.framework.base

import java.util.function.Supplier

fun createAttackStrengths(
    playerState: PlayerState,
    enemies: Enemies,
    fightEnemiesTogether: Boolean,
    diceRoller: Supplier<Int>
): AttackStrengths {
    val playerAttackStrength = getAttackStrengthFor(playerState, diceRoller)
    var enemyAttackStrengthsSequence = enemies.asSequence().withIndex()
        .filter { !it.value.isDead }
    if (!fightEnemiesTogether) enemyAttackStrengthsSequence = enemyAttackStrengthsSequence.take(1)
    val enemyAttackStrengths =
        enemyAttackStrengthsSequence.associateBy({ it.index }, { getAttackStrengthFor(it.value, diceRoller) })
    return AttackStrengths(playerAttackStrength, enemyAttackStrengths, enemies)
}

private fun getAttackStrengthFor(aState: AbstractEntityState, diceRoller: Supplier<Int>): AttackStrength {
    val dieRoll = diceRoller.get()
    val skill = aState.getSkill().currentValue
    val modifier = aState.getAttackStrengthModifier()
    return AttackStrength(dieRoll, skill, modifier)
}

class AttackStrengths(
    val playerAttackStrength: AttackStrength,
    private val enemyAttackStrengths: Map<Int, AttackStrength>,
    private val enemies: Enemies
) {

    private val highestEnemyAttackStrength = enemyAttackStrengths.maxOf { it.value }
    private val highestAttackStrength = playerAttackStrength.coerceAtLeast(highestEnemyAttackStrength)

    val playerWon = playerAttackStrength > highestEnemyAttackStrength
    val playerHit = playerAttackStrength < highestEnemyAttackStrength

    fun getEnemyAttackStrength(enemyId: Int) = enemyAttackStrengths.getOrDefault(enemyId, null)

    fun enemyHasHighestAttackStrength(enemyId: Int) =
        getEnemyAttackStrength(enemyId)?.let { it == highestAttackStrength } ?: false

    fun winningEnemyHasPoisonedWeapon(currentRound: Int) = playerHit &&
            enemyAttackStrengths.any { (enemyId, attackStrength) ->
                attackStrength == highestAttackStrength
                        && enemies.enemies[enemyId].poisonedWeaponRounds >= currentRound
            }
}
