/*
 * Created on Mar 7, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.util.ArrayList;
import java.util.List;

import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.AddItemClosure;
import rustleund.fightingfantasy.framework.closures.impl.DoBattleClosure;
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure;

/**
 * @author rustlea
 */
public class GameController {

	private final ClosureLoader closureLoader;
	private final BattleEffectsLoader battleEffectsLoader;
	private final ItemUtil itemUtil;

	private final List<GameView> gameViews;
	private GameState gameState;

	public GameController(ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader, ItemUtil itemUtil) {
		this.gameViews = new ArrayList<>();
		this.closureLoader = closureLoader;
		this.battleEffectsLoader = battleEffectsLoader;
		this.itemUtil = itemUtil;
	}

	public void addView(GameView view) {
		gameViews.add(view);
	}

	public void updateViews() {
		this.gameViews.forEach(view -> view.update(this.gameState));
	}

	private void doGameStateUpdatingAction(GameAction action) {
		gameState.clearMessage();
		action.doAction();
		updateViews();
		gameState.clearMessage();
	}

	public void doTestLuckInBattle(boolean enemyHit) {
		doGameStateUpdatingAction(() -> gameState.getBattleState().doTestLuck(enemyHit, null));
	}

	public void useItem(Item item) {
		doGameStateUpdatingAction(() -> item.useItem(gameState));
	}

	public void eatMeal() {
		if (gameState.isBattleInProgress()) {
			doGameStateUpdatingAction(() -> gameState.setMessage("You cannot eat a meal during battle"));
		} else {
			doGameStateUpdatingAction(() -> {
				PlayerState playerState = gameState.getPlayerState();
				Scale provisions = playerState.getProvisions();
				if (provisions.getCurrentValue() > 0) {
					provisions.adjustCurrentValueNoException(-1);
					playerState.getStamina().adjustCurrentValueNoException(4);
				}
			});
		}
	}

	public void doBattle(int battleId) {
		doGameStateUpdatingAction(() -> new DoBattleClosure(battleId).execute(gameState));
	}

	public void doFlee(int battleId) {
		doGameStateUpdatingAction(() -> {
			PageState pageState = gameState.getPageState();
			BattleState battleState = pageState.getBattle(battleId);
			battleState.doPlayerFlee();
		});
	}

	public void doMultiCommand(int multiCommandId) {
        doGameStateUpdatingAction(() -> {
            Closure multiCommand = this.gameState.getPageState().getMultiCommands(multiCommandId);
            multiCommand.execute(this.gameState);
        });
	}

	public void addItemToInventory(int itemId) {
        doGameStateUpdatingAction(() -> new AddItemClosure(itemId, itemUtil).execute(gameState));
	}

	public void goToPage(String pageId) {
        doGameStateUpdatingAction(() -> new LinkClosure(pageId, closureLoader, battleEffectsLoader).execute(gameState));
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState state) {
		gameState = state;
	}

	public ItemUtil getItemUtil() {
		return this.itemUtil;
	}
}