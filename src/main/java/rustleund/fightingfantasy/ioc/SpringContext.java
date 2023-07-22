package rustleund.fightingfantasy.ioc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

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
import rustleund.fightingfantasy.framework.closures.impl.*;

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
		mappings.put("clearPoisonDamage", e -> new ClearPoisonDamageClosure());
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
		mappings.put("setOnPlayerDeath", e -> new SetOnPlayerDeathClosure(e, closureLoader()));
		mappings.put("setPoisonImmunity", SetPoisonImmunity::new);
		mappings.put("takePoisonDamage", TakePoisonDamageClosure::new);
		mappings.put("testAnyFlag", testAnyFlagClosureFunction());
		mappings.put("testHighestAttackStrength", testHighestAttackStrengthClosureFunction());
		mappings.put("testFlag", testFlagClosureFunction());
		mappings.put("testFlagText", testFlagTextClosureFunction());
		mappings.put("testItem", testItemClosureFunction());
		mappings.put("testLuck", element -> new TestLuckClosure(element, closureLoader()));
		mappings.put("testSkill", testSkillClosureFunction());
		mappings.put("testStat", testStatClosureFunction());
		mappings.put("testStatText", testStatTextClosureFunction());
		mappings.put("testEnemyStat", testEnemyStatClosureFunction());
		mappings.put("testEnemyTypes", testEnemyTypesClosureFunction());
		mappings.put("testEnemySkill", testEnemySkillClosureFunction());
		mappings.put("testPlayerAttackStrengthDifferenceFromEnemy", testPlayerAttackStrengthDifferenceFromEnemyFunction());

		return new DefaultClosureLoader(mappings);
	}

	@Bean
	public Function<Element, Closure> adjustPlayerAttackStrengthClosureFunction() {
		return element -> new AdjustByAmountClosure(element, playerStateFromGameState().andThen(PlayerState::getAttackStrengthModifier),
				(gameState, newValue) -> gameState.getPlayerState().setAttackStrengthModifier(newValue));
	}

	@Bean
	public Function<Element, Closure> adjustFirstNonDeadEnemyAttackStrengthClosureFunction() {
		return element -> new AdjustByAmountClosure(element, firstNonDeadEnemyStateFromGameState().andThen(EnemyState::getAttackStrengthModifier),
				(gameState, newValue) -> gameState.getBattleState().getEnemies().getFirstNonDeadEnemy().setAttackStrengthModifier(newValue));
	}

	@Bean
	public Function<Element, Closure> adjustPlayerDamageModifierClosureFunction() {
		return element -> new AdjustByAmountClosure(element, playerStateFromGameState().andThen(PlayerState::getDamageModifier),
				(gameState, newValue) -> gameState.getPlayerState().setDamageModifier(newValue));
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
	public Function<Element, Closure> testHighestAttackStrengthClosureFunction() {
		return e -> new TestClosure(new TestAttackStrengthHighestPredicate(e), closureLoader(), e);
	}

	@Bean
	public Function<Element, Closure> testEnemyTypesClosureFunction() {
		return element -> new TestClosure(new TestEnemyTypesPredicate(element), closureLoader(), element);
	}

	@Bean
	public Function<Element, Closure> addEnemiesClosureFunction() {
		return input -> new AddEnemiesClosure(input, closureLoader());
	}

	@Bean
	public Function<Element, Closure> adjustEnemyScaleClosureFunction() {
		return AdjustEnemyScaleClosure::new;
	}

	@Bean
	public Function<Element, Closure> adjustScaleClosureFunction() {
		return input -> new AdjustScaleClosure(input, GameState::getPlayerState);
	}

	@Bean
	public Function<Element, Closure> initPlayerStateClosureFunction() {
		return input -> new InitPlayerStateClosure(itemUtil());
	}

	@Bean
	public Function<Element, Closure> addItemClosureFunction() {
		return input -> new AddItemClosure(input, itemUtil());
	}

	@Bean
	public Function<Element, Closure> linkClosureFunction() {
		return input -> new LinkClosure(input, closureLoader(), battleEffectsLoader());
	}

	@Bean
	public Function<Element, Closure> testItemClosureFunction() {
		return element -> new TestClosure(new TestItemPredicate(element), closureLoader(), element);
	}

	@Bean
	public Function<Element, Closure> testSkillClosureFunction() {
		return input -> new TestClosure(new TestSkillPredicate(input, GameState::getPlayerState), closureLoader(), input);
	}

	@Bean
	public Function<Element, Closure> testStatClosureFunction() {
		return input -> new TestClosure(new TestStatPredicate(input), closureLoader(), input);
	}

	@Bean
	public Function<Element, Closure> testStatTextClosureFunction() {
		return e -> new PredicateTextClosure(e, new TestStatPredicate(e));
	}

	@Bean
	public Function<Element, Closure> testEnemyStatClosureFunction() {
		return input -> new TestClosure(new TestEnemyStatPredicate(input), closureLoader(), input);
	}

	@Bean
	public Function<Element, Closure> testEnemySkillClosureFunction() {
		return element -> new TestClosure(new TestSkillPredicate(element, gameStateToEnemy(element)), closureLoader(), element);
	}

	private java.util.function.Function<? super GameState, ? extends AbstractEntityState> gameStateToEnemy(Element element) {
		return gameState -> gameState.getBattleState().getEnemies().getEnemies().get(Integer.parseInt(element.getAttribute("enemyId")));
	}

	@Bean
	public Function<Element, Closure> testFlagClosureFunction() {
		return input -> new TestClosure(new TestFlagPredicate(input), closureLoader(), input);
	}

	@Bean
	public Function<Element, Closure> testFlagTextClosureFunction() {
		return e -> new PredicateTextClosure(e, new TestFlagPredicate(e));
	}

	@Bean
	public Function<Element, Closure> testAnyFlagClosureFunction() {
		return input -> new TestClosure(new TestAnyFlagPredicate(input), closureLoader(), input);
	}

	@Bean
	public Function<Element, Closure> rollDiceClosureFunction() {
		return input -> new RollDiceClosure(input, closureLoader());
	}

	@Bean
	public Function<Element, Closure> mustEatMealClosureFunction() {
		return input -> new MustEatMealClosure(input, closureLoader(), battleEffectsLoader());
	}

	@Bean
	public Function<Element, Closure> addBattleEffectsForNextBattleClosureFunction() {
		return input -> new AddBattleEffectsForNextBattleClosure(input, battleEffectsLoader());
	}

	@Bean
	public Function<Element, Closure> addBattleEffectsToCurrentBattleClosureFunction() {
		return input -> new AddBattleEffectsToCurrentBattleClosure(input, battleEffectsLoader());
	}

	@Bean
	public Function<Element, Closure> addBattleEffectsToNextBattleRoundClosureFunction() {
		return element -> new AddBattleEffectsToNextBattleRoundClosure(element, battleEffectsLoader());
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
