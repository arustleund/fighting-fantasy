/*
 * Created on Oct 25, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.closures.Closure;

/**
 * @author rustlea
 */
public class TestAnyFlagClosure extends AbstractClosure {

	private List<Integer> flagIds;
	private Closure successful;
	private Closure unsuccessful;

	public TestAnyFlagClosure(Element element) {
		this.flagIds = new ArrayList<Integer>();
		StringTokenizer tokenizer = new StringTokenizer(element.getAttribute("ids"), ",");
		while (tokenizer.hasMoreTokens()) {
			this.flagIds.add(Integer.valueOf(tokenizer.nextToken()));
		}

		this.successful = DefaultClosureLoader.loadClosureFromChildTag(element, "successful");
		this.unsuccessful = DefaultClosureLoader.loadClosureFromChildTag(element, "unsuccessful");
	}

	@Override
	public boolean execute(GameState gameState) {
		PlayerState playerState = gameState.getPlayerState();

		boolean shouldExecuteSuccessfulClosure = false;
		Iterator<Integer> iter = this.flagIds.iterator();
		while (!shouldExecuteSuccessfulClosure && iter.hasNext()) {
			shouldExecuteSuccessfulClosure = playerState.getFlagValue(iter.next());
		}

		if (shouldExecuteSuccessfulClosure) {
			return successful.execute(gameState);
		}
		return unsuccessful.execute(gameState);
	}

}