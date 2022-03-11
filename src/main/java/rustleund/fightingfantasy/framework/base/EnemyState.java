/*
 * Created on Mar 8, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

public class EnemyState extends AbstractEntityState {

    private int poisonedWeaponRounds = 0;
    private int poisonDamage = 0;
    private final Set<String> types = new HashSet<>();
    private Closure enemyKilled;

    public EnemyState(String name, int skill, int stamina, int poisonedWeaponRounds, int poisonDamage) {
        this.name = name;
        this.skill = new Scale(0, skill, skill, true);
        this.stamina = new Scale(0, stamina, stamina, true);
        this.poisonedWeaponRounds = poisonedWeaponRounds;
        this.poisonDamage = poisonDamage;
    }

    public EnemyState(Element enemyTag, ClosureLoader closureLoader) {

        this.name = enemyTag.getAttribute("name");

        Integer skillInteger = Integer.valueOf(enemyTag.getAttribute("skill"));
        this.skill = new Scale(0, skillInteger, skillInteger, true);

        Integer staminaInteger = Integer.valueOf(enemyTag.getAttribute("stamina"));
        this.stamina = new Scale(0, staminaInteger, staminaInteger, true);

        if (enemyTag.hasAttribute("poisonedWeaponRounds")) {
            this.poisonedWeaponRounds = Integer.parseInt(enemyTag.getAttribute("poisonedWeaponRounds"));
            if (enemyTag.hasAttribute("poisonDamage")) {
                this.poisonDamage = Integer.parseInt(enemyTag.getAttribute("poisonDamage"));
            } else {
                this.poisonDamage = 2;
            }
        }

        if (enemyTag.hasAttribute("types")) {
            this.types.addAll(Arrays.asList(enemyTag.getAttribute("types").split(",")));
        }

        Element onKilledElement = XMLUtilKt.getChildElementByName(enemyTag, "onKilled");
        if (onKilledElement != null) {
            this.enemyKilled = closureLoader.loadClosureFromChildren(onKilledElement);
        }
    }

    public int getPoisonedWeaponRounds() {
        return poisonedWeaponRounds;
    }

    public Closure getEnemyKilled() {
        return this.enemyKilled;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }

    public boolean isOfType(String type) {
        return this.types.contains(type);
    }
}