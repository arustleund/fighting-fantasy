/*
 * Created on Oct 10, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class TestLuckClosure extends AbstractClosure {

	private Closure trueClosure;
	private Closure falseClosure;

	public TestLuckClosure(Element element, ClosureLoader closureLoader) {
		this.trueClosure = closureLoader.loadClosureFromChild(element, "successful");
		this.falseClosure = closureLoader.loadClosureFromChild(element, "unsuccessful");
	}

	@Override
	public boolean execute(GameState gameState) {
		PlayerState playerState = gameState.getPlayerState();

		boolean lucky = DiceRoller.rollDice(2) <= playerState.getLuck().getCurrentValue();
		playerState.getLuck().adjustCurrentValueNoException(-1);

		return lucky ? this.trueClosure.execute(gameState) : this.falseClosure.execute(gameState);
	}
}