/*
 * Created on Oct 25, 2005
 */
package rustleund.nightdragon.framework.closures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.Closure;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestAnyFlagClosure extends AbstractCommand {

	private List<Integer> flagIds;
	private Closure successful;
	private Closure unsuccessful;

	public TestAnyFlagClosure(Element element) {
		this.flagIds = new ArrayList<Integer>();
		StringTokenizer tokenizer = new StringTokenizer(element.getAttribute("ids"), ",");
		while (tokenizer.hasMoreTokens()) {
			this.flagIds.add(new Integer(tokenizer.nextToken()));
		}

		this.successful = AbstractCommandLoader.loadClosureFromChildTag(element, "successful");
		this.unsuccessful = AbstractCommandLoader.loadClosureFromChildTag(element, "unsuccessful");

		this.executeSuccessful = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(GameState gameState) {
		PlayerState playerState = gameState.getPlayerState();

		boolean shouldExecuteSuccessfulClosure = false;
		Iterator<Integer> iter = this.flagIds.iterator();
		while (!shouldExecuteSuccessfulClosure && iter.hasNext()) {
			shouldExecuteSuccessfulClosure = playerState.getFlagValue(iter.next());
		}

		if (shouldExecuteSuccessfulClosure) {
			successful.execute(gameState);
		} else {
			unsuccessful.execute(gameState);
		}
	}

}