/*
 * Created on Jun 17, 2004
 */
package rustleund.fightingfantasy.framework;

/**
 * @author rustlea
 */
public class Item {

	private Integer id = null;

	private String name = null;

	private Integer price = null;

	private Integer limit = null;

	private Integer count = null;

	private Closure useItem = null;

	private boolean canUseInBattle;

	public Item() {
		count = new Integer(1);
	}

	public boolean hasLimit() {
		return limit != null;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	public String toString() {
		return (count + " - " + name);
	}

	/**
	 * @return
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * @param integer
	 */
	public void setPrice(Integer integer) {
		price = integer;
	}

	/**
	 * @return
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param integer
	 */
	public void setId(Integer integer) {
		id = integer;
	}

	/**
	 * @return
	 */
	public Integer getLimit() {
		return limit;
	}

	/**
	 * @param integer
	 */
	public void setLimit(Integer integer) {
		limit = integer;
	}

	public void decrementCount() {
		count = new Integer(count.intValue() - 1);
	}

	public void incrementCount() {
		count = new Integer(count.intValue() + 1);
	}

	/**
	 * @return
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param integer
	 */
	public void setCount(Integer integer) {
		count = integer;
	}

	public void useItem(GameState gameState) {
		if (useItem != null) {
			boolean battleInProgress = gameState.isBattleInProgress();
			if (!battleInProgress || (battleInProgress && canUseInBattle)) {
				useItem.execute(gameState);
			} else {
				gameState.setMessage("Cannot use " + this.name + " during a battle");
			}
		}
	}

	/**
	 * @return Returns the useItem.
	 */
	public Closure getUseItem() {
		return useItem;
	}

	/**
	 * @param useItem
	 *            The useItem to set.
	 */
	public void setUseItem(Closure useItem) {
		this.useItem = useItem;
	}

	/**
	 * @return Returns the canUseInBattle.
	 */
	public boolean isCanUseInBattle() {
		return canUseInBattle;
	}

	/**
	 * @param canUseInBattle
	 *            The canUseInBattle to set.
	 */
	public void setCanUseInBattle(boolean canUseInBattle) {
		this.canUseInBattle = canUseInBattle;
	}
}