/*
 * Created on Mar 8, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure;

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
	private boolean canFlee = false;
	private boolean flee = false;
	private boolean fightEnemiesTogether = true;
	private PageState pageState;
	private List<BattleEffects> allBattleEffects;
	private String currentBattleMessage;
	private AttackStrengths currentAttackStrengths;
	private int playerHitCount;
	private int battleRound = 1;

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
			loadEffects(effectsTag, pageState.getGameState().getPlayerState());
		}

		this.additionalMessages = new HashMap<>();
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

	private void loadEffects(Element effectsTag, PlayerState playerState) {
		loadEffectsFromTag(effectsTag);
		loadEffectsFromPlayerState(playerState);
	}

	private void loadEffectsFromTag(Element effectsTag) {
		BattleEffects initialBattleEffects = new BattleEffects();
		battleEffectsLoader.loadBattleEffectsFromTag(initialBattleEffects, effectsTag);
		this.allBattleEffects.add(initialBattleEffects);
	}

	private void loadEffectsFromPlayerState(PlayerState playerState) {
		if (playerState.getNextBattleBattleEffects() != null) {
			for (BattleEffects playerBattleEffects : playerState.getNextBattleBattleEffects()) {
				this.allBattleEffects.add(playerBattleEffects);
			}
		}
	}

	public boolean battleIsOver() {
		return getPlayerState().isDead() || enemies.areDead() || flee;
	}

	public PlayerState getPlayerState() {
		return pageState.getGameState().getPlayerState();
	}

	public void incrementGameState() {
		if (!battleStarted) {
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
				new LinkClosure(0, this.closureLoader, this.battleEffectsLoader).execute(this.pageState.getGameState());
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
		StringBuffer message = new StringBuffer(START_STRING);
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

		if (this.currentAttackStrengths.playerWon()) {
			EnemyState firstEnemyToAttack = enemies.getFirstNonDeadEnemy();
			firstEnemyToAttack.getStamina().adjustCurrentValueNoException(-2 - playerState.getDamageModifier());

			message.append("You hit the " + firstEnemyToAttack.getName() + "!");
			if (firstEnemyToAttack.isDead()) {
				message.append(" You have killed the " + firstEnemyToAttack.getName() + "!");
				if (firstEnemyToAttack.getEnemyKilled() != null) {
					firstEnemyToAttack.getEnemyKilled().execute(pageState.getGameState());
				}
			}
			message.append("<br>");
		} else if (this.currentAttackStrengths.playerHit()) {
			hitPlayer(playerState, this.currentAttackStrengths.winningEnemyHasPoisonedWeapon(), message);
		} else {
			message.append("Attack strengths are equal, no one is hit!<br>");
		}

		message.append("Your stamina after this round: " + playerState.getStamina().getCurrentValue() + "<br>");
		for (EnemyState enemy : enemies) {
			if (enemy.isDead()) {
				message.append(enemy.getName() + " is dead.");
			} else {
				message.append(enemy.getName() + "'s stamina after this round: " + enemy.getStamina().getCurrentValue());
			}
			message.append("<br>");
		}

		if (this.additionalMessages.containsKey(BattleMessagePosition.END)) {
			message.append("<br>");
			message.append(this.additionalMessages.get(BattleMessagePosition.END));
		}

		message.append("<a href=\"http://dobattle:" + this.id.toString() + "\">CONTINUE</a>");
		if (canFlee) {
			message.append("&nbsp;<a href=\"http://doflee:" + this.id.toString() + "\">FLEE</a>");
		}

		message.append("</p>");
		message.append(END_STRING);

		setCurrentBattleMessage(message.toString());
	}

	private void hitPlayer(PlayerState playerState, boolean foundPoisonedWeaponEnemy, StringBuffer message) {
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