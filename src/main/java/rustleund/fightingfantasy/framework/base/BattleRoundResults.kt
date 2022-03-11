package rustleund.fightingfantasy.framework.base

data class BattleRoundResults(
    val attackStrengths: AttackStrengths,
    val stateThatWasHit: AbstractEntityState?,
    val normalDamageDone: Int,
    val poisonDamageDone: Int = 0,
    val poisonDamageNegated: Boolean = false,
)

fun determineBattleRoundResults(
    playerState: PlayerState,
    enemies: Enemies,
    fightEnemiesTogether: Boolean,
    diceRoller: () -> List<Int>,
    currentBattleRound: Int
): BattleRoundResults {
    val attackStrengths = createAttackStrengths(playerState, enemies, fightEnemiesTogether, diceRoller)
    return if (attackStrengths.playerWon) {
        val firstEnemyToAttack = enemies.firstNonDeadEnemy
        BattleRoundResults(attackStrengths, firstEnemyToAttack, 2)
    } else if (attackStrengths.playerHit) {
        val poisonDamageDone = attackStrengths.winningEnemyPoisonDamage(currentBattleRound)
        BattleRoundResults(attackStrengths, playerState, 2, poisonDamageDone, playerState.isPoisonImmunity)
    } else BattleRoundResults(attackStrengths, null, 0)
}