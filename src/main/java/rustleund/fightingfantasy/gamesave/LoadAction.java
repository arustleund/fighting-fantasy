package rustleund.fightingfantasy.gamesave;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import rustleund.fightingfantasy.framework.base.GameController;
import rustleund.fightingfantasy.framework.base.Item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoadAction extends AbstractAction {

	private static final long serialVersionUID = -4230657785810027591L;

	private GameController gameController;

	public LoadAction(GameController gameController) {
		super("Load Saved Game");
		this.gameController = gameController;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Item.class, new ItemDeserializer());

		Gson gson = gsonBuilder.create();

		SavedGame savedGame = gson.fromJson("", SavedGame.class);

		gameController.getGameState().setPlayerState(savedGame.getPlayerState());
	}
}
