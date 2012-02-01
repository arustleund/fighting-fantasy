/*
 * Created on Jun 17, 2004
 */
package rustleund.nightdragon.main;

import javax.swing.JComponent;
import javax.swing.JFrame;

import rustleund.nightdragon.framework.GameController;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.GameView;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.closures.LinkClosure;
import rustleund.nightdragon.framework.util.ItemUtil;

/**
 * @author rustlea
 */
public class Main {

	private static void createAndShowGUI(GameView gameView) {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("Night Dragon");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		JComponent newContentPane = gameView;
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		
		ItemUtil.getInstance().init();

		GameController gameController = new GameController();
		GameState gameState = new GameState();

		PlayerState playerState = new PlayerState("YOU");

		gameState.setPlayerState(playerState);
		
		gameState.setMessage("-");

		gameController.setGameState(gameState);

		final GameView gameView = new GameView(gameController);

		gameController.addView(gameView);
		
		new LinkClosure("doStats").execute(gameState);
		
		gameView.update(gameState);

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(gameView);
			}
		});

	}
}
