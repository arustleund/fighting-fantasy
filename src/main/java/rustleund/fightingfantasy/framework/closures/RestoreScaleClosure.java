/*
 * Created on Oct 15, 2005
 */
package rustleund.fightingfantasy.framework.closures;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.AbstractCommand;
import rustleund.fightingfantasy.framework.GameState;
import rustleund.fightingfantasy.framework.Scale;

/**
 * @author rustlea
 */
public class RestoreScaleClosure extends AbstractCommand {

	private String scaleName = null;

	public RestoreScaleClosure(Element element) {
		this.scaleName = element.getAttribute("type");
	}

	@Override
	public boolean execute(GameState gameState) {

		Scale scale = null;

		try {
			scale = (Scale) PropertyUtils.getProperty(gameState.getPlayerState(), scaleName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		scale.restorePreviousValue();

		return true;
	}

}