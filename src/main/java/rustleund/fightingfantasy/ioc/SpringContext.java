package rustleund.fightingfantasy.ioc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.impl.DefaultBattleEffectsLoader;
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
import rustleund.fightingfantasy.framework.closures.impl.TestAnyFlagPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestClosure;
import rustleund.fightingfantasy.framework.closures.impl.TestFlagPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestItemPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestLuckClosure;
import rustleund.fightingfantasy.framework.closures.impl.TestSkillPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestStatPredicate;

import com.google.common.base.Function;

@Configuration
public class SpringContext {

	@Bean
	public ClosureLoader closureLoader() {
		Map<String, Function<Element, Closure>> mappings = new HashMap<String, Function<Element, Closure>>();

		mappings.put("adjustscale", new ElementConstructorClosureFunction(AdjustScaleClosure.class));
		mappings.put("adjustEnemyScale", new ElementConstructorClosureFunction(AdjustEnemyScaleClosure.class));
		mappings.put("displayText", new ElementConstructorClosureFunction(DisplayTextClosure.class));
		mappings.put("link", linkClosureFunction());
		mappings.put("addEnemies", new ElementConstructorClosureFunction(AddEnemiesClosure.class));
		mappings.put("addItem", new ElementConstructorClosureFunction(AddItemClosure.class));
		mappings.put("testItem", testItemClosureFunction());
		mappings.put("removeItem", new ElementConstructorClosureFunction(RemoveItemClosure.class));
		mappings.put("testLuck", new ElementConstructorClosureFunction(TestLuckClosure.class));
		mappings.put("testSkill", testSkillClosureFunction());
		mappings.put("testFlag", testFlagClosureFunction());
		mappings.put("testAnyFlag", testAnyFlagClosureFunction());
		mappings.put("setFlag", new ElementConstructorClosureFunction(SetFlagClosure.class));
		mappings.put("testStat", testStatClosureFunction());
		mappings.put("restoreScale", new ElementConstructorClosureFunction(RestoreScaleClosure.class));
		mappings.put("rollDice", rollDiceClosureFunction());
		mappings.put("initPlayer", new ElementConstructorClosureFunction(InitPlayerStateClosure.class));
		mappings.put("mustEatMeal", mustEatMealClosureFunction());
		mappings.put("addBattleMessage", new ElementConstructorClosureFunction(AddBattleMessageClosure.class));
		mappings.put("clearBattleMessage", new ElementConstructorClosureFunction(ClearBattleMessageClosure.class));
		mappings.put("clearPoisonDamage", new ElementConstructorClosureFunction(ClearPoisonDamageClosure.class));
		mappings.put("addBattleEffectsForNextBattle", addBattleEffectsForNextBattleClosureFunction());
		mappings.put("addBattleEffectsToCurrentBattle", addBattleEffectsToCurrentBattleClosureFunction());
		mappings.put("setPoisonImmunity", new ElementConstructorClosureFunction(SetPoisonImmunity.class));
		mappings.put("adjustPlayerAttackStrength", new ElementConstructorClosureFunction(AdjustPlayerAttackStrength.class));

		return new DefaultClosureLoader(mappings);
	}

	@Bean
	public Function<Element, Closure> linkClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new LinkClosure(input, closureLoader(), battleEffectsLoader());
			}
		};
	}

	@Bean
	public Function<Element, Closure> testItemClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new TestClosure(new TestItemPredicate(input), closureLoader(), input);
			}
		};
	}

	@Bean
	public Function<Element, Closure> testSkillClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new TestClosure(new TestSkillPredicate(input), closureLoader(), input);
			}
		};
	}

	@Bean
	public Function<Element, Closure> testStatClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new TestClosure(new TestStatPredicate(input), closureLoader(), input);
			}
		};
	}

	@Bean
	public Function<Element, Closure> testFlagClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new TestClosure(new TestFlagPredicate(input), closureLoader(), input);
			}
		};
	}

	@Bean
	public Function<Element, Closure> testAnyFlagClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new TestClosure(new TestAnyFlagPredicate(input), closureLoader(), input);
			}
		};
	}

	@Bean
	public Function<Element, Closure> rollDiceClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new RollDiceClosure(input, closureLoader());
			}
		};
	}

	@Bean
	public Function<Element, Closure> mustEatMealClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new MustEatMealClosure(input, closureLoader(), battleEffectsLoader());
			}
		};
	}

	@Bean
	public Function<Element, Closure> addBattleEffectsForNextBattleClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new AddBattleEffectsForNextBattleClosure(input, battleEffectsLoader());
			}
		};
	}

	@Bean
	public Function<Element, Closure> addBattleEffectsToCurrentBattleClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new AddBattleEffectsToCurrentBattleClosure(input, battleEffectsLoader());
			}
		};
	}

	@Bean
	public BattleEffectsLoader battleEffectsLoader() {
		return new DefaultBattleEffectsLoader(closureLoader());
	}

}