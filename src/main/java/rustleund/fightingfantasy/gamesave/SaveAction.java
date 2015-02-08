package rustleund.fightingfantasy.gamesave;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.Item;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SaveAction extends AbstractAction {

	private static final long serialVersionUID = 4973015396772966933L;

	private GameState gameState;

	public SaveAction(GameState gameState) {
		super("Save");
		this.gameState = gameState;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File saveDir = new File(System.getProperty("user.home"), "fighting-fantasy-saves");
		saveDir.mkdirs();

		String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-save.json";

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Item.class, new ItemSerializer());
		Gson gson = gsonBuilder.create();

		try {
			String json = gson.toJson(new SavedGame(this.gameState.getPageState().getPageName(), this.gameState.getPlayerState()));
			Files.asCharSink(new File(saveDir, fileName), Charsets.UTF_8).write(json);
		} catch (IOException e1) {
			Throwables.propagate(e1);
		}
	}
}
