/*
 * Created on Jun 17, 2004
 */
package rustleund.fightingfantasy.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.springframework.context.ConfigurableApplicationContext;
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
import com.google.common.base.Throwables;
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

		File gameFile = chooser.getSelectedFile();

		Path gameDirectory = getGameDirectory(gameFile);

		while (!gameDirectoryIsValid(gameDirectory, gameFile)) {
			openResult = chooser.showOpenDialog(frame);
			if (openResult == JFileChooser.CANCEL_OPTION) {
				System.exit(0);
			}
			gameFile = chooser.getSelectedFile();
			if (gameFile.isDirectory()) {
				gameDirectory = gameFile.toPath();
			}
		}

		// Create and set up the content pane.
		JComponent newContentPane = initializeGame(gameDirectory.toFile());
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private static Path getGameDirectory(File gameFile) {
		if (gameFile.isDirectory() && gameFile.exists()) {
			return gameFile.toPath();
		}
		try {
			Path result = Files.createTempDirectory("com.rustleund.fightingfantasy");
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				Main.clearGameDirectory(result, true);
			}));
			return result;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private static boolean gameDirectoryIsValid(Path gameDirectory, File gameFile) {
		if (gameFile.isFile()) {
			clearGameDirectory(gameDirectory, false);
			unzipToGameDirectory(gameDirectory, gameFile);
		}
		List<File> files = Lists.newArrayList(gameDirectory.toFile().listFiles());
		Function<File, String> function = (File input) -> {
			return input.getName();
		};
		return Iterables.any(files, Predicates.compose(Predicates.equalTo("config"), function)) &&
				Iterables.any(files, Predicates.compose(Predicates.equalTo("pages"), function));
	}

	private static void unzipToGameDirectory(Path gamesDirectory, File zipFile) {
		try {
			ZipFile zipFile2 = new ZipFile(zipFile);
			zipFile2.extractAll(gamesDirectory.toFile().getAbsolutePath());
		} catch (ZipException e) {
			Throwables.propagate(e);
		}
	}

	public static void clearGameDirectory(final Path gameDirectory, final boolean deleteGameToo) {
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
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
			throw Throwables.propagate(e1);
		}
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
		@SuppressWarnings("resource")
		ConfigurableApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringContext.class);
		applicationContext.registerShutdownHook();
		SpringContext springContext = applicationContext.getBean(SpringContext.class);

		ClosureLoader closureLoader = springContext.closureLoader();
		BattleEffectsLoader battleEffectsLoader = springContext.battleEffectsLoader();
		ItemUtil itemUtil = springContext.itemUtil();

		File configDirectory = new File(baseDirectory, "config");
		itemUtil.init(new File(configDirectory, "items.xml"));

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
