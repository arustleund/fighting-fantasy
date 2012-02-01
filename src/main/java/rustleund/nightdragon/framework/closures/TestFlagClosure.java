/*
 * Created on Oct 10, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.Command;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestFlagClosure extends AbstractCommand {

	private int flagId = -1;

	private Command successful = null;

	private Command unsuccessful = null;

	public TestFlagClosure(Element element) {
		this.flagId = Integer.parseInt(element.getAttribute("id"));

		this.successful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("successful").item(0));
		this.unsuccessful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("unsuccessful").item(0));
	}

	public boolean execute(GameState gameState) {
		if (gameState.getPlayerState().getFlagValue(flagId)) {
			return successful.execute(gameState);
		}
		return unsuccessful.execute(gameState);
	}

}