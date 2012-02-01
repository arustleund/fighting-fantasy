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
import rustleund.nightdragon.framework.Command;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestAnyFlagClosure extends AbstractCommand {

	private List<Integer> flagIds = null;

	private Command successful = null;

	private Command unsuccessful = null;

	public TestAnyFlagClosure(Element element) {
		this.flagIds = new ArrayList<Integer>();
		StringTokenizer tokenizer = new StringTokenizer(element.getAttribute("ids"), ",");
		while (tokenizer.hasMoreTokens()) {
			this.flagIds.add(Integer.valueOf(tokenizer.nextToken()));
		}

		this.successful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("successful").item(0));
		this.unsuccessful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("unsuccessful").item(0));

		this.executeSuccessful = true;
	}

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