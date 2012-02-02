/*
 * Created on Oct 10, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

/**
 * @author rustlea
 */
public class TestFlagClosure extends AbstractClosure {

	private int flagId = -1;
	private Closure successful = null;
	private Closure unsuccessful = null;

	public TestFlagClosure(Element element) {
		this.flagId = Integer.parseInt(element.getAttribute("id"));

		this.successful = DefaultClosureLoader.loadClosureFromChildTag(element, "successful");
		this.unsuccessful = DefaultClosureLoader.loadClosureFromChildTag(element, "unsuccessful");
	}

	@Override
	public boolean execute(GameState gameState) {
		if (gameState.getPlayerState().getFlagValue(flagId)) {
			return successful.execute(gameState);
		}
		return unsuccessful.execute(gameState);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}