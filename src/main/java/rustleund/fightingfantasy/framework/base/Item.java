/*
 * Created on Jun 17, 2004
 */
package rustleund.fightingfantasy.framework.base;

import rustleund.fightingfantasy.framework.closures.Closure;

/**
 * @author rustlea
 */
public class Item {

	private int id;
	private String name;
	private int defaultPrice;
	private Integer limit;
	private int count;
	private Closure useItem;
	private boolean canUseInBattle;
	private BattleEffects battleEffects;

	private Item() {
		// for deep copy
	}

	public Item(int id, String name, int defaultPrice) {
		this.id = id;
		this.name = name;
		this.defaultPrice = defaultPrice;
		this.count = 1;
	}

	public boolean hasLimit() {
		return limit != null;
	}

	public String getName() {
		return name;
	}

	public int getDefaultPrice() {
		return this.defaultPrice;
	}

	@Override
	public String toString() {
		return (count + " - " + name);
	}

	public int getId() {
		return id;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer integer) {
		limit = integer;
	}

	/**
	 * Decrease the quantity of this item by 1.
	 */
	public void decrementCount() {
		this.count--;
	}

	/**
	 * Increase the quantity of this item by 1.
	 */
	public void incrementCount() {
		this.count++;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Executes the commands that were configured on this {@link Item}. If no command was configured, this method does nothing.
	 * 
	 * @param gameState The {@link GameState} to execute the command with.
	 */
	public void useItem(GameState gameState) {
		if (useItem != null) {
			boolean battleInProgress = gameState.isBattleInProgress();
			if (!battleInProgress || canUseInBattle) {
				useItem.execute(gameState);
			} else {
				gameState.setMessage("Cannot use " + this.name + " during a battle");
			}
		}
	}

	public Closure getUseItem() {
		return useItem;
	}

	public void setUseItem(Closure useItem) {
		this.useItem = useItem;
	}

	public boolean isCanUseInBattle() {
		return canUseInBattle;
	}

	public void setCanUseInBattle(boolean canUseInBattle) {
		this.canUseInBattle = canUseInBattle;
	}

	public BattleEffects getBattleEffects() {
		return this.battleEffects;
	}

	public void setBattleEffects(BattleEffects battleEffects) {
		this.battleEffects = battleEffects;
	}

	public Item deepCopy() {
		Item result = new Item();
		result.canUseInBattle = canUseInBattle;
		result.count = count;
		result.defaultPrice = defaultPrice;
		result.id = id;
		result.limit = limit;
		result.name = name;
		result.useItem = useItem;
		result.battleEffects = battleEffects;
		return result;
	}
}