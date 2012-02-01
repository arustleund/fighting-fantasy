/*
 * Created on Mar 10, 2004
 */
package rustleund.nightdragon.framework.closures;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import rustleund.nightdragon.framework.AbstractEntityState;
import rustleund.nightdragon.framework.Command;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.Scale;
import rustleund.nightdragon.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class DynaBattleClosure implements Command {

	private int numberOfTimesToExecute = 0;

	private boolean forPlayer = false;

	private boolean random = false;

	private int numberOfDiceToRoll = 0;

	private int diceTotalMinRequired = 0;

	private int diceTotalMaxRequired = 0;

	private String stateProperty = null;

	private int amountToAdjust = 0;

	public DynaBattleClosure(Element element) {
		NodeList properties = element.getChildNodes();
		for (int i = 0; i < properties.getLength(); i++) {
			Node propertyNode = properties.item(i);
			if (propertyNode instanceof Element) {
				Node propertyValueNode = propertyNode.getFirstChild();
				if (propertyValueNode instanceof Text) {
					String propertyValueNodeValue = propertyValueNode.getNodeValue();
					try {
						BeanUtils.copyProperty(this, propertyNode.getNodeName(), propertyValueNodeValue);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void execute(GameState gameState) {
		AbstractEntityState state = null;
		if (forPlayer) {
			state = gameState.getPlayerState();
		}

		if (numberOfTimesToExecute != 0) {

			boolean executeAdjustment = !random;

			if (random) {

				int diceTotal = DiceRoller.rollDice(numberOfDiceToRoll);
				if (diceTotal >= diceTotalMinRequired && diceTotal <= diceTotalMaxRequired) {
					executeAdjustment = true;
				}

			}

			if (executeAdjustment) {
				try {
					((Scale) PropertyUtils.getSimpleProperty(state, stateProperty)).adjustCurrentValueNoException(amountToAdjust);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			numberOfTimesToExecute--;

		}

	}

	/**
	 * @return
	 */
	public int getNumberOfTimesToExecute() {
		return numberOfTimesToExecute;
	}

	/**
	 * @param i
	 */
	public void setNumberOfTimesToExecute(int i) {
		numberOfTimesToExecute = i;
	}

	/**
	 * @return
	 */
	public boolean isForPlayer() {
		return forPlayer;
	}

	/**
	 * @param b
	 */
	public void setForPlayer(boolean b) {
		forPlayer = b;
	}

	/**
	 * @return
	 */
	public int getAmountToAdjust() {
		return amountToAdjust;
	}

	/**
	 * @return
	 */
	public int getDiceTotalMaxRequired() {
		return diceTotalMaxRequired;
	}

	/**
	 * @return
	 */
	public int getDiceTotalMinRequired() {
		return diceTotalMinRequired;
	}

	/**
	 * @return
	 */
	public int getNumberOfDiceToRoll() {
		return numberOfDiceToRoll;
	}

	/**
	 * @return
	 */
	public boolean isRandom() {
		return random;
	}

	/**
	 * @return
	 */
	public String getStateProperty() {
		return stateProperty;
	}

	/**
	 * @param i
	 */
	public void setAmountToAdjust(int i) {
		amountToAdjust = i;
	}

	/**
	 * @param i
	 */
	public void setDiceTotalMaxRequired(int i) {
		diceTotalMaxRequired = i;
	}

	/**
	 * @param i
	 */
	public void setDiceTotalMinRequired(int i) {
		diceTotalMinRequired = i;
	}

	/**
	 * @param i
	 */
	public void setNumberOfDiceToRoll(int i) {
		numberOfDiceToRoll = i;
	}

	/**
	 * @param b
	 */
	public void setRandom(boolean b) {
		random = b;
	}

	/**
	 * @param string
	 */
	public void setStateProperty(String string) {
		stateProperty = string;
	}

}