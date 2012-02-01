/*
 * Created on Mar 7, 2004
 */
package rustleund.nightdragon.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import rustleund.nightdragon.framework.closures.AddItemClosure;
import rustleund.nightdragon.framework.closures.LinkClosure;
import rustleund.nightdragon.framework.closures.TestLuckClosure;

/**
 * @author rustlea
 */
public class GameController implements HyperlinkListener {

	DocumentBuilder builder = null;

	private List<GameView> gameViews = null;

	private GameState gameState = null;

	public GameController() {
		gameViews = new ArrayList<GameView>();
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	public void addView(GameView view) {
		gameViews.add(view);
	}

	public void updateViews() {
		for (GameView gameView : gameViews) {
			gameView.update(gameState);
		}
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
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
			gameState.clearMessage();
		}
	}

	public void useItem(Item item) {

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
		gameState.setBattleInProgress(true);
		PageState pageState = gameState.getPageState();
		BattleState battleState = pageState.getBattle(battleId);
		if (!battleState.battleIsOver()) {
			battleState.incrementGameState();
			if (battleState.getCurrentBattleMessage() != null) {
				pageState.replacePagetext(BattleState.START_STRING, BattleState.END_STRING, battleState.getCurrentBattleMessage());
			}
			if (battleState.getEnemies().areDead()) {
				battleState.getEndBattle().execute(gameState);
				gameState.setBattleInProgress(false);
			}
		}
	}

	private void doFlee(int battleId) {
		PageState pageState = gameState.getPageState();
		BattleState battleState = pageState.getBattle(battleId);
		battleState.getPlayerFlee().execute(gameState);
	}

	private void doMultiCommand(int multiCommandId) {
		List<Command> multiCommands = gameState.getPageState().getMultiCommands(multiCommandId);
		Iterator<Command> iter = multiCommands.iterator();
		boolean shouldContinue = true;
		while (shouldContinue && iter.hasNext()) {
			Command command = iter.next();
			command.execute(gameState);
			if (!command.executeWasSuccessful()) {
				shouldContinue = false;
			}
		}
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

	/**
	 * @return
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * @param state
	 */
	public void setGameState(GameState state) {
		gameState = state;
	}

}