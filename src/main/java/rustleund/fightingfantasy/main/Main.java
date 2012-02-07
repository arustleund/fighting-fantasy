/*
 * Created on Jun 17, 2004
 */
package rustleund.fightingfantasy.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameController;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.GameView;
import rustleund.fightingfantasy.framework.base.Item;
import rustleund.fightingfantasy.framework.base.ItemUtil;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure;
import rustleund.fightingfantasy.ioc.SpringContext;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author rustlea
 */
public class Main {

	private static void createAndShowGUI() {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("Fighting Fantasy");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir") + "/src/main/resources");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(frame);

		File baseDirectory = chooser.getSelectedFile();
		while (!baseDirectoryIsValid(baseDirectory)) {
			chooser.showOpenDialog(frame);
			baseDirectory = chooser.getSelectedFile();
		}

		// Create and set up the content pane.
		JComponent newContentPane = initializeGame(baseDirectory);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private static boolean baseDirectoryIsValid(File baseDirectory) {
		if (baseDirectory.isDirectory()) {
			List<File> files = Lists.newArrayList(baseDirectory.listFiles());
			Function<File, String> function = new Function<File, String>() {
				@Override
				public String apply(File input) {
					return input.getName();
				}
			};
			return Iterables.any(files, Predicates.compose(Predicates.equalTo("config"), function)) &&
					Iterables.any(files, Predicates.compose(Predicates.equalTo("pages"), function));
		}
		return false;
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});

	}

	private static GameView initializeGame(File baseDirectory) {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringContext.class);
		SpringContext springContext = applicationContext.getBean(SpringContext.class);

		ClosureLoader closureLoader = springContext.closureLoader();
		BattleEffectsLoader battleEffectsLoader = springContext.battleEffectsLoader();
		ItemUtil itemUtil = springContext.itemUtil();

		itemUtil.init(new File(baseDirectory, "config/items.xml"));

		GameController gameController = new GameController(closureLoader, battleEffectsLoader, itemUtil);
		GameState gameState = new GameState();

		gameState.setBaseDirectory(baseDirectory);

		PlayerState playerState = new PlayerState("YOU", new ArrayList<Item>());
		gameState.setPlayerState(playerState);

		gameState.setMessage("-");

		gameController.setGameState(gameState);

		final GameView gameView = new GameView(gameController);

		gameController.addView(gameView);

		new LinkClosure("doStats", closureLoader, battleEffectsLoader).execute(gameState);

		gameView.update(gameState);

		return gameView;
	}

}
