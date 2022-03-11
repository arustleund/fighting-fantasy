package rustleund.fightingfantasy.framework.base

fun addBattleTable(
    b: StringBuilder,
    playerState: PlayerState,
    enemies: Enemies,
    battleRoundResults: BattleRoundResults
) {
    b.append("""<p><table border="1"><tr><th></th><th>SK</th><th>DICE</th><th>+</th><th>TOTAL</th><th>DMG</th><th>STAMINA</th></tr>""")
    val attackStrengths = battleRoundResults.attackStrengths
    appendAttackStrength(b, attackStrengths.playerAttackStrength, playerState, battleRoundResults)
    enemies.forEachIndexed { index, enemyState ->
        appendAttackStrength(
            b,
            attackStrengths.getEnemyAttackStrength(index),
            enemyState,
            battleRoundResults
        )
    }
    b.append("</table></p><br>")
}

private fun appendAttackStrength(
    b: StringBuilder,
    attackStrength: AttackStrength?,
    state: AbstractEntityState,
    battleRoundResults: BattleRoundResults
) {
    b.append("<tr")
    if (state == battleRoundResults.stateThatWasHit) b.append(" style=\"background-color: #FBB\"")
    if (attackStrength == battleRoundResults.attackStrengths.highestAttackStrength) b.append(" style=\"background-color: #BFB\"")
    b.append(">")
    b.append("<td>${state.name}</td>")
    b.append("<td style=\"text-align: center;\">${state.skill.currentValue}</td>")
    b.append("""<td style="text-align: center;">""")
    attackStrength?.dieRoll?.forEach { b.append(img("Die_face_${it}b.svg.png")) }
    b.append("</td>")
    b.append("<td style=\"text-align: center;\">${attackStrength?.modifier ?: ""}</td>")
    val total = attackStrength?.total?.toString() ?: ""
    b.append("<td style=\"text-align: center;\"><b>$total</b></td>")
    b.append("<td style=\"text-align: center;\">")
    if (state == battleRoundResults.stateThatWasHit) {
        b.append(battleRoundResults.normalDamageDone)
        if (battleRoundResults.poisonDamageDone > 0) {
            b.append(" + ${battleRoundResults.poisonDamageDone} poison")
            if (battleRoundResults.poisonDamageNegated) b.append(" (negated)")
        }
    } else b.append("0")
    b.append("</td>")
    val staminaBody = if (state.isDead) img("skull.png") else state.stamina.currentValue
    b.append("<td style=\"text-align: center;\">$staminaBody</td>")
    b.append("</tr>")
}

private fun img(name: String): String {
    val img = ClassLoader.getSystemResource(name).toString()
    return """<img src="$img" height="20" width="20" style="vertical-align: top;" />"""
}