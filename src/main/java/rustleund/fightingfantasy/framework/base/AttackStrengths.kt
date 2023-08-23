package rustleund.fightingfantasy.framework.base

fun createAttackStrengths(
    playerState: PlayerState,
    enemies: Enemies,
    fightEnemiesTogether: Boolean,
    diceRoller: () -> List<Int>
): AttackStrengths {
    val playerAttackStrength = getAttackStrengthFor(playerState, diceRoller)
    var enemyAttackStrengthsSequence = enemies.asSequence().withIndex()
        .filter { !it.value.isDead }
    if (!fightEnemiesTogether) enemyAttackStrengthsSequence = enemyAttackStrengthsSequence.take(1)
    val enemyAttackStrengths =
        enemyAttackStrengthsSequence.associateBy({ it.index }, { getAttackStrengthFor(it.value, diceRoller) })
    return AttackStrengths(playerAttackStrength, enemyAttackStrengths, enemies)
}

private fun getAttackStrengthFor(aState: AbstractEntityState, diceRoller: () -> List<Int>): AttackStrength {
    val dieRoll = diceRoller()
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
    val highestAttackStrength = playerAttackStrength.coerceAtLeast(highestEnemyAttackStrength)

    val playerWon = playerAttackStrength > highestEnemyAttackStrength
    val playerHit = playerAttackStrength < highestEnemyAttackStrength

    fun getEnemyAttackStrength(enemyIdx: Int) = enemyAttackStrengths.getOrDefault(enemyIdx, null)

    fun enemyHasHighestAttackStrength(enemyIdx: Int) =
        getEnemyAttackStrength(enemyIdx)?.let { it == highestAttackStrength } ?: false

    fun winningEnemyPoisonDamage(currentRound: Int): Int {
        if (!playerHit) return 0
        val winningEnemyWithPoison = enemies.asSequence()
            .withIndex()
            .filter { getEnemyAttackStrength(it.index) == highestEnemyAttackStrength }
            .filter { it.value.poisonedWeaponRounds >= currentRound }
            .firstOrNull()
        return winningEnemyWithPoison?.let { enemies.enemies[it.index].poisonDamage } ?: 0
    }
}
