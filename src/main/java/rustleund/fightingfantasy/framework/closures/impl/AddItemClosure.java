/*
 * Created on Jul 12, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.Item;
import rustleund.fightingfantasy.framework.base.PageState;
import rustleund.fightingfantasy.framework.base.PlayerState;
import rustleund.fightingfantasy.framework.util.ItemUtil;

/**
 * @author rustlea
 */
public class AddItemClosure extends AbstractClosure {

	private int itemId;
	private int price = -1;
	private int quantity = 1;
	private int pageLimit = -1;
	private int pageBuys = 0;

	public AddItemClosure(Element addItemElement) {
		this.itemId = Integer.valueOf(addItemElement.getAttribute("id"));
		if (addItemElement.hasAttribute("price")) {
			this.price = Integer.valueOf(addItemElement.getAttribute("price"));
		}
		if (addItemElement.hasAttribute("quantity")) {
			this.quantity = Integer.valueOf(addItemElement.getAttribute("quantity"));
		}
		if (addItemElement.hasAttribute("pageLimit")) {
			this.pageLimit = Integer.valueOf(addItemElement.getAttribute("pageLimit"));
		}
	}

	public AddItemClosure(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public boolean execute(GameState gameState) {
		PlayerState playerState = gameState.getPlayerState();
		PageState pageState = gameState.getPageState();
		Item item = ItemUtil.getInstance().getItem(itemId);
		if (item.hasLimit() && playerState.itemCount(itemId) + this.quantity > item.getLimit()) {
			gameState.setMessage("Buying the " + item.getName() + " would put you above the maximum amount allowed (" + item.getLimit() + ")");
			return false;
		}
		if (this.pageLimit >= 0 && this.pageBuys + this.quantity > this.pageLimit) {
			gameState.setMessage("This location cannot sell any more of the " + item.getName());
			return false;
		}
		if (pageState.hasKeepMinimumForScale("gold") && ((playerState.getGold().getCurrentValue() - getPrice(item.getPrice())) < pageState.getKeepMinimumForScale("gold"))) {
			gameState.setMessage("Buying the " + item.getName() + " would put you below the minimum of " + pageState.getKeepMinimumForScale("gold") + " Gold Pieces");
			return false;
		}
		if (playerState.getGold().getCurrentValue() < getPrice(item.getPrice())) {
			gameState.setMessage("You do not have sufficient Gold to buy the " + item.getName());
			return false;
		}
		playerState.getGold().adjustCurrentValue(getPrice(item.getPrice()) * -1);
		for (int i = 0; i < this.quantity; i++) {
			playerState.addItem(item);
			this.pageBuys++;
		}
		return true;
	}

	private int getPrice(int standardPrice) {
		return (this.price == -1 ? standardPrice : this.price) * this.quantity;
	}

}