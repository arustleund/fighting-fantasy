/*
 * Created on Mar 8, 2004
 */
package rustleund.nightdragon.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.nightdragon.framework.closures.LinkClosure;
import rustleund.nightdragon.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class BattleState {

	public static final String START_STRING = "<!-- START BATTLE -->";
	public static final String END_STRING = "<!-- END BATTLE -->";

	private Integer id;
	private Enemies enemies;
	private boolean battleStarted = false;
	private boolean canFlee = false;
	private boolean flee = false;
	private boolean fightEnemiesTogether = true;
	private PageState pageState;
	private List<BattleEffects> allBattleEffects;
	private String currentBattleMessage;

	private Map<BattleMessagePosition, String> additionalMessages;

	public enum BattleMessagePosition {
		BEGINNING, END
	}

	public BattleState(Element battleTag, PageState pageState) {
		this.pageState = pageState;

		this.id = new Integer(battleTag.getAttribute("id"));

		if (battleTag.hasAttribute("fightEnemiesTogether")) {
			this.fightEnemiesTogether = battleTag.getAttribute("fightEnemiesTogether").equalsIgnoreCase("true");
		}

		this.canFlee = battleTag.hasAttribute("canFlee") && battleTag.getAttribute("canFlee").equalsIgnoreCase("true");

		loadEnemies((Element) battleTag.getElementsByTagName("enemies").item(0));

		this.allBattleEffects = new ArrayList<BattleEffects>();
		loadEffects((Element) battleTag.getElementsByTagName("effects").item(0), pageState.getGameState().getPlayerState());

		this.additionalMessages = new HashMap<BattleMessagePosition, String>();
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
		NodeList enemyTags = enemiesTag.getElementsByTagName("enemy");
		for (int i = 0; i < enemyTags.getLength(); i++) {
			Element enemyTag = (Element) enemyTags.item(i);
			enemies.addEnemy(new EnemyState(enemyTag));
		}
	}

	private void loadEffects(Element effectsTag, PlayerState playerState) {
		loadEffectsFromTag(effectsTag);
		loadEffectsFromPlayerState(playerState);
	}

	private void loadEffectsFromTag(Element effectsTag) {
		BattleEffects initialBattleEffects = new BattleEffects();
		BattleEffectsLoaderUtil.loadBattleEffectsFromTag(initialBattleEffects, effectsTag);
		this.allBattleEffects.add(initialBattleEffects);
	}

	private void loadEffectsFromPlayerState(PlayerState playerState) {
		if (playerState.getNextBattleBattleEffects() != null) {
			for (BattleEffects playerBattleEffects : playerState.getNextBattleBattleEffects()) {
				this.allBattleEffects.add(playerBattleEffects);
			}
		}
	}

	private Integer getAttackStrengthFor(AbstractEntityState aState) {
		return new Integer(aState.getAttackStrength() + DiceRoller.rollDice(2));
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
				new LinkClosure(0).execute(this.pageState.getGameState());
			}
		}
	}

	public void doStartBattle() {
		for (BattleEffects battleEffects : new ArrayList<BattleEffects>(this.allBattleEffects)) {
			if (battleEffects.getStartBattle() != null) {
				battleEffects.getStartBattle().execute(this.pageState.getGameState());
			}
		}
	}

	public void doPlayerFlee() {
		for (BattleEffects battleEffects : new ArrayList<BattleEffects>(this.allBattleEffects)) {
			if (battleEffects.getPlayerFlee() != null) {
				battleEffects.getPlayerFlee().execute(this.pageState.getGameState());
			}
		}
	}

	public void doStartRound() {
		for (BattleEffects battleEffects : new ArrayList<BattleEffects>(this.allBattleEffects)) {
			if (battleEffects.getStartRound() != null) {
				battleEffects.getStartRound().execute(this.pageState.getGameState());
			}
		}
	}

	public void doEndRound() {
		for (BattleEffects battleEffects : new ArrayList<BattleEffects>(this.allBattleEffects)) {
			if (battleEffects.getEndRound() != null) {
				battleEffects.getEndRound().execute(this.pageState.getGameState());
			}
		}
	}

	public void doPlayerHit() {
		for (BattleEffects battleEffects : new ArrayList<BattleEffects>(this.allBattleEffects)) {
			if (battleEffects.getPlayerHit() != null) {
				battleEffects.getPlayerHit().execute(this.pageState.getGameState());
			}
		}
	}

	public void doEndBattle() {
		for (BattleEffects battleEffects : new ArrayList<BattleEffects>(this.allBattleEffects)) {
			if (battleEffects.getEndBattle() != null) {
				battleEffects.getEndBattle().execute(this.pageState.getGameState());
			}
		}
	}

	private void doDamage() {

		StringBuffer message = new StringBuffer(START_STRING);
		message.append("<p>");

		if (this.additionalMessages.containsKey(BattleMessagePosition.BEGINNING)) {
			message.append(this.additionalMessages.get(BattleMessagePosition.BEGINNING));
			message.append("<br>");
		}

		SortedMap<Integer, List<AbstractEntityState>> attackStrengths = new TreeMap<Integer, List<AbstractEntityState>>();

		PlayerState playerState = getPlayerState();

		Integer playerAttackStrength = getAttackStrengthFor(playerState);

		message.append("Your attack strength: " + playerAttackStrength + "<br>");

		List<AbstractEntityState> statesForStrength = new ArrayList<AbstractEntityState>();
		statesForStrength.add(playerState);
		attackStrengths.put(playerAttackStrength, statesForStrength);

		if (fightEnemiesTogether) {
			for (EnemyState thisEnemy : enemies.getEnemies()) {
				doAttackStrengthForEnemy(message, attackStrengths, thisEnemy);
			}
		} else {
			doAttackStrengthForEnemy(message, attackStrengths, enemies.getFirstNonDeadEnemy());
		}

		List<AbstractEntityState> highestAttackStrengthStates = attackStrengths.get(attackStrengths.lastKey());
		if (highestAttackStrengthStates.size() == 1) {
			AbstractEntityState highestState = highestAttackStrengthStates.get(0);
			if (highestState instanceof PlayerState) {
				EnemyState firstEnemyToAttack = enemies.getFirstNonDeadEnemy();
				firstEnemyToAttack.getStamina().adjustCurrentValueNoException(-2);

				message.append("You hit the " + firstEnemyToAttack.getName() + "!");
				if (firstEnemyToAttack.isDead()) {
					message.append(" You have killed the " + firstEnemyToAttack.getName() + "!");
				}
				message.append("<br>");
			} else {
				hitPlayer(playerState, ((EnemyState) highestState).isPoisonedWeapon(), message);
			}
		} else {
			// More than one person has the highest attack strength
			boolean foundPlayer = false;
			boolean foundPoisonedWeaponEnemy = false;
			for (AbstractEntityState state : highestAttackStrengthStates) {
				if (state instanceof PlayerState) {
					foundPlayer = true;
				} else {
					foundPoisonedWeaponEnemy |= ((EnemyState) state).isPoisonedWeapon();
				}
			}
			if (!foundPlayer) {
				hitPlayer(playerState, foundPoisonedWeaponEnemy, message);
			}
		}

		message.append("Your stamina after this round: " + playerState.getStamina().getCurrentValue() + "<br>");
		for (EnemyState enemy : enemies.getEnemies()) {
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

	private void doAttackStrengthForEnemy(StringBuffer message, SortedMap<Integer, List<AbstractEntityState>> attackStrengths, EnemyState thisEnemy) {
		if (thisEnemy.isDead()) {
			message.append(thisEnemy.getName() + " is dead.<br>");
		} else {
			Integer thisEnemysAttackStrength = getAttackStrengthFor(thisEnemy);

			message.append(thisEnemy.getName() + "'s attack strength: " + thisEnemysAttackStrength + "<br>");

			if (attackStrengths.containsKey(thisEnemysAttackStrength)) {
				attackStrengths.get(thisEnemysAttackStrength).add(thisEnemy);
			} else {
				List<AbstractEntityState> newStatesForStrength = new ArrayList<AbstractEntityState>();
				newStatesForStrength.add(thisEnemy);
				attackStrengths.put(thisEnemysAttackStrength, newStatesForStrength);
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

}