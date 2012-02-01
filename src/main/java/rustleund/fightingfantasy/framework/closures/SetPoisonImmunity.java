package rustleund.fightingfantasy.framework.closures;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.AbstractCommand;
import rustleund.fightingfantasy.framework.GameState;

public class SetPoisonImmunity extends AbstractCommand {

	private boolean immune;

	public SetPoisonImmunity(Element element) {
		this.immune = attributeValue(element, "immune", true);
	}

	public void execute(GameState gameState) {
		gameState.getPlayerState().setPoisonImmunity(this.immune);
	}

}
