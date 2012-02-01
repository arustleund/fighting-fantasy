/*
 * Created on Jun 17, 2004
 */
package rustleund.nightdragon.framework;

/**
 * @author rustlea
 */
public class GameState {

	private PlayerState playerState = null;

	private PageState pageState = null;

	private String message = null;

	private boolean isBattleInProgress;

	public GameState() {
		this.isBattleInProgress = false;
	}

	public void clearMessage() {
		message = "-";
	}

	/**
	 * @return
	 */
	public PlayerState getPlayerState() {
		return playerState;
	}

	/**
	 * @param state
	 */
	public void setPlayerState(PlayerState state) {
		playerState = state;
	}

	/**
	 * @return
	 */
	public PageState getPageState() {
		return pageState;
	}

	/**
	 * @param state
	 */
	public void setPageState(PageState state) {
		pageState = state;
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param string
	 */
	public void setMessage(String string) {
		message = string;
	}

	/**
	 * @return Returns the isBattleInProgress.
	 */
	public boolean isBattleInProgress() {
		return isBattleInProgress;
	}
	/**
	 * @param isBattleInProgress The isBattleInProgress to set.
	 */
	public void setBattleInProgress(boolean isBattleInProgress) {
		this.isBattleInProgress = isBattleInProgress;
	}
}