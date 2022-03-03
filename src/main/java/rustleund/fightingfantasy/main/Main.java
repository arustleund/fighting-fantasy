/*
 * Created on Jun 17, 2004
 */
package rustleund.fightingfantasy.main;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameController;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.SwingGameView;
import rustleund.fightingfantasy.framework.base.ItemUtil;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure;
import rustleund.fightingfantasy.gamesave.BackAction;
import rustleund.fightingfantasy.gamesave.LoadAction;
import rustleund.fightingfantasy.gamesave.SaveAction;
import rustleund.fightingfantasy.ioc.SpringContext;

/**
 * @author rustlea
 */
public class Main {

	private static void createAndShowGUI(Path gameDirectory) {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("Fighting Fantasy");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		if (gameDirectory == null) {
			JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "Game Files or Directories";
				}

				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(".zip");
				}
			});
			int openResult = chooser.showOpenDialog(frame);
			if (openResult == JFileChooser.CANCEL_OPTION) {
				System.exit(0);
			}

			Path gameFile = chooser.getSelectedFile().toPath();

			gameDirectory = getGameDirectory(gameFile);

			while (!gameDirectoryIsValid(gameDirectory, gameFile)) {
				openResult = chooser.showOpenDialog(frame);
				if (openResult == JFileChooser.CANCEL_OPTION) {
					System.exit(0);
				}
				gameFile = chooser.getSelectedFile().toPath();
				if (Files.isDirectory(gameFile)) {
					gameDirectory = gameFile;
				}
			}
		}

		// Create and set up the content pane.
		JComponent newContentPane = initializeGame(gameDirectory, frame);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private static Path getGameDirectory(Path gameFile) {
		if (Files.isDirectory(gameFile) && Files.exists(gameFile)) {
			return gameFile;
		}
		try {
			Path result = Files.createTempDirectory("com.rustleund.fightingfantasy");
			Runtime.getRuntime().addShutdownHook(new Thread(() -> Main.clearGameDirectory(result, true)));
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean gameDirectoryIsValid(Path gameDirectory, Path gameFile) {
		if (Files.isRegularFile(gameFile)) {
			clearGameDirectory(gameDirectory, false);
			unzipToGameDirectory(gameDirectory, gameFile);
		}
		return Files.exists(gameDirectory.resolve("config")) && Files.exists(gameDirectory.resolve("pages"));
	}

	private static void unzipToGameDirectory(Path gamesDirectory, Path zipFile) {
		try {
			ZipFile zipFile2 = new ZipFile(zipFile.toFile());
			zipFile2.extractAll(gamesDirectory.toFile().getAbsolutePath());
		} catch (ZipException e) {
			throw new RuntimeException(e);
		}
	}

	public static void clearGameDirectory(final Path gameDirectory, final boolean deleteGameToo) {
		FileVisitor<Path> visitor = new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
				if (e == null) {
					if (deleteGameToo || !gameDirectory.equals(dir)) {
						Files.delete(dir);
					}
					return FileVisitResult.CONTINUE;
				}
				// directory iteration failed
				throw e;
			}
		};
		try {
			Files.walkFileTree(gameDirectory, visitor);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		Path gameDirectory = args.length > 0 ? new File(args[0]).toPath() : null;
		javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI(gameDirectory));
	}

	private static SwingGameView initializeGame(Path baseDirectory, JFrame frame) {
		@SuppressWarnings("resource")
		ConfigurableApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringContext.class);
		applicationContext.registerShutdownHook();
		SpringContext springContext = applicationContext.getBean(SpringContext.class);

		ClosureLoader closureLoader = springContext.closureLoader();
		BattleEffectsLoader battleEffectsLoader = springContext.battleEffectsLoader();
		ItemUtil itemUtil = springContext.itemUtil();

		Path configDirectory = baseDirectory.resolve("config");
		itemUtil.init(configDirectory.resolve("items.xml"));

		GameController gameController = new GameController(closureLoader, battleEffectsLoader, itemUtil);
		GameState gameState = new GameState();

		gameState.setBaseDirectory(baseDirectory);

		PlayerState playerState = new PlayerState("YOU", new ArrayList<>());
		gameState.setPlayerState(playerState);

		gameState.setMessage("-");

		gameController.setGameState(gameState);

		final SwingGameView gameView = new SwingGameView(gameController);

		gameController.addView(gameView);

		frame.setJMenuBar(buildMenuBar(gameState, gameController));

		new LinkClosure("doStats", closureLoader, battleEffectsLoader).execute(gameState);

		gameView.update(gameState);

		return gameView;
	}

	private static JMenuBar buildMenuBar(GameState gameState, GameController gameController) {
		JMenuBar result = new JMenuBar();
		buildFileMenu(gameState, gameController, result);
		buildNavigateMenu(result, gameController);
		return result;
	}

	private static void buildFileMenu(GameState gameState, GameController gameController, JMenuBar result) {
		JMenu menu = new JMenu("File");

		JMenuItem loadMenuItem = new JMenuItem(new LoadAction(gameController));
		loadMenuItem.setMnemonic(KeyEvent.VK_O);
		loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.META_DOWN_MASK));
		menu.add(loadMenuItem);

		JMenuItem saveMenuItem = new JMenuItem(new SaveAction(gameState));
		saveMenuItem.setMnemonic(KeyEvent.VK_S);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_DOWN_MASK));
		menu.add(saveMenuItem);

		result.add(menu);
	}

	private static void buildNavigateMenu(JMenuBar menuBar, GameController gameController) {
		JMenu menu = new JMenu("Navigate");

		JMenuItem backMenuItem = new JMenuItem(new BackAction(gameController));
		menu.add(backMenuItem);

		menuBar.add(menu);
	}
}
