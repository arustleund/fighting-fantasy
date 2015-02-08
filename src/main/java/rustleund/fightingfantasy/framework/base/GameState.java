/*
 * Created on Jun 17, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.io.File;
import java.util.LinkedList;

import rustleund.fightingfantasy.gamesave.SavedGame;

import com.google.common.collect.Lists;

/**
 * @author rustlea
 */
public class GameState {

	private File baseDirectory;
	private PlayerState playerState;
	private PageState pageState;
	private BattleState battleState;
	private String message;
	private boolean isBattleInProgress;
	private boolean pageLoaded;
	private LinkedList<SavedGame> gameProgress = Lists.newLinkedList();

	public void addGameProgress(SavedGame savedGame) {
		this.gameProgress.add(savedGame);
	}

	public SavedGame popGameProgress() {
		if (this.gameProgress.isEmpty()) {
			return null;
		}
		return this.gameProgress.removeLast();
	}

	public SavedGame getLatestGameProgress() {
		if (this.gameProgress.isEmpty()) {
			return null;
		}
		return this.gameProgress.getLast();
	}

	public File getBaseDirectory() {
		return this.baseDirectory;
	}

	/**
	 * @return The {@link File} representing the directory that images are stored in
	 */
	public File getImagesDirectory() {
		return new File(this.baseDirectory, "images");
	}

	/**
	 * @return The {@link File} representing the directory that pages are stored in
	 */
	public File getPagesDirectory() {
		return new File(this.baseDirectory, "pages");
	}

	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public void clearMessage() {
		message = "-";
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