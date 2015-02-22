/*
 * Created on Mar 7, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.AddItemClosure;
import rustleund.fightingfantasy.framework.closures.impl.DoBattleClosure;
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure;

/**
 * @author rustlea
 */
public class GameController implements HyperlinkListener {

	private ClosureLoader closureLoader;
	private BattleEffectsLoader battleEffectsLoader;
	private ItemUtil itemUtil;

	private List<GameView> gameViews;
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
		for (GameView view : this.gameViews) {
			view.update(this.gameState);
		}
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			this.gameState.clearMessage();

			String command = e.getURL().getHost();
			int port = e.getURL().getPort();
			if (command.equals("link")) {
				loadPageIntoGameState(port);
			} else if (command.equals("buyItem")) {
				addItemToInventory(port);
			} else if (command.equals("domulti")) {
				doMultiCommand(port);
			} else if (command.equals("dobattle")) {
				doBattle(port);
			} else if (command.equals("doflee")) {
				doFlee(port);
			}

			updateViews();
			this.gameState.clearMessage();
		}
	}

	public void useItem(Item item) {
		gameState.clearMessage();
		item.useItem(gameState);

		updateViews();
		gameState.clearMessage();
	}

	public void eatMeal() {
		if (gameState.isBattleInProgress()) {
			gameState.setMessage("You cannot eat a meal during battle");
		} else {
			PlayerState playerState = gameState.getPlayerState();
			Scale provisions = playerState.getProvisions();
			if (provisions.getCurrentValue() > 0) {
				provisions.adjustCurrentValueNoException(-1);
				playerState.getStamina().adjustCurrentValueNoException(4);
			}
		}
		updateViews();
		gameState.clearMessage();
	}

	private void doBattle(int battleId) {
		new DoBattleClosure(battleId).execute(gameState);
	}

	private void doFlee(int battleId) {
		PageState pageState = gameState.getPageState();
		BattleState battleState = pageState.getBattle(battleId);
		battleState.doPlayerFlee();
	}

	private void doMultiCommand(int multiCommandId) {
		Closure multiCommand = this.gameState.getPageState().getMultiCommands(multiCommandId);
		multiCommand.execute(this.gameState);
	}

	public void addItemToInventory(int itemId) {
		new AddItemClosure(itemId, itemUtil).execute(gameState);
	}

	private void loadPageIntoGameState(int pageNumber) {
		goToPage("" + pageNumber);
	}

	public void goToPage(String pageId) {
		new LinkClosure(pageId, closureLoader, battleEffectsLoader).execute(gameState);
		updateViews();
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