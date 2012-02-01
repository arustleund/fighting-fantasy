package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;

public class SetPoisonImmunity extends AbstractCommand {

	private boolean immune;

	public SetPoisonImmunity(Element element) {
		this.immune = attributeValue(element, "immune", true);
	}

	public void execute(GameState gameState) {
		gameState.getPlayerState().setPoisonImmunity(this.immune);
	}

}
