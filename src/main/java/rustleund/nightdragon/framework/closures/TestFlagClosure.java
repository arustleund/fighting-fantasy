/*
 * Created on Oct 10, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.apache.commons.collections.Closure;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestFlagClosure extends AbstractCommand {

	private int flagId = -1;

	private Closure successful = null;

	private Closure unsuccessful = null;

	public TestFlagClosure(Element element) {
		this.flagId = Integer.parseInt(element.getAttribute("id"));

		this.successful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("successful").item(0));
		this.unsuccessful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("unsuccessful").item(0));

		this.executeSuccessful = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(Object arg0) {
		GameState gameState = (GameState) arg0;

		if (gameState.getPlayerState().getFlagValue(flagId)) {
			successful.execute(gameState);
		} else {
			unsuccessful.execute(gameState);
		}
	}

}