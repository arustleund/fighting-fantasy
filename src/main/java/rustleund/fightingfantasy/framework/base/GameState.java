/*
 * Created on Jun 17, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import org.jetbrains.annotations.Nullable;
import rustleund.fightingfantasy.framework.base.audio.AudioFile;
import rustleund.fightingfantasy.framework.closures.impl.DisplayTextClosure;
import rustleund.fightingfantasy.gamesave.SavedGame;

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
	private boolean battleRoundInProgress;
	private boolean pageLoaded;
	private final LinkedList<SavedGame> gameProgress = new LinkedList<>();

	private final LinkedList<AudioFile> audioFilesToPlay = new LinkedList<>();

	public LinkedList<AudioFile> getAudioFilesToPlay() {
		return audioFilesToPlay;
	}

	/**
	 * Queues narration to be played and remembers it on the current page so it can be replayed.
	 */
	public void queueNarration(AudioFile audioFile) {
		audioFilesToPlay.add(audioFile);
		pageState.getNarrationFiles().add(audioFile);
	}

	/**
	 * Shows <code>images/&lt;fileName&gt;</code> on the current page if that file exists; otherwise the current image is left alone.
	 *
	 * @return <code>true</code> if the image exists and is now showing
	 */
	public boolean showImageIfExists(String fileName) {
		Path image = getImagesDirectory().resolve(fileName);
		if (Files.exists(image)) {
			pageState.setCurrentImage(image);
			return true;
		}
		return false;
	}

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

	public Path getSoundsDirectory() {
		return this.baseDirectory.resolve("sounds");
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

	/**
	 * Whether a round of battle is being resolved. During a round the player can drop to 0 stamina and then be saved
	 * (by a playerHit effect or by Testing Their Luck), so the player's death is only handled once the round is over.
	 */
	public boolean isBattleRoundInProgress() {
		return battleRoundInProgress;
	}

	public void setBattleRoundInProgress(boolean battleRoundInProgress) {
		this.battleRoundInProgress = battleRoundInProgress;
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