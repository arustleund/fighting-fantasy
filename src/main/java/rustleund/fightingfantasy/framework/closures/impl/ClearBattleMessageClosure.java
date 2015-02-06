package rustleund.fightingfantasy.framework.closures.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.BattleState.BattleMessagePosition;

public class ClearBattleMessageClosure extends AbstractClosure {

	private Collection<BattleMessagePosition> positionsToClear;

	public ClearBattleMessageClosure(Element e) {
		if (e.hasAttribute("positionsToClear")) {
			this.positionsToClear = new ArrayList<>();
			StringTokenizer tok = new StringTokenizer(e.getAttribute("positionsToClear"), ",");
			while (tok.hasMoreTokens()) {
				this.positionsToClear.add(BattleMessagePosition.valueOf(tok.nextToken()));
			}
		}
	}

	@Override
	public boolean execute(GameState gameState) {
		if (this.positionsToClear == null) {
			gameState.getBattleState().clearAllAdditionalMessages();
		} else {
			for (BattleMessagePosition position : this.positionsToClear) {
				gameState.getBattleState().clearAdditionalMessage(position);
			}
		}
		return true;
	}

}
