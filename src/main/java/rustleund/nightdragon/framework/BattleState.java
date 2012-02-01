/*
 * Created on Mar 8, 2004
 */
package rustleund.nightdragon.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rustleund.nightdragon.framework.closures.DynaBattleClosure;
import rustleund.nightdragon.framework.closures.LinkClosure;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;
import rustleund.nightdragon.framework.util.DiceRoller;

import com.google.common.collect.Lists;

/**
 * @author rustlea
 */
public class BattleState {

	public static final String START_STRING = "<!-- START BATTLE -->";

	public static final String END_STRING = "<!-- END BATTLE -->";

	private Integer id = null;

	private Enemies enemies = null;

	private boolean battleStarted = false;

	private boolean canFlee = false;

	private boolean flee = false;

	private boolean fightEnemiesTogether = true;

	private PageState pageState = null;

	private Command startBattle = null;

	private Command startRound = null;

	private Command playerFlee = null;

	private Command playerHit = null;

	private Command endBattle = null;

	private Command endRound = null;

	private String currentBattleMessage = null;

	public BattleState(Element battleTag, PageState pageState) {
		this.pageState = pageState;

		this.id = new Integer(battleTag.getAttribute("id"));

		if (battleTag.hasAttribute("fightEnemiesTogether")) {
			this.fightEnemiesTogether = battleTag.getAttribute("fightEnemiesTogether").equalsIgnoreCase("true");
		}

		this.canFlee = battleTag.hasAttribute("canFlee") && battleTag.getAttribute("canFlee").equalsIgnoreCase("true");

		loadEnemies((Element) battleTag.getElementsByTagName("enemies").item(0));
		loadEffects((Element) battleTag.getElementsByTagName("effects").item(0));
	}

	private void loadEnemies(Element enemiesTag) {
		this.enemies = new Enemies();
		NodeList enemyTags = enemiesTag.getElementsByTagName("enemy");
		for (int i = 0; i < enemyTags.getLength(); i++) {
			Element enemyTag = (Element) enemyTags.item(i);
			enemies.addEnemy(new EnemyState(enemyTag));
		}
	}

	private void loadEffects(Element effectsTag) {
		NodeList allEffects = effectsTag.getChildNodes();
		for (int i = 0; i < allEffects.getLength(); i++) {
			Node effectChild = allEffects.item(i);
			if (effectChild instanceof Element) {
				Element effectTag = (Element) effectChild;
				Command effect = null;
				if ("dynaEffect".equals(effectTag.getAttribute("type"))) {
					effect = new DynaBattleClosure(effectTag);
				} else {
					effect = AbstractCommandLoader.loadChainedClosure(effectTag);
				}
				try {
					PropertyUtils.setProperty(this, effectTag.getNodeName(), effect);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Integer getAttackStrengthFor(AbstractEntityState aState) {
		return new Integer(aState.getBaseAttackStrength() + aState.getSkill().getCurrentValue() + DiceRoller.rollDice(2));
	}

	public boolean battleIsOver() {
		return getPlayerState().isDead() || enemies.areDead() || flee;
	}

	public PlayerState getPlayerState() {
		return pageState.getGameState().getPlayerState();
	}

	private void executeClosure(Command closure) {
		if (closure != null) {
			closure.execute(getPageState().getGameState());
		}
	}

	public void incrementGameState() {
		if (!battleStarted) {
			executeClosure(startBattle);
			battleStarted = true;
			incrementGameState();
		} else if (flee) {
			executeClosure(playerFlee);
		} else {
			executeClosure(startRound);
			doDamage();
			executeClosure(endRound);

			if (getPlayerState().isDead()) {
				executeClosure(new LinkClosure(0));
			}
		}
	}

	private void doDamage() {

		StringBuffer message = new StringBuffer(START_STRING);
		message.append("<p>");

		SortedMap<Integer, List<AbstractEntityState>> attackStrengths = new TreeMap<Integer, List<AbstractEntityState>>();

		PlayerState playerState = getPlayerState();

		Integer playerAttackStrength = getAttackStrengthFor(playerState);

		message.append("Your attack strength: " + playerAttackStrength + "<br>");

		List<AbstractEntityState> statesForStrength = new ArrayList<AbstractEntityState>();
		statesForStrength.add(playerState);
		attackStrengths.put(playerAttackStrength, statesForStrength);

		if (fightEnemiesTogether) {
			for (EnemyState thisEnemy : enemies) {
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
				playerState.getStamina().adjustCurrentValueNoException(-2);
				message.append("You were hit!<br>");
				executeClosure(playerHit);
			}
		} else {
			// More than one person has the highest attack strength
			boolean foundPlayer = false;
			Iterator<AbstractEntityState> iter = highestAttackStrengthStates.iterator();
			while (!foundPlayer && iter.hasNext()) {
				AbstractEntityState state = iter.next();
				foundPlayer = state instanceof PlayerState;
			}
			if (!foundPlayer) {
				playerState.getStamina().adjustCurrentValueNoException(-2);
				message.append("You were hit!<br>");
				executeClosure(playerHit);
			}
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

		message.append("<a href=\"http://dobattle:" + this.id.toString() + "\">CONTINUE</a>");
		if (canFlee) {
			message.append("&nbsp;<a href=\"http://doflee:" + this.id.toString() + "\">FLEE</a>");
		}

		message.append("</p>");
		message.append(END_STRING);

		setCurrentBattleMessage(message.toString());

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
				attackStrengths.put(thisEnemysAttackStrength, Lists.<AbstractEntityState> newArrayList(thisEnemy));
			}
		}
	}

	public void setEndBattle(Command closure) {
		endBattle = closure;
	}

	public void setEndRound(Command closure) {
		endRound = closure;
	}

	public void setEnemies(Enemies enemies) {
		this.enemies = enemies;
	}

	public void setFlee(boolean flee) {
		this.flee = flee;
	}

	public void setPlayerFlee(Command closure) {
		playerFlee = closure;
	}

	public void setPlayerHit(Command closure) {
		playerHit = closure;
	}

	public void setStartBattle(Command closure) {
		startBattle = closure;
	}

	public void setStartRound(Command closure) {
		startRound = closure;
	}

	public String getCurrentBattleMessage() {
		return currentBattleMessage;
	}

	public void setCurrentBattleMessage(String currentBattleMessage) {
		this.currentBattleMessage = currentBattleMessage;
	}

	public Command getEndBattle() {
		return endBattle;
	}

	public Command getEndRound() {
		return endRound;
	}

	public Enemies getEnemies() {
		return enemies;
	}

	public Command getPlayerFlee() {
		return playerFlee;
	}

	public Command getPlayerHit() {
		return playerHit;
	}

	public Command getStartBattle() {
		return startBattle;
	}

	public Command getStartRound() {
		return startRound;
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

}