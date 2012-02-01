/*
 * Created on Mar 7, 2004
 */
package rustleund.nightdragon.framework;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import rustleund.nightdragon.framework.closures.AddItemClosure;
import rustleund.nightdragon.framework.closures.LinkClosure;
import rustleund.nightdragon.framework.closures.TestLuckClosure;

/**
 * @author rustlea
 */
public class GameController implements HyperlinkListener {

	private List<GameView> gameViews;
	private GameState gameState;

	public GameController() {
		this.gameViews = new ArrayList<GameView>();
	}

	public void addView(GameView view) {
		gameViews.add(view);
	}

	public void updateViews() {
		for (GameView view : this.gameViews) {
			view.update(this.gameState);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			this.gameState.clearMessage();

			String command = e.getURL().getHost();
			if (command.equals("link")) {
				loadPageIntoGameState(e.getURL().getPort());
			} else if (command.equals("buyItem")) {
				addItemToInventory(e.getURL().getPort());
			} else if (command.equals("testluck")) {
				testLuck(e.getURL().getPort());
			} else if (command.equals("domulti")) {
				doMultiCommand(e.getURL().getPort());
			} else if (command.equals("dobattle")) {
				doBattle(e.getURL().getPort());
			} else if (command.equals("doflee")) {
				doFlee(e.getURL().getPort());
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
		PageState pageState = this.gameState.getPageState();
		BattleState battleState = pageState.getBattle(battleId);

		this.gameState.setBattleInProgress(true);
		this.gameState.setBattleState(battleState);

		if (!battleState.battleIsOver()) {
			battleState.incrementGameState();
			if (battleState.getCurrentBattleMessage() != null) {
				pageState.replacePagetext(BattleState.START_STRING, BattleState.END_STRING, battleState.getCurrentBattleMessage());
			}
			if (battleState.getEnemies().areDead()) {
				this.gameState.getPlayerState().setNextBattleBattleEffects(null);
				this.gameState.setBattleInProgress(false);
				this.gameState.setBattleState(null);
				battleState.doEndBattle();
			}
		}
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

	private void testLuck(int testluckId) {
		new TestLuckClosure(testluckId).execute(gameState);
	}

	public void addItemToInventory(int itemId) {
		new AddItemClosure(itemId).execute(gameState);
	}

	private void loadPageIntoGameState(int pageNumber) {
		new LinkClosure(pageNumber).execute(gameState);
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState state) {
		gameState = state;
	}

}