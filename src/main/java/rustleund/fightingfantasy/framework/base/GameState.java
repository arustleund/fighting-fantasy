/*
 * Created on Jun 17, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.nio.file.Path;
import java.util.LinkedList;

import org.jetbrains.annotations.Nullable;
import rustleund.fightingfantasy.framework.closures.impl.DisplayTextClosure;
import rustleund.fightingfantasy.gamesave.SavedGame;

import com.google.common.collect.Lists;

/**
 * @author rustlea
 */
public class GameState {

	private Path baseDirectory;
	private PlayerState playerState;
	private PageState pageState;
	private BattleState battleState;
	private String message;
	private boolean isBattleInProgress;
	private boolean pageLoaded;
	private final LinkedList<SavedGame> gameProgress = Lists.newLinkedList();

	public void addGameProgress(SavedGame savedGame) {
		this.gameProgress.add(savedGame);
	}

	public SavedGame popGameProgress() {
		if (this.gameProgress.isEmpty()) {
			return null;
		}
		return this.gameProgress.removeLast();
	}

	/**
	 * @return The most recently saved {@link SavedGame}, or <code>null</code> if nothing has been saved so far
	 */
	public SavedGame getLatestGameProgress() {
		if (this.gameProgress.isEmpty()) {
			return null;
		}
		return this.gameProgress.getLast();
	}

	/**
	 * @return The {@link Path} representing the directory that images are stored in
	 */
	public Path getImagesDirectory() {
		return this.baseDirectory.resolve("images");
	}

	/**
	 * @return The {@link Path} representing the directory that pages are stored in
	 */
	public Path getPagesDirectory() {
		return this.baseDirectory.resolve("pages");
	}

	public void setBaseDirectory(Path baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public void clearMessage() {
		message = "-";
	}

	public void endBattle() {
		if (!playerState.isDead()) {
			getPlayerState().setNextBattleBattleEffects(null);
			setBattleInProgress(false);
			setBattleState(null);
		}
	}

	public void playerHasDied() {
		if (playerState.getOnPlayerDeathClosure() == null) {
			new DisplayTextClosure("<p>You have died, and your adventure ends here.</p>").execute(this);
		} else {
			playerState.getOnPlayerDeathClosure().execute(this);
			playerState.setOnPlayerDeathClosure(null);
		}
	}

	public PlayerState getPlayerState() {
		return playerState;
	}

	public void setPlayerState(PlayerState state) {
		playerState = state;
	}

	public PageState getPageState() {
		return pageState;
	}

	public void setPageState(PageState state) {
		pageState = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String string) {
		message = string;
	}

	public boolean isBattleInProgress() {
		return isBattleInProgress;
	}

	public void setBattleInProgress(boolean isBattleInProgress) {
		this.isBattleInProgress = isBattleInProgress;
	}

	@Nullable
	public BattleState getBattleState() {
		return this.battleState;
	}

	public void setBattleState(BattleState battleState) {
		this.battleState = battleState;
	}

	public boolean isPageLoaded() {
		return this.pageLoaded;
	}

	public void setPageLoaded(boolean pageLoaded) {
		this.pageLoaded = pageLoaded;
	}

}