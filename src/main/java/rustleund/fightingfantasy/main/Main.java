/*
 * Created on Jun 17, 2004
 */
package rustleund.fightingfantasy.main;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import rustleund.fightingfantasy.framework.GameController;
import rustleund.fightingfantasy.framework.GameState;
import rustleund.fightingfantasy.framework.GameView;
import rustleund.fightingfantasy.framework.PlayerState;
import rustleund.fightingfantasy.framework.Scale;
import rustleund.fightingfantasy.framework.closures.LinkClosure;
import rustleund.fightingfantasy.framework.util.ItemUtil;
import rustleund.fightingfantasy.ioc.SpringContext;

/**
 * @author rustlea
 */
public class Main {

	private static void createAndShowGUI(GameView gameView) {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("Fighting Fantasy");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		JComponent newContentPane = gameView;
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		final boolean isDebugMode = args.length > 0;

		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringContext.class);

		ItemUtil.getInstance().init();

		GameController gameController = new GameController();
		GameState gameState = new GameState();

		PlayerState playerState = new PlayerState("YOU");
		if (isDebugMode) {
			adjustScaleForDebug(playerState.getSkill(), 10);
			adjustScaleForDebug(playerState.getLuck(), 15);
			adjustScaleForDebug(playerState.getStamina(), 30);
			playerState.getGold().adjustCurrentValue(50);
		}

		gameState.setPlayerState(playerState);

		gameState.setMessage("-");

		gameController.setGameState(gameState);

		final GameView gameView = new GameView(gameController);

		gameController.addView(gameView);

		if (isDebugMode) {
			new LinkClosure(JOptionPane.showInputDialog("Page")).execute(gameState);
		} else {
			new LinkClosure("doStats").execute(gameState);
		}

		gameView.update(gameState);

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI(gameView);
			}
		});

	}

	private static void adjustScaleForDebug(Scale scale, int newUpper) {
		scale.adjustUpperBound(newUpper - scale.getUpperBound());
		scale.adjustCurrentValue(newUpper - scale.getCurrentValue());
	}
}
