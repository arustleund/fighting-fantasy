/*
 * Created on Jul 12, 2004
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.Item;
import rustleund.nightdragon.framework.PageState;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.util.ItemUtil;

/**
 * @author rustlea
 */
public class AddItemClosure extends AbstractCommand {

	private int itemId;

	public AddItemClosure(Element addItemElement) {
		this.itemId = Integer.parseInt(addItemElement.getAttribute("id"));
	}

	public AddItemClosure(int itemId) {
		this.itemId = itemId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(Object object) {

		GameState gameState = (GameState) object;

		PlayerState playerState = gameState.getPlayerState();
		PageState pageState = gameState.getPageState();
		Item item = ItemUtil.getInstance().getItem(itemId);
		if (item.hasLimit() && playerState.itemCount(itemId) >= item.getLimit().intValue()) {
			gameState.setMessage("You already have the maximum amount of " + item.getName());
			executeSuccessful = false;
		} else if (pageState.hasKeepMinimumForScale("gold") && ((playerState.getGold().getCurrentValue() - item.getPrice().intValue()) < pageState.getKeepMinimumForScale("gold"))) {
			gameState.setMessage("Buying the " + item.getName() + " would put you below the minimum of " + pageState.getKeepMinimumForScale("gold") + " Gold Pieces");
			executeSuccessful = false;
		} else if (playerState.getGold().getCurrentValue() < item.getPrice().intValue()) {
			gameState.setMessage("You do not have sufficient Gold to buy the " + item.getName());
			executeSuccessful = false;
		} else {
			playerState.getGold().adjustCurrentValue(item.getPrice().intValue() * -1);
			playerState.addItem(item);
			executeSuccessful = true;
		}

	}

}