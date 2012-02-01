/*
 * Created on Oct 25, 2005
 */
package rustleund.nightdragon.framework.closures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections.Closure;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestAnyFlagClosure extends AbstractCommand {

	private List flagIds = null;

	private Closure successful = null;

	private Closure unsuccessful = null;

	public TestAnyFlagClosure(Element element) {
		this.flagIds = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(element.getAttribute("ids"), ",");
		while (tokenizer.hasMoreTokens()) {
			this.flagIds.add(new Integer(tokenizer.nextToken()));
		}

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
		PlayerState playerState = gameState.getPlayerState();

		boolean shouldExecuteSuccessfulClosure = false;
		Iterator iter = this.flagIds.iterator();
		while (!shouldExecuteSuccessfulClosure && iter.hasNext()) {
			int thisFlagId = ((Integer) iter.next()).intValue();
			shouldExecuteSuccessfulClosure = playerState.getFlagValue(thisFlagId);
		}

		if (shouldExecuteSuccessfulClosure) {
			successful.execute(gameState);
		} else {
			unsuccessful.execute(gameState);
		}
	}

}