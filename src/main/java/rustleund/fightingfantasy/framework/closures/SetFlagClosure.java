/*
 * Created on Oct 14, 2005
 */
package rustleund.fightingfantasy.framework.closures;

import org.apache.commons.lang.BooleanUtils;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.AbstractCommand;
import rustleund.fightingfantasy.framework.GameState;

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

		this.executeSuccessful = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(GameState gameState) {
		gameState.getPlayerState().setFlag(flagId, flagValue);
	}
}