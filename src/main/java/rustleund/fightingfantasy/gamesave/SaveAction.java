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

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.Item;

import com.google.common.collect.Range;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SaveAction extends AbstractAction {

	@Serial
	private static final long serialVersionUID = 4973015396772966933L;

	private final transient GameState gameState;

	public SaveAction(GameState gameState) {
		super("Save");
		this.gameState = gameState;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home")));
		Container topLevelAncestor = ((JComponent) event.getSource()).getTopLevelAncestor();
		int result = fileChooser.showSaveDialog(topLevelAncestor);
		if (result == JFileChooser.APPROVE_OPTION) {
			File saveFile = fileChooser.getSelectedFile();
			SavedGame latestGameProgress = this.gameState.getLatestGameProgress();
			if (latestGameProgress != null) {
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Item.class, new ItemSerializer());
				gsonBuilder.registerTypeAdapter(Range.class, new IntegerRangeSerializer());
				Gson gson = gsonBuilder.create();
				String json = gson.toJson(latestGameProgress);
				try {
					Files.asCharSink(saveFile, StandardCharsets.UTF_8).write(json);
				} catch (IOException ioException) {
					JOptionPane.showMessageDialog(topLevelAncestor, "The game could not be saved", "An error occurred", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
