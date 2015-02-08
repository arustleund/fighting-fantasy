package rustleund.fightingfantasy.gamesave;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import rustleund.fightingfantasy.framework.base.GameController;
import rustleund.fightingfantasy.framework.base.Item;

import com.google.common.base.Charsets;
import com.google.common.collect.Range;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class LoadAction extends AbstractAction {

	private static final long serialVersionUID = -4230657785810027591L;

	private GameController gameController;

	public LoadAction(GameController gameController) {
		super("Load Saved Game");
		this.gameController = gameController;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home")));
		Container topLevelAncestor = ((JComponent) event.getSource()).getTopLevelAncestor();
		int result = fileChooser.showOpenDialog(topLevelAncestor);
		if (result == JFileChooser.APPROVE_OPTION) {
			File saveFile = fileChooser.getSelectedFile();

			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Item.class, new ItemDeserializer(gameController));
			gsonBuilder.registerTypeAdapter(Range.class, new IntegerRangeDeserializer());

			Gson gson = gsonBuilder.create();
			CharSource charSource = Files.asCharSource(saveFile, Charsets.UTF_8);

			try {
				SavedGame savedGame = gson.fromJson(charSource.read(), SavedGame.class);
				gameController.getGameState().setPlayerState(savedGame.getPlayerState());
				gameController.goToPage(savedGame.getPageId());
			} catch (JsonSyntaxException | IOException ex) {
				JOptionPane.showMessageDialog(topLevelAncestor, "The saved game could not be loaded", "An error occurred", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
