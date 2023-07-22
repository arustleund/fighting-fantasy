package rustleund.fightingfantasy.gamesave;

import rustleund.fightingfantasy.framework.base.PlayerState;

public record SavedGame(String pageId, PlayerState playerState) {
}
