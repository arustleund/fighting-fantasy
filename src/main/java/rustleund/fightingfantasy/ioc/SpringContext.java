package rustleund.fightingfantasy.ioc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.AbstractEntityState;
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.EnemyState;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.ItemUtil;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.base.impl.DefaultBattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.impl.DefaultItemUtil;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.AddBattleEffectsForNextBattleClosure;
import rustleund.fightingfantasy.framework.closures.impl.AddBattleEffectsToCurrentBattleClosure;
import rustleund.fightingfantasy.framework.closures.impl.AddBattleEffectsToNextBattleRoundClosure;
import rustleund.fightingfantasy.framework.closures.impl.AddBattleMessageClosure;
import rustleund.fightingfantasy.framework.closures.impl.AddEnemiesClosure;
import rustleund.fightingfantasy.framework.closures.impl.AddItemClosure;
import rustleund.fightingfantasy.framework.closures.impl.AdjustByAmountClosure;
import rustleund.fightingfantasy.framework.closures.impl.AdjustEnemyScaleClosure;
import rustleund.fightingfantasy.framework.closures.impl.AdjustScaleClosure;
import rustleund.fightingfantasy.framework.closures.impl.ClearBattleMessageClosure;
import rustleund.fightingfantasy.framework.closures.impl.ClearPoisonDamageClosure;
import rustleund.fightingfantasy.framework.closures.impl.DefaultClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.DisplayEnemiesClosure;
import rustleund.fightingfantasy.framework.closures.impl.DisplayTextClosure;
import rustleund.fightingfantasy.framework.closures.impl.DoBattleClosure;
import rustleund.fightingfantasy.framework.closures.impl.InitPlayerStateClosure;
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure;
import rustleund.fightingfantasy.framework.closures.impl.LinkIfFlagFalseClosure;
import rustleund.fightingfantasy.framework.closures.impl.MustEatMealClosure;
import rustleund.fightingfantasy.framework.closures.impl.PlayerAttackStrengthDifferenceFromEnemyFunction;
import rustleund.fightingfantasy.framework.closures.impl.RemoveItemClosure;
import rustleund.fightingfantasy.framework.closures.impl.RestoreScaleClosure;
import rustleund.fightingfantasy.framework.closures.impl.RollDiceClosure;
import rustleund.fightingfantasy.framework.closures.impl.SetFlagClosure;
import rustleund.fightingfantasy.framework.closures.impl.SetPoisonImmunity;
import rustleund.fightingfantasy.framework.closures.impl.TestAnyFlagPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestClosure;
import rustleund.fightingfantasy.framework.closures.impl.TestEnemyStatPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestEnemyTypesPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestFlagPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestItemPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestLuckClosure;
import rustleund.fightingfantasy.framework.closures.impl.TestNumberPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestSkillPredicate;
import rustleund.fightingfantasy.framework.closures.impl.TestStatPredicate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

@Configuration
public class SpringContext {

	@Bean
	public ClosureLoader closureLoader() {
		Map<String, Function<Element, Closure>> mappings = new HashMap<>();

		mappings.put("addBattleEffectsForNextBattle", addBattleEffectsForNextBattleClosureFunction());
		mappings.put("addBattleEffectsToCurrentBattle", addBattleEffectsToCurrentBattleClosureFunction());
		mappings.put("addBattleEffectsToNextBattleRound", addBattleEffectsToNextBattleRoundClosureFunction());
		mappings.put("addBattleMessage", AddBattleMessageClosure::new);
		mappings.put("addEnemies", addEnemiesClosureFunction());
		mappings.put("addItem", addItemClosureFunction());
		mappings.put("adjustEnemyScale", adjustEnemyScaleClosureFunction());
		mappings.put("adjustFirstNonDeadEnemyAttackStrength", adjustFirstNonDeadEnemyAttackStrengthClosureFunction());
		mappings.put("adjustPlayerAttackStrength", adjustPlayerAttackStrengthClosureFunction());
		mappings.put("adjustPlayerDamageModifier", adjustPlayerDamageModifierClosureFunction());
		mappings.put("adjustScale", adjustScaleClosureFunction());
		mappings.put("clearBattleMessage", ClearBattleMessageClosure::new);
		mappings.put("clearPoisonDamage", ClearPoisonDamageClosure::new);
		mappings.put("displayText", DisplayTextClosure::new);
		mappings.put("displayEnemies", DisplayEnemiesClosure::new);
		mappings.put("doBattle", DoBattleClosure::new);
		mappings.put("flaggedLink", element -> new LinkIfFlagFalseClosure(element, closureLoader(), battleEffectsLoader()));
		mappings.put("initPlayer", initPlayerStateClosureFunction());
		mappings.put("link", linkClosureFunction());
		mappings.put("mustEatMeal", mustEatMealClosureFunction());
		mappings.put("removeItem", RemoveItemClosure::new);
		mappings.put("restoreScale", RestoreScaleClosure::new);
		mappings.put("rollDice", rollDiceClosureFunction());
		mappings.put("setFlag", SetFlagClosure::new);
		mappings.put("setPoisonImmunity", SetPoisonImmunity::new);
		mappings.put("testAnyFlag", testAnyFlagClosureFunction());
		mappings.put("testFlag", testFlagClosureFunction());
		mappings.put("testItem", testItemClosureFunction());
		mappings.put("testLuck", element -> new TestLuckClosure(element, closureLoader()));
		mappings.put("testSkill", testSkillClosureFunction());
		mappings.put("testStat", testStatClosureFunction());
		mappings.put("testEnemyStat", testEnemyStatClosureFunction());
		mappings.put("testEnemyTypes", testEnemyTypesClosureFunction());
		mappings.put("testEnemySkill", testEnemySkillClosureFunction());
		mappings.put("testPlayerAttackStrengthDifferenceFromEnemy", testPlayerAttackStrengthDifferenceFromEnemyFunction());

		return new DefaultClosureLoader(mappings);
	}

	@Bean
	public Function<Element, Closure> adjustPlayerAttackStrengthClosureFunction() {
		return element -> {
			return new AdjustByAmountClosure(element, playerStateFromGameState().andThen(PlayerState::getAttackStrengthModifier),
					(gameState, newValue) -> gameState.getPlayerState().setAttackStrengthModifier(newValue));
		};
	}

	@Bean
	public Function<Element, Closure> adjustFirstNonDeadEnemyAttackStrengthClosureFunction() {
		return element -> {
			return new AdjustByAmountClosure(element, firstNonDeadEnemyStateFromGameState().andThen(EnemyState::getAttackStrengthModifier),
					(gameState, newValue) -> gameState.getBattleState().getEnemies().getFirstNonDeadEnemy().setAttackStrengthModifier(newValue));
		};
	}

	@Bean
	public Function<Element, Closure> adjustPlayerDamageModifierClosureFunction() {
		return element -> {
			return new AdjustByAmountClosure(element, playerStateFromGameState().andThen(PlayerState::getDamageModifier),
					(gameState, newValue) -> gameState.getPlayerState().setDamageModifier(newValue));
		};
	}

	@Bean
	public java.util.function.Function<GameState, PlayerState> playerStateFromGameState() {
		return GameState::getPlayerState;
	}

	@Bean
	public java.util.function.Function<GameState, EnemyState> firstNonDeadEnemyStateFromGameState() {
		return gameState -> gameState.getBattleState().getEnemies().getFirstNonDeadEnemy();
	}

	@Bean
	public Function<Element, Closure> testPlayerAttackStrengthDifferenceFromEnemyFunction() {
		return element -> {
			Predicate<GameState> predicate = new TestNumberPredicate(element, new PlayerAttackStrengthDifferenceFromEnemyFunction());
			return new TestClosure(predicate, closureLoader(), element);
		};
	}

	@Bean
	public Function<Element, Closure> testEnemyTypesClosureFunction() {
		return element -> {
			return new TestClosure(new TestEnemyTypesPredicate(element), closureLoader(), element);
		};
	}

	@Bean
	public Function<Element, Closure> addEnemiesClosureFunction() {
		return (Element input) -> {
			return new AddEnemiesClosure(input, closureLoader());
		};
	}

	@Bean
	public Function<Element, Closure> adjustEnemyScaleClosureFunction() {
		return (Element input) -> {
			return new AdjustEnemyScaleClosure(input, closureLoader(), battleEffectsLoader());
		};
	}

	@Bean
	public Function<Element, Closure> adjustScaleClosureFunction() {
		return (Element input) -> {
			return new AdjustScaleClosure(input, closureLoader(), battleEffectsLoader());
		};
	}

	@Bean
	public Function<Element, Closure> initPlayerStateClosureFunction() {
		return (Element input) -> {
			return new InitPlayerStateClosure(itemUtil());
		};
	}

	@Bean
	public Function<Element, Closure> addItemClosureFunction() {
		return input -> new AddItemClosure(input, itemUtil());
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
		return element -> new TestClosure(new TestItemPredicate(element), closureLoader(), element);
	}

	@Bean
	public Function<Element, Closure> testSkillClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new TestClosure(new TestSkillPredicate(input, GameState::getPlayerState), closureLoader(), input);
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
	public Function<Element, Closure> testEnemyStatClosureFunction() {
		return new Function<Element, Closure>() {
			@Override
			public Closure apply(Element input) {
				return new TestClosure(new TestEnemyStatPredicate(input), closureLoader(), input);
			}
		};
	}

	@Bean
	public Function<Element, Closure> testEnemySkillClosureFunction() {
		return element -> new TestClosure(new TestSkillPredicate(element, gameStateToEnemy(element)), closureLoader(), element);
	}

	private java.util.function.Function<? super GameState, ? extends AbstractEntityState> gameStateToEnemy(Element element) {
		return gameState -> gameState.getBattleState().getEnemies().getEnemies().get(Integer.valueOf(element.getAttribute("enemyId")));
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
	public Function<Element, Closure> addBattleEffectsToNextBattleRoundClosureFunction() {
		return element -> {
			return new AddBattleEffectsToNextBattleRoundClosure(element, battleEffectsLoader());
		};
	}

	@Bean
	public BattleEffectsLoader battleEffectsLoader() {
		return new DefaultBattleEffectsLoader(closureLoader());
	}

	@Bean
	public ItemUtil itemUtil() {
		return new DefaultItemUtil(closureLoader(), battleEffectsLoader());
	}

}
