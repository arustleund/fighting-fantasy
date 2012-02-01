/*
 * Created on Oct 7, 2005
 */
package rustleund.fightingfantasy.framework.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.ChainedClosure;
import rustleund.fightingfantasy.framework.Closure;
import rustleund.fightingfantasy.framework.closures.AddBattleEffectsForNextBattleClosure;
import rustleund.fightingfantasy.framework.closures.AddBattleEffectsToCurrentBattleClosure;
import rustleund.fightingfantasy.framework.closures.AddBattleMessageClosure;
import rustleund.fightingfantasy.framework.closures.AddEnemiesClosure;
import rustleund.fightingfantasy.framework.closures.AddItemClosure;
import rustleund.fightingfantasy.framework.closures.AdjustEnemyScaleClosure;
import rustleund.fightingfantasy.framework.closures.AdjustPlayerAttackStrength;
import rustleund.fightingfantasy.framework.closures.AdjustScaleClosure;
import rustleund.fightingfantasy.framework.closures.ClearBattleMessageClosure;
import rustleund.fightingfantasy.framework.closures.ClearPoisonDamageClosure;
import rustleund.fightingfantasy.framework.closures.DisplayTextClosure;
import rustleund.fightingfantasy.framework.closures.InitPlayerStateClosure;
import rustleund.fightingfantasy.framework.closures.LinkClosure;
import rustleund.fightingfantasy.framework.closures.MustEatMealClosure;
import rustleund.fightingfantasy.framework.closures.RemoveItemClosure;
import rustleund.fightingfantasy.framework.closures.RestoreScaleClosure;
import rustleund.fightingfantasy.framework.closures.RollDiceClosure;
import rustleund.fightingfantasy.framework.closures.SetFlagClosure;
import rustleund.fightingfantasy.framework.closures.SetPoisonImmunity;
import rustleund.fightingfantasy.framework.closures.TestAnyFlagClosure;
import rustleund.fightingfantasy.framework.closures.TestFlagClosure;
import rustleund.fightingfantasy.framework.closures.TestItemClosure;
import rustleund.fightingfantasy.framework.closures.TestLuckClosure;
import rustleund.fightingfantasy.framework.closures.TestSkillClosure;
import rustleund.fightingfantasy.framework.closures.TestStatClosure;

/**
 * @author rustlea
 */
public class AbstractCommandLoader {

	private static final Map<String, Class<? extends Closure>> COMMAND_MAPPINGS = new HashMap<String, Class<? extends Closure>>();

	static {
		COMMAND_MAPPINGS.put("adjustscale", AdjustScaleClosure.class);
		COMMAND_MAPPINGS.put("adjustEnemyScale", AdjustEnemyScaleClosure.class);
		COMMAND_MAPPINGS.put("displayText", DisplayTextClosure.class);
		COMMAND_MAPPINGS.put("link", LinkClosure.class);
		COMMAND_MAPPINGS.put("addEnemies", AddEnemiesClosure.class);
		COMMAND_MAPPINGS.put("addItem", AddItemClosure.class);
		COMMAND_MAPPINGS.put("testItem", TestItemClosure.class);
		COMMAND_MAPPINGS.put("removeItem", RemoveItemClosure.class);
		COMMAND_MAPPINGS.put("testLuck", TestLuckClosure.class);
		COMMAND_MAPPINGS.put("testSkill", TestSkillClosure.class);
		COMMAND_MAPPINGS.put("testFlag", TestFlagClosure.class);
		COMMAND_MAPPINGS.put("testAnyFlag", TestAnyFlagClosure.class);
		COMMAND_MAPPINGS.put("setFlag", SetFlagClosure.class);
		COMMAND_MAPPINGS.put("testStat", TestStatClosure.class);
		COMMAND_MAPPINGS.put("restoreScale", RestoreScaleClosure.class);
		COMMAND_MAPPINGS.put("rollDice", RollDiceClosure.class);
		COMMAND_MAPPINGS.put("initPlayer", InitPlayerStateClosure.class);
		COMMAND_MAPPINGS.put("mustEatMeal", MustEatMealClosure.class);
		COMMAND_MAPPINGS.put("addBattleMessage", AddBattleMessageClosure.class);
		COMMAND_MAPPINGS.put("clearBattleMessage", ClearBattleMessageClosure.class);
		COMMAND_MAPPINGS.put("clearPoisonDamage", ClearPoisonDamageClosure.class);
		COMMAND_MAPPINGS.put("addBattleEffectsForNextBattle", AddBattleEffectsForNextBattleClosure.class);
		COMMAND_MAPPINGS.put("addBattleEffectsToCurrentBattle", AddBattleEffectsToCurrentBattleClosure.class);
		COMMAND_MAPPINGS.put("setPoisonImmunity", SetPoisonImmunity.class);
		COMMAND_MAPPINGS.put("adjustPlayerAttackStrength", AdjustPlayerAttackStrength.class);
	}

	public static Closure getAbstractCommand(Element commandTag) {
		Closure result = null;
		String commandTagType = commandTag.getNodeName();
		Class<? extends Closure> commandClass = COMMAND_MAPPINGS.get(commandTagType);
		try {
			Constructor<? extends Closure> constructor = commandClass.getConstructor(new Class[] { Element.class });
			result = constructor.newInstance(new Object[] { commandTag });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ChainedClosure loadChainedClosure(Element commandGroupTag) {
		List<Closure> commands = new ArrayList<Closure>();
		NodeList commandTags = commandGroupTag.getChildNodes();
		for (int i = 0; i < commandTags.getLength(); i++) {
			Node commandNode = commandTags.item(i);
			if (commandNode instanceof Element) {
				commands.add(getAbstractCommand((Element) commandNode));
			}
		}
		return new ChainedClosure(commands);
	}

	public static Closure loadClosureFromChildTag(Element parentTag, String childTagName) {
		NodeList possibles = parentTag.getElementsByTagName(childTagName);
		for (int i = 0; i < possibles.getLength(); i++) {
			Element possible = (Element) possibles.item(i);
			if (possible.getParentNode().isSameNode(parentTag)) {
				return loadChainedClosure(possible);
			}
		}
		return null;
	}

}