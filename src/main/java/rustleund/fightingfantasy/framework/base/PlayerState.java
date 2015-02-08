/*
 * Created on Mar 7, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import rustleund.fightingfantasy.framework.util.DiceRoller;

import com.google.common.collect.Maps;

/**
 * @author rustlea
 */
public class PlayerState extends AbstractEntityState {

	private Scale luck;
	private Scale provisions;
	private Scale honor;
	private Scale nemesis;
	private Scale gold;
	private Scale time;
	private Map<Integer, Item> items;
	private Map<Integer, Boolean> flags;
	private List<BattleEffects> nextBattleBattleEffects;
	private int poisonDamage;
	private boolean poisonImmunity = false;

	private PlayerState() {
		// for deep copy
	}

	public PlayerState(String name, List<Item> items) {
		int initialSkill = DiceRoller.rollOneDie() + 6;
		int initialStamina = DiceRoller.rollDice(2) + 12;
		int initialLuck = DiceRoller.rollOneDie() + 6;

		int initialGold = DiceRoller.rollDice(2) + 3;

		init(name, initialSkill, initialStamina, initialLuck, 12, 0, 0, initialGold, 0, items);
	}

	public PlayerState(String name, int skill, int stamina, int luck, int provisions, int honor, int nemesis, int gold, int time, List<Item> items) {
		init(name, skill, stamina, luck, provisions, honor, nemesis, gold, time, items);
	}

	private void init(String name, int skill, int stamina, int luck, int provisions, int honor, int nemesis, int gold, int time, List<Item> items) {
		this.name = name;
		this.skill = new Scale(0, skill, skill, true);
		this.stamina = new Scale(0, stamina, stamina, true);
		this.luck = new Scale(0, luck, luck, true);
		this.provisions = new Scale(0, provisions, provisions, true);
		this.honor = new Scale(null, honor, null, true);
		this.nemesis = new Scale(null, nemesis, null, true);
		this.gold = new Scale(0, gold, null, false);
		this.time = new Scale(0, time, null, true);

		this.items = new HashMap<>();
		this.flags = new HashMap<>();

		for (Item item : items) {
			addItem(item);
		}
	}

	public void takePoisonDamage(int amount) {
		if (!this.poisonImmunity) {
			this.stamina.adjustCurrentValueNoException(amount * -1);
			this.poisonDamage += amount;
		}
	}

	public void clearPoisonDamage() {
		this.stamina.adjustCurrentValueNoException(this.poisonDamage);
		this.poisonDamage = 0;
	}

	public void addItem(Item toAdd) {
		if (this.items.containsKey(toAdd.getId())) {
			this.items.get(toAdd.getId()).incrementCount();
		} else {
			this.items.put(toAdd.getId(), toAdd);
		}
	}

	public void removeOneOfItem(int itemId) {
		int itemCount = itemCount(itemId);
		if (itemCount == 1) {
			this.items.remove(itemId);
		} else if (itemCount != 0) {
			this.items.get(itemId).decrementCount();
		}
	}

	public void removeAllOfItem(int itemId) {
		this.items.remove(itemId);
	}

	public int itemCount(int itemId) {
		if (this.items.containsKey(itemId)) {
			return this.items.get(itemId).getCount();
		}
		return 0;
	}

	public void setFlag(int flagId, boolean value) {
		this.flags.put(flagId, value);
	}

	public boolean getFlagValue(int flagId) {
		if (this.flags.containsKey(flagId)) {
			return this.flags.get(flagId);
		}
		return false;
	}

	public void addNextBattleBattleEffect(BattleEffects battleEffects) {
		if (this.nextBattleBattleEffects == null) {
			this.nextBattleBattleEffects = new ArrayList<>();
		}
		this.nextBattleBattleEffects.add(battleEffects);
	}

	public int getPoisonDamage() {
		return this.poisonDamage;
	}

	public void setPoisonDamage(int poisonDamage) {
		this.poisonDamage = poisonDamage;
	}

	public List<BattleEffects> getNextBattleBattleEffects() {
		return this.nextBattleBattleEffects;
	}

	public void setNextBattleBattleEffects(List<BattleEffects> nextBattleBattleEffects) {
		this.nextBattleBattleEffects = nextBattleBattleEffects;
	}

	public Scale getGold() {
		return gold;
	}

	public Scale getHonor() {
		return honor;
	}

	public Map<Integer, Item> getItems() {
		return items;
	}

	public Scale getLuck() {
		return luck;
	}

	public Scale getNemesis() {
		return nemesis;
	}

	public Scale getProvisions() {
		return provisions;
	}

	public Scale getTime() {
		return time;
	}

	public void setGold(Scale scale) {
		gold = scale;
	}

	public void setHonor(Scale scale) {
		honor = scale;
	}

	public void setItems(Map<Integer, Item> map) {
		items = map;
	}

	public void setLuck(Scale scale) {
		luck = scale;
	}

	public void setNemesis(Scale scale) {
		nemesis = scale;
	}

	public void setProvisions(Scale scale) {
		provisions = scale;
	}

	public void setTime(Scale scale) {
		time = scale;
	}

	public boolean isPoisonImmunity() {
		return this.poisonImmunity;
	}

	public void setPoisonImmunity(boolean poisonImmunity) {
		this.poisonImmunity = poisonImmunity;
	}

	public PlayerState deepCopy() {
		PlayerState result = new PlayerState();
		result.attackStrengthModifier = attackStrengthModifier;
		result.flags = new HashMap<>(flags);
		result.gold = gold.deepCopy();
		result.honor = honor.deepCopy();
		result.items = deepCopyItems();
		result.luck = luck.deepCopy();
		result.name = name;
		result.nemesis = nemesis.deepCopy();
		result.nextBattleBattleEffects = nextBattleBattleEffects == null ? null : new ArrayList<>(nextBattleBattleEffects);
		result.poisonDamage = poisonDamage;
		result.poisonImmunity = poisonImmunity;
		result.provisions = provisions.deepCopy();
		result.skill = skill.deepCopy();
		result.stamina = stamina.deepCopy();
		result.time = time.deepCopy();
		return result;
	}

	private Map<Integer, Item> deepCopyItems() {
		Map<Integer, Item> result = Maps.newHashMapWithExpectedSize(items.size());
		for (Entry<Integer, Item> entry : items.entrySet()) {
			result.put(entry.getKey(), entry.getValue().deepCopy());
		}
		return result;
	}
}