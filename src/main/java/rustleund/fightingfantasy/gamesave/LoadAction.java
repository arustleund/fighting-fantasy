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

import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameController;
import rustleund.fightingfantasy.framework.base.Item;

import com.google.common.collect.Range;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure;

public class LoadAction extends AbstractAction {

	@Serial
	private static final long serialVersionUID = -4230657785810027591L;

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

			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Item.class, new ItemDeserializer(gameController));
			gsonBuilder.registerTypeAdapter(Range.class, new IntegerRangeDeserializer());
			gsonBuilder.registerTypeAdapter(Closure.class, new ClosureDeserializer());
			gsonBuilder.registerTypeAdapter(LinkClosure.class, new LinkClosureDeserializer(closureLoader, battleEffectsLoader));

			Gson gson = gsonBuilder.create();
			CharSource charSource = Files.asCharSource(saveFile, StandardCharsets.UTF_8);

			try {
				SavedGame savedGame = gson.fromJson(charSource.read(), SavedGame.class);
				gameController.getGameState().setPlayerState(savedGame.playerState());
				gameController.goToPage(savedGame.pageId());
			} catch (JsonSyntaxException | IOException ex) {
				JOptionPane.showMessageDialog(topLevelAncestor, "The saved game could not be loaded", "An error occurred", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
