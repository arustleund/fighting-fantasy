/*
 * Created on Mar 8, 2004
 */
package rustleund.fightingfantasy.framework.base;

import org.w3c.dom.Element;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.DisplayTextClosure;
import rustleund.fightingfantasy.framework.util.DiceRoller;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author rustlea
 */
public class BattleState {

    public static final String START_STRING = "<!-- START BATTLE -->";
    public static final String END_STRING = "<!-- END BATTLE -->";

    private final BattleEffectsLoader battleEffectsLoader;
    private final ClosureLoader closureLoader;

    private Integer id;
    private Enemies enemies;
    private boolean battleStarted = false;
    private final boolean canFlee;
    private boolean fightEnemiesTogether = true;
    private final PageState pageState;
    private final List<BattleEffects> allBattleEffects;
    private String currentBattleMessage;
    private AttackStrengths currentAttackStrengths;
    private int playerHitCount;
    private int battleRound = 1;
    private boolean testedLuckThisRound = false;

    private final Map<BattleMessagePosition, String> additionalMessages;
    private final Collection<BattleEffects> battleEffectsForNextRound = new ArrayList<>();

    public enum BattleMessagePosition {
        BEGINNING, END, IMMEDIATE_END
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

        loadEnemies(XMLUtilKt.getChildElementByName(battleTag, "enemies"));

        this.allBattleEffects = new ArrayList<>();

        Element effectsTag = XMLUtilKt.getChildElementByName(battleTag, "effects");
        if (effectsTag != null) {
            loadEffects(effectsTag);
        }

        this.additionalMessages = new EnumMap<>(BattleMessagePosition.class);

        Integer displayTextOnEnd = XMLUtilKt.optionalIntAttribute(battleTag, "displayTextOnEnd");
        if (displayTextOnEnd != null) {
            BattleEffects displayTextEffects = new BattleEffects();
            displayTextEffects.setEndBattle(new DisplayTextClosure(displayTextOnEnd));
            allBattleEffects.add(displayTextEffects);
        }
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

    public void immediatelyAppendBattleMessage(String message) {
        appendAtEndOfBattleMessage(message);
        updateBattleMessageOnPageState();
    }

    private void loadEnemies(Element enemiesTag) {
        this.enemies = new Enemies();
        XMLUtilKt.getChildElementsByName(enemiesTag, "enemy").iterator()
                .forEachRemaining(this::addEnemyToEnemies);
    }

    private void addEnemyToEnemies(Element e) {
        EnemyState enemy = new EnemyState(e, closureLoader);
        if (enemy.isOfType("self")) {
            enemy.setSkill(getPlayerState().getSkill().deepCopy());
            enemy.setStamina(getPlayerState().getStamina().deepCopy());
        }
        enemies.addEnemy(enemy);
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
        return !getPlayerState().isDead() && !enemies.areDead();
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
        } else {
            doStartRound();
            doDamage();
            doEndRound();

            if (getPlayerState().isDead()) {
                pageState.getGameState().playerHasDied();
            }
        }
    }

    public void doStartBattle() {
        doBattleStage(this.allBattleEffects, BattleEffects::getStartBattle);
        doBattleStage(itemBattleEffects(), BattleEffects::getStartBattle);
    }

    public void doPlayerFlee() {
        this.battleStarted = false;
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

    private void doEndBattle() {
        this.battleStarted = false;
        doBattleStage(this.allBattleEffects, BattleEffects::getEndBattle);
        doBattleStage(itemBattleEffects(), BattleEffects::getEndBattle);
    }

    public void doAfterPossibleStaminaChange() {
        updateBattleMessageOnPageState();
        if ((getEnemies().areDead() || getPlayerState().isDead()) && battleStarted) {
            doEndBattle();
            getPageState().getGameState().endBattle();
        }
    }

    private void updateBattleMessageOnPageState() {
        if (getCurrentBattleMessage() != null) {
            pageState.replacePagetext(BattleState.START_STRING, BattleState.END_STRING, getCurrentBattleMessage());
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
                doEnemyHitOnTestLuck(lucky, message);
            } else {
                doPlayerHitOnTestLuck(lucky, message);
            }
            if (existingMessageBuilder == null) {
                appendAtEndOfBattleMessage(message.toString());
                doAfterPossibleStaminaChange();
            }
        }
    }

    private void doPlayerHitOnTestLuck(boolean lucky, StringBuilder message) {
        if (lucky) {
            message.append("You will take 1 less point of damage!<br>");
            getPlayerState().getStamina().adjustCurrentValueNoException(1);
        } else {
            message.append("You will take 1 more point of damage!<br>");
            getPlayerState().getStamina().adjustCurrentValueNoException(-1);
        }
        addPlayerStamina(message, getPlayerState());
    }

    private void doEnemyHitOnTestLuck(boolean lucky, StringBuilder message) {
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
    }

    private Collection<BattleEffects> itemBattleEffects() {
        Collection<Item> items = pageState.getGameState().getPlayerState().getItems().values();
        Stream<BattleEffects> itemBattleEffects = items.stream().map(Item::getBattleEffects);
        return itemBattleEffects.filter(Objects::nonNull).toList();
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

        BattleRoundResults battleRoundResults =
                BattleRoundResultsKt.determineBattleRoundResults(playerState, enemies, fightEnemiesTogether, () -> DiceRoller.rollDiceResult(2), battleRound);
        this.currentAttackStrengths = battleRoundResults.getAttackStrengths();

        if (battleIsNotOver()) {
            StringBuilder battleLuckMessage = new StringBuilder();
            if (battleRoundResults.getAttackStrengths().getPlayerWon()) {
                EnemyState firstEnemyToAttack = enemies.getFirstNonDeadEnemy();
                int damage = -2 - playerState.getDamageModifier();
                firstEnemyToAttack.getStamina().adjustCurrentValueNoException(damage);

                if (firstEnemyToAttack.isDead()) {
                    if (firstEnemyToAttack.getEnemyKilled() != null) {
                        firstEnemyToAttack.getEnemyKilled().execute(pageState.getGameState());
                    }
                } else {
                    if (firstEnemyToAttack.getEnemyHit() != null) {
                        firstEnemyToAttack.getEnemyHit().execute(pageState.getGameState());
                    }
                    if (!playerState.getLuck().isEmpty()) {
                        battleLuckMessage.append("<a href=\"http://testluckbattle:0\"><i>Test Your Luck</i></a> to try to do add an additional 2 points of damage");
                    }
                }
            } else if (battleRoundResults.getAttackStrengths().getPlayerHit()) {
                hitPlayer(playerState, battleRoundResults.getAttackStrengths().winningEnemyPoisonDamage(battleRound), battleLuckMessage);
            }

            BattleStateSupportKt.addBattleTable(message, playerState, enemies, battleRoundResults);
            message.append(battleLuckMessage);
            message.append("<br>");
        }

        if (this.additionalMessages.containsKey(BattleMessagePosition.END)) {
            message.append(this.additionalMessages.get(BattleMessagePosition.END));
            message.append("<br>");
        }

        message.append("<br><a href=\"http://dobattle:").append(this.id.toString()).append("\">CONTINUE</a>");
        if (canFlee) {
            message.append("&nbsp;<a href=\"http://doflee:").append(this.id).append("\">FLEE</a>");
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
        message.append("Your stamina after this round: ").append(playerState.getStamina().getCurrentValue()).append("<br>");
    }

    private void appendEnemyStamina(StringBuilder message, EnemyState enemy) {
        if (enemy.isDead()) {
            message.append(enemy.getName()).append(" is dead.");
        } else {
            message.append(enemy.getName()).append("'s stamina after this round: ").append(enemy.getStamina().getCurrentValue());
        }
        message.append("<br>");
    }

    private void hitPlayer(PlayerState playerState, int poisonDamage, StringBuilder message) {
        this.playerHitCount++;
        if (poisonDamage > 0 && !playerState.isPoisonImmunity()) {
            playerState.takePoisonDamage(poisonDamage);
        }

        int staminaBeforeHit = playerState.getStamina().getCurrentValue();
        playerState.getStamina().adjustCurrentValueNoException(-2);
        doPlayerHit();

        if (!playerState.getLuck().isEmpty() && staminaBeforeHit >= 2) {
            if (playerState.isDead()) {
                message.append("Your Stamina was reduced to 0, Testing Your Luck to save your life...<br>");
                doTestLuck(false, message);
            } else {
                message.append("<a href=\"http://testluckbattle:1\"><i>Test Your Luck</i></a> to try to reduce 1 points of damage<br>");
            }
        }
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<BattleEffects> getAllBattleEffects() {
        return this.allBattleEffects;
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