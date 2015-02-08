package rustleund.fightingfantasy.gamesave;

import rustleund.fightingfantasy.framework.base.PlayerState;

public class SavedGame {

	private String pageId;
	private PlayerState playerState;

	public SavedGame(String pageId, PlayerState playerState) {
		this.pageId = pageId;
		this.playerState = playerState;
	}

	public String getPageId() {
		return this.pageId;
	}

	public PlayerState getPlayerState() {
		return this.playerState;
	}
}
