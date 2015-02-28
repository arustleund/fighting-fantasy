package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PageState;
import rustleund.fightingfantasy.framework.closures.Closure;

public class DisplayEnemiesClosure implements Closure {

	private int battleId;

	public DisplayEnemiesClosure(Element element) {
		this.battleId = Integer.valueOf(element.getAttribute("battleId"));
	}

	@Override
	public boolean execute(GameState gameState) {
		PageState pageState = gameState.getPageState();
		StringBuilder sb = new StringBuilder("<p><table border=\"1\">");
		sb.append("<tr><td> </td><td>SKILL</td><td>STAMINA</td></tr>");
		pageState.getBattle(battleId).getEnemies().forEach(e -> {
			sb.append("<tr><td>");
			sb.append(e.getName());
			sb.append("</td><td>");
			sb.append(e.getSkill().getUpperBound());
			sb.append("</td><td>");
			sb.append(e.getStamina().getUpperBound());
			sb.append("</td></tr>");
		});
		sb.append("</table></p>");
		pageState.addToPagetext(sb.toString());
		return true;
	}
}
