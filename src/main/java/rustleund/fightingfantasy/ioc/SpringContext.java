package rustleund.fightingfantasy.ioc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.AddBattleEffectsForNextBattleClosure;
import rustleund.fightingfantasy.framework.closures.impl.AddBattleEffectsToCurrentBattleClosure;
import rustleund.fightingfantasy.framework.closures.impl.AddBattleMessageClosure;
import rustleund.fightingfantasy.framework.closures.impl.AddEnemiesClosure;
import rustleund.fightingfantasy.framework.closures.impl.AddItemClosure;
import rustleund.fightingfantasy.framework.closures.impl.AdjustEnemyScaleClosure;
import rustleund.fightingfantasy.framework.closures.impl.AdjustPlayerAttackStrength;
import rustleund.fightingfantasy.framework.closures.impl.AdjustScaleClosure;
import rustleund.fightingfantasy.framework.closures.impl.ClearBattleMessageClosure;
import rustleund.fightingfantasy.framework.closures.impl.ClearPoisonDamageClosure;
import rustleund.fightingfantasy.framework.closures.impl.DefaultClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.DisplayTextClosure;
import rustleund.fightingfantasy.framework.closures.impl.ElementConstructorClosureFunction;
import rustleund.fightingfantasy.framework.closures.impl.InitPlayerStateClosure;
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure;
import rustleund.fightingfantasy.framework.closures.impl.MustEatMealClosure;
import rustleund.fightingfantasy.framework.closures.impl.RemoveItemClosure;
import rustleund.fightingfantasy.framework.closures.impl.RestoreScaleClosure;
import rustleund.fightingfantasy.framework.closures.impl.RollDiceClosure;
import rustleund.fightingfantasy.framework.closures.impl.SetFlagClosure;
import rustleund.fightingfantasy.framework.closures.impl.SetPoisonImmunity;
import rustleund.fightingfantasy.framework.closures.impl.TestAnyFlagClosure;
import rustleund.fightingfantasy.framework.closures.impl.TestFlagClosure;
import rustleund.fightingfantasy.framework.closures.impl.TestItemClosure;
import rustleund.fightingfantasy.framework.closures.impl.TestLuckClosure;
import rustleund.fightingfantasy.framework.closures.impl.TestSkillClosure;
import rustleund.fightingfantasy.framework.closures.impl.TestStatClosure;

import com.google.common.base.Function;

@Configuration
public class SpringContext {

	@Bean
	public ClosureLoader closureLoader() {
		Map<String, Function<Element, Closure>> mappings = new HashMap<String, Function<Element, Closure>>();

		mappings.put("adjustscale", new ElementConstructorClosureFunction(AdjustScaleClosure.class));
		mappings.put("adjustEnemyScale", new ElementConstructorClosureFunction(AdjustEnemyScaleClosure.class));
		mappings.put("displayText", new ElementConstructorClosureFunction(DisplayTextClosure.class));
		mappings.put("link", new ElementConstructorClosureFunction(LinkClosure.class));
		mappings.put("addEnemies", new ElementConstructorClosureFunction(AddEnemiesClosure.class));
		mappings.put("addItem", new ElementConstructorClosureFunction(AddItemClosure.class));
		mappings.put("testItem", new ElementConstructorClosureFunction(TestItemClosure.class));
		mappings.put("removeItem", new ElementConstructorClosureFunction(RemoveItemClosure.class));
		mappings.put("testLuck", new ElementConstructorClosureFunction(TestLuckClosure.class));
		mappings.put("testSkill", new ElementConstructorClosureFunction(TestSkillClosure.class));
		mappings.put("testFlag", new ElementConstructorClosureFunction(TestFlagClosure.class));
		mappings.put("testAnyFlag", new ElementConstructorClosureFunction(TestAnyFlagClosure.class));
		mappings.put("setFlag", new ElementConstructorClosureFunction(SetFlagClosure.class));
		mappings.put("testStat", new ElementConstructorClosureFunction(TestStatClosure.class));
		mappings.put("restoreScale", new ElementConstructorClosureFunction(RestoreScaleClosure.class));
		mappings.put("rollDice", new ElementConstructorClosureFunction(RollDiceClosure.class));
		mappings.put("initPlayer", new ElementConstructorClosureFunction(InitPlayerStateClosure.class));
		mappings.put("mustEatMeal", new ElementConstructorClosureFunction(MustEatMealClosure.class));
		mappings.put("addBattleMessage", new ElementConstructorClosureFunction(AddBattleMessageClosure.class));
		mappings.put("clearBattleMessage", new ElementConstructorClosureFunction(ClearBattleMessageClosure.class));
		mappings.put("clearPoisonDamage", new ElementConstructorClosureFunction(ClearPoisonDamageClosure.class));
		mappings.put("addBattleEffectsForNextBattle", new ElementConstructorClosureFunction(AddBattleEffectsForNextBattleClosure.class));
		mappings.put("addBattleEffectsToCurrentBattle", new ElementConstructorClosureFunction(AddBattleEffectsToCurrentBattleClosure.class));
		mappings.put("setPoisonImmunity", new ElementConstructorClosureFunction(SetPoisonImmunity.class));
		mappings.put("adjustPlayerAttackStrength", new ElementConstructorClosureFunction(AdjustPlayerAttackStrength.class));

		return new DefaultClosureLoader(mappings);
	}

}
