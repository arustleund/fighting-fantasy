package rustleund.fightingfantasy.gamesave;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameController;

import com.google.common.io.Files;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

public class LoadAction extends AbstractAction {

	@Serial
	private static final long serialVersionUID = -4230657785810027591L;

	private static final Log log = LogFactory.getLog(LoadAction.class);

	private final transient GameController gameController;
	private final transient ClosureLoader closureLoader;
	private final transient BattleEffectsLoader battleEffectsLoader;

	public LoadAction(GameController gameController, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		super("Load Saved Game");
		this.gameController = gameController;
		this.closureLoader = closureLoader;
		this.battleEffectsLoader = battleEffectsLoader;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home")));
		Container topLevelAncestor = ((JComponent) event.getSource()).getTopLevelAncestor();
		int result = fileChooser.showOpenDialog(topLevelAncestor);
		if (result == JFileChooser.APPROVE_OPTION) {
			File saveFile = fileChooser.getSelectedFile();

			SavedGame savedGame;
			try {
				String json = Files.asCharSource(saveFile, StandardCharsets.UTF_8).read();
				savedGame = SavedGameGson.forLoading(gameController, closureLoader, battleEffectsLoader).fromJson(json, SavedGame.class);
			} catch (IOException | RuntimeException exception) {
				// Gson reports a save it can't make sense of with all sorts of RuntimeExceptions, not just JsonParseException
				log.error("Unable to load the saved game " + saveFile, exception);
				JOptionPane.showMessageDialog(topLevelAncestor, "The saved game could not be loaded", "An error occurred", JOptionPane.ERROR_MESSAGE);
				return;
			}
			gameController.restoreGame(savedGame);
		}
	}
}
