package rustleund.fightingfantasy.gamesave;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import rustleund.fightingfantasy.framework.base.GameController;
import rustleund.fightingfantasy.framework.base.GameState;

public class BackAction extends AbstractAction {

	private static final long serialVersionUID = -9111296685764704027L;

	private GameController gameController;

	public BackAction(GameController gameController) {
		super("Back");
		this.gameController = gameController;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GameState gameState = this.gameController.getGameState();
		SavedGame latestGameProgress = gameState.popGameProgress();
		latestGameProgress = gameState.popGameProgress();
		if (latestGameProgress != null) {
			gameState.setPlayerState(latestGameProgress.getPlayerState());
			this.gameController.goToPage(latestGameProgress.getPageId());
		}
	}
}
