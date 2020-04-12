/*
 * Created on Mar 8, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

/**
 * @author rustlea
 */
public class BattleState {

	public static final String START_STRING = "<!-- START BATTLE -->";
	public static final String END_STRING = "<!-- END BATTLE -->";

	private BattleEffectsLoader battleEffectsLoader;
	private ClosureLoader closureLoader;

	private Integer id;
	private Enemies enemies;
	private boolean battleStarted = false;
	private boolean canFlee;
	private boolean flee = false;
	private boolean fightEnemiesTogether = true;
	private PageState pageState;
	private List<BattleEffects> allBattleEffects;
	private String currentBattleMessage;
	private AttackStrengths currentAttackStrengths;
	private int playerHitCount;
	private int battleRound = 1;
	private boolean testedLuckThisRound = false;

	private Map<BattleMessagePosition, String> additionalMessages;
	private Collection<BattleEffects> battleEffectsForNextRound = new ArrayList<>();

	public enum BattleMessagePosition {
		BEGINNING, END
	}

	public BattleState(Element battleTag, PageState pageState, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		this.closureLoader = closureLoader;
		this.battleEffectsLoader = battleEffectsLoader;

		this.pageState = pageState;

		this.id = Integer.valueOf(battleTag.getAttribute("id"));

		if (battleTag.hasAttribute("fightEnemiesTogether")) {
			this.fightEnemiesTogether = battleTag.getAttribute("fightEnemiesTogether").equalsIgnoreCase("true");
		}

		this.canFlee = battleTag.hasAttribute("canFlee") && battleTag.getAttribute("canFlee").equalsIgnoreCase("true");

		loadEnemies(XMLUtil.getChildElementByName(battleTag, "enemies"));

		this.allBattleEffects = new ArrayList<>();

		Element effectsTag = XMLUtil.getChildElementByName(battleTag, "effects");
		if (effectsTag != null) {
			loadEffects(effectsTag);
		}

		this.additionalMessages = new EnumMap<>(BattleMessagePosition.class);
	}

	public void addAdditionalMessage(BattleMessagePosition position, String message) {
		this.additionalMessages.put(position, message);
	}

	public void clearAdditionalMessage(BattleMessagePosition position) {
		this.additionalMessages.remove(position);
	}

	public void clearAllAdditionalMessages() {
		this.additionalMessages.clear();
	}

	private void loadEnemies(Element enemiesTag) {
		this.enemies = new Enemies();
		XMLUtil.getChildElementsByName(enemiesTag, "enemy").forEach(e -> enemies.addEnemy(new EnemyState(e, closureLoader)));
	}

	private void loadEffects(Element effectsTag) {
		loadEffectsFromTag(effectsTag);
	}

	private void loadEffectsFromTag(Element effectsTag) {
		BattleEffects initialBattleEffects = new BattleEffects();
		battleEffectsLoader.loadBattleEffectsFromTag(initialBattleEffects, effectsTag);
		this.allBattleEffects.add(initialBattleEffects);
	}

	private void loadEffectsFromPlayerState(PlayerState playerState) {
		if (playerState.getNextBattleBattleEffects() != null) {
			this.allBattleEffects.addAll(playerState.getNextBattleBattleEffects());
		}
	}

	public boolean battleIsNotOver() {
		return !getPlayerState().isDead() && !enemies.areDead() && !flee;
	}

	public PlayerState getPlayerState() {
		return pageState.getGameState().getPlayerState();
	}

	public void incrementGameState() {
		if (!battleStarted) {
			loadEffectsFromPlayerState(pageState.getGameState().getPlayerState());
			doStartBattle();
			battleStarted = true;
			this.pageState.getGameState().getPlayerState().setPoisonDamage(0);
			incrementGameState();
		} else if (flee) {
			doPlayerFlee();
		} else {
			doStartRound();
			doDamage();
			doEndRound();

			if (getPlayerState().isDead()) {
				appendAtEndOfBattleMessage("You have died, and your adventure ends here.");
			}
		}
	}

	public void doStartBattle() {
		doBattleStage(this.allBattleEffects, BattleEffects::getStartBattle);
		doBattleStage(itemBattleEffects(), BattleEffects::getStartBattle);
	}

	public void doPlayerFlee() {
		doBattleStage(this.allBattleEffects, BattleEffects::getPlayerFlee);
		doBattleStage(itemBattleEffects(), BattleEffects::getPlayerFlee);
	}

	public void doStartRound() {
		testedLuckThisRound = false;
		doBattleStage(this.allBattleEffects, BattleEffects::getStartRound);
		doBattleStage(this.battleEffectsForNextRound, BattleEffects::getStartRound);
		doBattleStage(itemBattleEffects(), BattleEffects::getStartRound);
	}

	public void doEndRound() {
		List<BattleEffects> nextRoundCopy = new ArrayList<>(battleEffectsForNextRound);
		this.battleEffectsForNextRound.clear();
		doBattleStage(nextRoundCopy, BattleEffects::getEndRound);
		doBattleStage(this.allBattleEffects, BattleEffects::getEndRound);
		doBattleStage(itemBattleEffects(), BattleEffects::getEndRound);
		this.battleRound++;
	}

	public void doPlayerHit() {
		doBattleStage(this.allBattleEffects, BattleEffects::getPlayerHit);
		doBattleStage(itemBattleEffects(), BattleEffects::getPlayerHit);
	}

	public void doEndBattle() {
		doBattleStage(this.allBattleEffects, BattleEffects::getEndBattle);
		doBattleStage(itemBattleEffects(), BattleEffects::getEndBattle);
	}

	public void doAfterPossibleStaminaChange() {
		if (getCurrentBattleMessage() != null) {
			pageState.replacePagetext(BattleState.START_STRING, BattleState.END_STRING, getCurrentBattleMessage());
		}
		if (getEnemies().areDead() || getPlayerState().isDead()) {
			getPageState().getGameState().endBattle();
		}
	}

	public void doTestLuck(boolean enemyHit, StringBuilder existingMessageBuilder) {
		if (!testedLuckThisRound) {
			boolean lucky = getPlayerState().testLuck(0);
			testedLuckThisRound = true;
			StringBuilder message = existingMessageBuilder == null ? new StringBuilder() : existingMessageBuilder;
			message.append("You were ");
			message.append(lucky ? "lucky" : "unlucky");
			message.append("! ");
			if (enemyHit) {
				EnemyState firstNonDeadEnemy = enemies.getFirstNonDeadEnemy();
				message.append(firstNonDeadEnemy.getName());
				if (lucky) {
					message.append(" will take 2 additional points of damage!<br>");
					firstNonDeadEnemy.getStamina().adjustCurrentValueNoException(-2);
				} else {
					message.append(" will take 1 less point of damage!<br>");
					firstNonDeadEnemy.getStamina().adjustCurrentValueNoException(1);
				}
				appendEnemyStamina(message, firstNonDeadEnemy);
			} else {
				if (lucky) {
					message.append("You will take 1 less point of damage!<br>");
					getPlayerState().getStamina().adjustCurrentValueNoException(1);
				} else {
					message.append("You will take 1 more point of damage!<br>");
					getPlayerState().getStamina().adjustCurrentValueNoException(-1);
				}
				addPlayerStamina(message, getPlayerState());
				if (getPlayerState().isDead()) {
					message.append("You have died, and your adventure ends here.");
				}
			}
			if (existingMessageBuilder == null) {
				appendAtEndOfBattleMessage(message.toString());
				doAfterPossibleStaminaChange();
			}
		}
	}

	private Collection<BattleEffects> itemBattleEffects() {
		Collection<Item> items = pageState.getGameState().getPlayerState().getItems().values();
		Stream<BattleEffects> itemBattleEffects = items.stream().map(Item::getBattleEffects);
		return itemBattleEffects.filter(Objects::nonNull).collect(Collectors.toList());
	}

	private void doBattleStage(Collection<BattleEffects> battleEffects, Function<BattleEffects, Closure> closureFunction) {
		Stream<Closure> mapped = new ArrayList<>(battleEffects).stream().map(closureFunction);
		mapped.filter(Objects::nonNull).forEach(c -> c.execute(this.pageState.getGameState()));
	}

	private void doDamage() {
		StringBuilder message = new StringBuilder(START_STRING);
		message.append("<p>");

		if (this.additionalMessages.containsKey(BattleMessagePosition.BEGINNING)) {
			message.append(this.additionalMessages.get(BattleMessagePosition.BEGINNING));
			message.append("<br>");
		}

		PlayerState playerState = getPlayerState();

		this.currentAttackStrengths = AttackStrengths.init(playerState, enemies, fightEnemiesTogether);

		message.append("Your attack strength: " + this.currentAttackStrengths.getPlayerAttackStrength() + "<br>");

		for (int i = 0; i < enemies.getEnemies().size(); i++) {
			EnemyState enemy = enemies.getEnemies().get(i);
			if (enemy.isDead()) {
				message.append(enemy.getName() + " is dead.<br>");
			} else {
				AttackStrength enemyAttackStrength = this.currentAttackStrengths.getEnemyAttackStrength(i);
				if (enemyAttackStrength == null) {
					message.append(enemy.getName() + " is waiting to fight.<br>");
				} else {
					message.append(enemy.getName() + "'s attack strength: " + enemyAttackStrength + "<br>");
				}
			}
		}

		if (battleIsNotOver()) {
			if (this.currentAttackStrengths.playerWon()) {
				EnemyState firstEnemyToAttack = enemies.getFirstNonDeadEnemy();
				int damage = -2 - playerState.getDamageModifier();
				firstEnemyToAttack.getStamina().adjustCurrentValueNoException(damage);

				message.append("You hit the " + firstEnemyToAttack.getName() + " for " + (damage * -1) + " damage!");
				if (firstEnemyToAttack.isDead()) {
					message.append(" You have killed the " + firstEnemyToAttack.getName() + "!");
					if (firstEnemyToAttack.getEnemyKilled() != null) {
						firstEnemyToAttack.getEnemyKilled().execute(pageState.getGameState());
					}
				} else if (!playerState.getLuck().isEmpty()) {
					message.append("<br>");
					message.append("<a href=\"http://testluckbattle:0\"><i>Test Your Luck</i></a> to try to do add an additional 2 points of damage");
				}
				message.append("<br>");
			} else if (this.currentAttackStrengths.playerHit()) {
				hitPlayer(playerState, this.currentAttackStrengths.winningEnemyHasPoisonedWeapon(), message);
			} else {
				message.append("Attack strengths are equal, no one is hit!<br>");
			}
		}

		addPlayerStamina(message, playerState);
		for (EnemyState enemy : enemies) {
			appendEnemyStamina(message, enemy);
		}

		if (this.additionalMessages.containsKey(BattleMessagePosition.END)) {
			message.append(this.additionalMessages.get(BattleMessagePosition.END));
			message.append("<br>");
		}

		message.append("<a href=\"http://dobattle:" + this.id.toString() + "\">CONTINUE</a>");
		if (canFlee) {
			message.append("&nbsp;<a href=\"http://doflee:" + this.id.toString() + "\">FLEE</a>");
		}

		message.append("</p>");
		message.append(END_STRING);

		setCurrentBattleMessage(message.toString());
	}

	private void appendAtEndOfBattleMessage(String message) {
		StringBuilder newBattleMessage = new StringBuilder(getCurrentBattleMessage());
		newBattleMessage.setLength(newBattleMessage.length() - END_STRING.length());
		newBattleMessage.append("<br>");
		newBattleMessage.append(message);
		newBattleMessage.append(END_STRING);
		setCurrentBattleMessage(newBattleMessage.toString());
	}

	private void addPlayerStamina(StringBuilder message, PlayerState playerState) {
		message.append("Your stamina after this round: " + playerState.getStamina().getCurrentValue() + "<br>");
	}

	private void appendEnemyStamina(StringBuilder message, EnemyState enemy) {
		if (enemy.isDead()) {
			message.append(enemy.getName() + " is dead.");
		} else {
			message.append(enemy.getName() + "'s stamina after this round: " + enemy.getStamina().getCurrentValue());
		}
		message.append("<br>");
	}

	private void hitPlayer(PlayerState playerState, boolean foundPoisonedWeaponEnemy, StringBuilder message) {
		this.playerHitCount++;
		if (foundPoisonedWeaponEnemy) {
			if (playerState.isPoisonImmunity()) {
				message.append("You were hit by a poisoned weapon, but you took no damage because you are immune to poison!<br>");
			} else {
				playerState.takePoisonDamage(2);
				message.append("You were hit by a poisoned weapon!<br>");
				doPlayerHit();
			}
		} else {
			playerState.getStamina().adjustCurrentValueNoException(-2);
			message.append("You were hit!<br>");
			doPlayerHit();
		}
		if (!playerState.getLuck().isEmpty()) {
			if (playerState.isDead()) {
				message.append("Your Stamina was reduced to 0, Testing Your Luck to save your life...<br>");
				doTestLuck(false, message);
			} else {
				message.append("<a href=\"http://testluckbattle:1\"><i>Test Your Luck</i></a> to try to reduce 1 points of damage<br>");
			}
		}
	}

	public void setEnemies(Enemies enemies) {
		this.enemies = enemies;
	}

	public void setFlee(boolean flee) {
		this.flee = flee;
	}

	public String getCurrentBattleMessage() {
		return currentBattleMessage;
	}

	public void setCurrentBattleMessage(String currentBattleMessage) {
		this.currentBattleMessage = currentBattleMessage;
	}

	public Enemies getEnemies() {
		return enemies;
	}

	public PageState getPageState() {
		return pageState;
	}

	public void setPageState(PageState pageState) {
		this.pageState = pageState;
	}

	public boolean isCanFlee() {
		return canFlee;
	}

	public void setCanFlee(boolean canFlee) {
		this.canFlee = canFlee;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isFightEnemiesTogether() {
		return fightEnemiesTogether;
	}

	public List<BattleEffects> getAllBattleEffects() {
		return this.allBattleEffects;
	}

	public void setAllBattleEffects(List<BattleEffects> allBattleEffects) {
		this.allBattleEffects = allBattleEffects;
	}

	public AttackStrengths getCurrentAttackStrengths() {
		return this.currentAttackStrengths;
	}

	public int getPlayerHitCount() {
		return this.playerHitCount;
	}

	public void addBattleEffectsForNextRound(Collection<? extends BattleEffects> effectsForNextRound) {
		this.battleEffectsForNextRound.addAll(effectsForNextRound);
	}

	public int getBattleRound() {
		return this.battleRound;
	}
}