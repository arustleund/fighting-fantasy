/*
 * Created on Oct 15, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.Scale;

/**
 * @author rustlea
 */
public class RestoreScaleClosure extends AbstractCommand {

	private String scaleName = null;

	public RestoreScaleClosure(Element element) {
		this.scaleName = element.getAttribute("type");
	}

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