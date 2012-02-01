/*
 * Created on Mar 7, 2004
 */
package rustleund.nightdragon.framework;

import java.util.HashMap;
import java.util.Map;

import rustleund.nightdragon.framework.util.DiceRoller;
import rustleund.nightdragon.framework.util.ItemUtil;

/**
 * @author rustlea
 */
public class PlayerState extends AbstractEntityState {

	private Scale luck = null;

	private Scale provisions = null;

	private Scale honor = null;

	private Scale nemesis = null;

	private Scale gold = null;

	private Scale time = null;

	private Map items = null;

	private Map flags = null;

	public PlayerState(String name) {

		int initialSkill = DiceRoller.rollOneDie() + 6;
		int initialStamina = DiceRoller.rollDice(2) + 12;
		int initialLuck = DiceRoller.rollOneDie() + 6;

		int initialGold = DiceRoller.rollDice(2) + 13;

		init(name, initialSkill, initialStamina, initialLuck, 12, 0, 0, initialGold, 0);

	}

	public PlayerState(String name, int skill, int stamina, int luck, int provisions, int honor, int nemesis, int gold, int time) {
		init(name, skill, stamina, luck, provisions, honor, nemesis, gold, time);
	}

	private void init(String name, int skill, int stamina, int luck, int provisions, int honor, int nemesis, int gold, int time) {
		this.name = name;
		this.skill = new Scale(new Integer(0), new Integer(skill), new Integer(skill), true);
		this.stamina = new Scale(new Integer(0), new Integer(stamina), new Integer(stamina), true);
		this.luck = new Scale(new Integer(0), new Integer(luck), new Integer(luck), true);
		this.provisions = new Scale(new Integer(0), new Integer(provisions), new Integer(provisions), true);
		this.honor = new Scale(null, new Integer(honor), null, true);
		this.nemesis = new Scale(null, new Integer(nemesis), null, true);
		this.gold = new Scale(new Integer(0), new Integer(gold), null, false);
		this.time = new Scale(new Integer(0), new Integer(time), null, true);

		items = new HashMap();
		flags = new HashMap();

		ItemUtil itemUtil = ItemUtil.getInstance();

		addItem(itemUtil.getItem(0));
		addItem(itemUtil.getItem(1));
		addItem(itemUtil.getItem(2));
		addItem(itemUtil.getItem(3));
		addItem(itemUtil.getItem(4));

	}

	public void addItem(Item toAdd) {
		if (items.containsKey(toAdd.getId())) {
			((Item) items.get(toAdd.getId())).incrementCount();
		} else {
			items.put(toAdd.getId(), toAdd);
		}
	}

	public void removeOneOfItem(int itemId) {
		int itemCount = itemCount(itemId);
		if (itemCount == 1) {
			items.remove(new Integer(itemId));
		} else if (itemCount != 0) {
			((Item) items.get(new Integer(itemId))).decrementCount();
		}
	}

	public void removeAllOfItem(int itemId) {
		items.remove(new Integer(itemId));
	}

	public int itemCount(int itemId) {
		if (items.containsKey(new Integer(itemId))) {
			return ((Item) items.get(new Integer(itemId))).getCount().intValue();
		}
		return 0;
	}

	public void setFlag(int flagId, boolean value) {
		flags.put(new Integer(flagId), Boolean.valueOf(value));
	}

	public boolean getFlagValue(int flagId) {
		Integer flagIdInteger = new Integer(flagId);
		if (flags.containsKey(flagIdInteger)) {
			return ((Boolean) flags.get(flagIdInteger)).booleanValue();
		}
		return false;
	}

	/**
	 * @return
	 */
	public Scale getGold() {
		return gold;
	}

	/**
	 * @return
	 */
	public Scale getHonor() {
		return honor;
	}

	/**
	 * @return
	 */
	public Map getItems() {
		return items;
	}

	/**
	 * @return
	 */
	public Scale getLuck() {
		return luck;
	}

	/**
	 * @return
	 */
	public Scale getNemesis() {
		return nemesis;
	}

	/**
	 * @return
	 */
	public Scale getProvisions() {
		return provisions;
	}

	/**
	 * @return
	 */
	public Scale getTime() {
		return time;
	}

	/**
	 * @param scale
	 */
	public void setGold(Scale scale) {
		gold = scale;
	}

	/**
	 * @param scale
	 */
	public void setHonor(Scale scale) {
		honor = scale;
	}

	/**
	 * @param list
	 */
	public void setItems(Map map) {
		items = map;
	}

	/**
	 * @param scale
	 */
	public void setLuck(Scale scale) {
		luck = scale;
	}

	/**
	 * @param scale
	 */
	public void setNemesis(Scale scale) {
		nemesis = scale;
	}

	/**
	 * @param scale
	 */
	public void setProvisions(Scale scale) {
		provisions = scale;
	}

	/**
	 * @param scale
	 */
	public void setTime(Scale scale) {
		time = scale;
	}

}