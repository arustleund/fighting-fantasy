package rustleund.nightdragon.framework.closures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.BattleState.BattleMessagePosition;

public class ClearBattleMessageClosure extends AbstractCommand {

	private Collection<BattleMessagePosition> positionsToClear;

	public ClearBattleMessageClosure(Element e) {
		if (e.hasAttribute("positionsToClear")) {
			this.positionsToClear = new ArrayList<BattleMessagePosition>();
			StringTokenizer tok = new StringTokenizer(e.getAttribute("positionsToClear"), ",");
			while (tok.hasMoreTokens()) {
				this.positionsToClear.add(BattleMessagePosition.valueOf(tok.nextToken()));
			}
		}
	}

	@Override
	public void execute(GameState gameState) {
		if (this.positionsToClear == null) {
			gameState.getBattleState().clearAllAdditionalMessages();
		} else {
			for (BattleMessagePosition position : this.positionsToClear) {
				gameState.getBattleState().clearAdditionalMessage(position);
			}
		}
	}

}
