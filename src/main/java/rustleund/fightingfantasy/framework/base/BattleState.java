/*
 * Created on Mar 8, 2004
 */
package rustleund.fightingfantasy.framework.base;

import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.DisplayTextClosure;
import rustleund.fightingfantasy.framework.util.DiceRoller;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;
import static rustleund.fightingfantasy.framework.base.XMLUtilKt.*;

/**
 * @author rustlea
 */
public class BattleState {

    public static final String START_STRING = "<!-- START BATTLE -->";
    public static final String END_STRING = "<!-- END BATTLE -->";

    private final int id;
    private final Enemies enemies;
    private final boolean canFlee;
    private final boolean fightEnemiesTogether;
    private final PageState pageState;
    private final List<BattleEffects> allBattleEffects;

    private boolean battleStarted = false;
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
        this(
                pageState,
                Integer.parseInt(battleTag.getAttribute("id")),
                booleanAttribute(battleTag, "fightEnemiesTogether", true),
                booleanAttribute(battleTag, "canFlee", false),
                optionalIntAttribute(battleTag, "displayTextOnEnd"),
                loadEnemies(getChildElementByName(battleTag, "enemies"), pageState.getGameState().getPlayerState(), closureLoader),
                createAndLoadMainBattleEffects(battleTag, battleEffectsLoader)
        );
    }

    @VisibleForTesting
    BattleState(
            PageState pageState,
            int id, boolean fightEnemiesTogether, boolean canFlee, @Nullable Integer displayTextOnEnd,
            Enemies enemies,
            BattleEffects mainBattleEffects
    ) {
        this.pageState = pageState;

        this.id = id;
        this.canFlee = canFlee;
        this.fightEnemiesTogether = fightEnemiesTogether;

        this.enemies = enemies;

        this.allBattleEffects = new ArrayList<>();
        this.allBattleEffects.add(mainBattleEffects);

        this.additionalMessages = new EnumMap<>(BattleMessagePosition.class);

        if (displayTextOnEnd != null) {
            BattleEffects displayTextEffects = new BattleEffects();
            displayTextEffects.setEndBattle(new DisplayTextClosure(displayTextOnEnd));
            this.allBattleEffects.add(displayTextEffects);
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

    private static Enemies loadEnemies(Element enemiesTag, PlayerState playerState, ClosureLoader closureLoader) {
        Enemies result = new Enemies();
        Iterator<Element> enemyElementsIterator = XMLUtilKt.getChildElementsByName(enemiesTag, "enemy").iterator();
        stream(spliteratorUnknownSize(enemyElementsIterator, Spliterator.ORDERED), false)
                .map(e -> loadEnemy(e, playerState, closureLoader))
                .forEach(result::addEnemy);
        return result;
    }

    private static EnemyState loadEnemy(Element e, PlayerState playerState, ClosureLoader closureLoader) {
        EnemyState enemy = new EnemyState(e, closureLoader);
        if (enemy.isOfType("self")) {
            enemy.setSkill(playerState.getSkill().deepCopy());
            enemy.setStamina(playerState.getStamina().deepCopy());
        }
        return enemy;
    }

    @NotNull
    private static BattleEffects createAndLoadMainBattleEffects(Element battleTag, BattleEffectsLoader battleEffectsLoader) {
        Element effectsTag = getChildElementByName(battleTag, "effects");
        if (effectsTag == null) {
            return new BattleEffects();
        }
        return createAndLoadBattleEffectsFromEffectsTag(effectsTag, battleEffectsLoader);
    }

    private static BattleEffects createAndLoadBattleEffectsFromEffectsTag(Element effectsTag, BattleEffectsLoader battleEffectsLoader) {
        BattleEffects result = new BattleEffects();
        battleEffectsLoader.loadBattleEffectsFromTag(result, effectsTag);
        return result;
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

    public void doEndBattle() {
        this.battleStarted = false;
        doBattleStage(this.allBattleEffects, BattleEffects::getEndBattle);
        doBattleStage(itemBattleEffects(), BattleEffects::getEndBattle);
        getPageState().getGameState().endBattle();
    }

    public void doAfterPossibleStaminaChange() {
        updateBattleMessageOnPageState();
        if ((getEnemies().areDead() || getPlayerState().isDead()) && battleStarted) {
            doEndBattle();
        }
    }

    private void updateBattleMessageOnPageState() {
        if (this.currentBattleMessage != null) {
            pageState.replacePagetext(BattleState.START_STRING, BattleState.END_STRING, this.currentBattleMessage);
        }
    }

    public void doTestLuck(boolean enemyHit, StringBuilder existingMessageBuilder) {
        if (!testedLuckThisRound) {
            boolean lucky = getPlayerState().testLuck(null);
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

        if (battleIsNotOver()) {
            BattleRoundResults battleRoundResults =
                    BattleRoundResultsKt.determineBattleRoundResults(playerState, enemies, fightEnemiesTogether, () -> DiceRoller.rollDiceResult(2), battleRound);
            this.currentAttackStrengths = battleRoundResults.getAttackStrengths();

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

        message.append("<br><a href=\"http://dobattle:").append(this.id).append("\">CONTINUE</a>");
        if (canFlee) {
            message.append("&nbsp;<a href=\"http://doflee:").append(this.id).append("\">FLEE</a>");
        }

        message.append("</p>");

        message.append(END_STRING);

        this.currentBattleMessage = message.toString();
    }

    private void appendAtEndOfBattleMessage(String message) {
        StringBuilder newBattleMessage = new StringBuilder(this.currentBattleMessage);
        newBattleMessage.setLength(newBattleMessage.length() - END_STRING.length());
        newBattleMessage.append("<br>");
        newBattleMessage.append(message);
        newBattleMessage.append(END_STRING);
        this.currentBattleMessage = newBattleMessage.toString();
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

    public Enemies getEnemies() {
        return enemies;
    }

    public PageState getPageState() {
        return pageState;
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