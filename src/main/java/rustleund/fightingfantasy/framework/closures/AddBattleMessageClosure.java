package rustleund.fightingfantasy.framework.closures;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.AbstractCommand;
import rustleund.fightingfantasy.framework.BattleState.BattleMessagePosition;
import rustleund.fightingfantasy.framework.GameState;

public class AddBattleMessageClosure extends AbstractCommand {

	private String message;
	private BattleMessagePosition messagePosition;

	public AddBattleMessageClosure(Element element) {
		this.message = element.getAttribute("message");
		if (element.hasAttribute("messagePosition")) {
			this.messagePosition = BattleMessagePosition.valueOf(element.getAttribute("messagePosition"));
		} else {
			this.messagePosition = BattleMessagePosition.END;
		}
	}

	@Override
	public boolean execute(GameState gameState) {
		gameState.getBattleState().addAdditionalMessage(this.messagePosition, this.message);
		return true;
	}

}
