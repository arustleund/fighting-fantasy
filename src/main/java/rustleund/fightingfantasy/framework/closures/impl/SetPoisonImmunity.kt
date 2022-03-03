package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;

public class SetPoisonImmunity extends AbstractClosure {

	private boolean immune;

	public SetPoisonImmunity(Element element) {
		this.immune = attributeValue(element, "immune", true);
	}

	@Override
	public boolean execute(GameState gameState) {
		gameState.getPlayerState().setPoisonImmunity(this.immune);
		return true;
	}

}
