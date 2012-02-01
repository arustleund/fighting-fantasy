/*
 * Created on Oct 14, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.apache.commons.lang.BooleanUtils;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;

/**
 * @author rustlea
 */
public class SetFlagClosure extends AbstractCommand {

	private int flagId = -1;

	private boolean flagValue = true;

	public SetFlagClosure(Element element) {
		this.flagId = Integer.parseInt(element.getAttribute("id"));

		if (element.hasAttribute("value")) {
			this.flagValue = BooleanUtils.toBoolean(element.getAttribute("value"));
		}
	}

	public boolean execute(GameState gameState) {
		gameState.getPlayerState().setFlag(flagId, flagValue);
		return true;
	}
}