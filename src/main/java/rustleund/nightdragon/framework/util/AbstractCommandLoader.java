/*
 * Created on Oct 7, 2005
 */
package rustleund.nightdragon.framework.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rustleund.nightdragon.framework.Command;
import rustleund.nightdragon.framework.closures.AddEnemiesClosure;
import rustleund.nightdragon.framework.closures.AddItemClosure;
import rustleund.nightdragon.framework.closures.AdjustScaleClosure;
import rustleund.nightdragon.framework.closures.ChainedCommand;
import rustleund.nightdragon.framework.closures.DisplayTextClosure;
import rustleund.nightdragon.framework.closures.InitPlayerStateClosure;
import rustleund.nightdragon.framework.closures.LinkClosure;
import rustleund.nightdragon.framework.closures.MustEatMealClosure;
import rustleund.nightdragon.framework.closures.RemoveItemClosure;
import rustleund.nightdragon.framework.closures.RestoreScaleClosure;
import rustleund.nightdragon.framework.closures.RollDiceClosure;
import rustleund.nightdragon.framework.closures.SetFlagClosure;
import rustleund.nightdragon.framework.closures.TestAnyFlagClosure;
import rustleund.nightdragon.framework.closures.TestFlagClosure;
import rustleund.nightdragon.framework.closures.TestItemClosure;
import rustleund.nightdragon.framework.closures.TestLuckClosure;
import rustleund.nightdragon.framework.closures.TestSkillClosure;
import rustleund.nightdragon.framework.closures.TestStatClosure;

/**
 * @author rustlea
 */
public class AbstractCommandLoader {

	private static final Map<String, Class<? extends Command>> COMMAND_MAPPINGS = new HashMap<String, Class<? extends Command>>();

	static {
		COMMAND_MAPPINGS.put("adjustscale", AdjustScaleClosure.class);
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
	}

	public static Command getAbstractCommand(Element commandTag) {
		Command result = null;
		String commandTagType = commandTag.getNodeName();
		Class<? extends Command> commandClass = COMMAND_MAPPINGS.get(commandTagType);
		try {
			Constructor<? extends Command> constructor = commandClass.getConstructor(new Class[] { Element.class });
			result = constructor.newInstance(new Object[] { commandTag });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Command loadChainedClosure(Element commandGroupTag) {
		List<Command> commands = new ArrayList<Command>();
		NodeList commandTags = commandGroupTag.getChildNodes();
		for (int i = 0; i < commandTags.getLength(); i++) {
			Node commandNode = commandTags.item(i);
			if (commandNode instanceof Element) {
				commands.add(getAbstractCommand((Element) commandNode));
			}
		}
		return new ChainedCommand(commands);
	}

}