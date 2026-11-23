package rustleund.fightingfantasy.gamesave;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import rustleund.fightingfantasy.framework.base.PlayerState;

/**
 * A snapshot taken once a page has finished loading, i.e. after its <code>&lt;immediate&gt;</code> commands ran, so that
 * restoring it doesn't run them again (which would, for example, roll dice a second time).
 *
 * @param pageId      the page the player is on
 * @param playerState the player as of the end of the page's immediate commands
 * @param pageText    the page text as of the end of the page's immediate commands. <code>null</code> in saves written
 *                    before snapshots included it; those can only be restored by re-running the page's immediate commands.
 * @param image       the name of the image file showing, relative to the images directory, or <code>null</code> if none
 * @param narration   the names of the narration files queued so far, relative to the sounds directory
 */
public record SavedGame(
        String pageId,
        PlayerState playerState,
        @Nullable String pageText,
        @Nullable String image,
        @Nullable List<String> narration
) {

    /**
     * @return <code>true</code> if this snapshot holds the page as it was after its immediate commands ran
     */
    public boolean hasPageSnapshot() {
        return pageText != null;
    }
}
