/*
 * Created on Jul 7, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PageState;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

/**
 * @author rustlea
 */
public class LinkClosure extends AbstractClosure {

	private ClosureLoader closureLoader;
	private BattleEffectsLoader battleEffectsLoader;

	private String pageName;

	public LinkClosure(Element element, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		this(element.getAttribute("page"), closureLoader, battleEffectsLoader);
	}

	public LinkClosure(int pageNumber, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		this(pageNumber + "", closureLoader, battleEffectsLoader);
	}

	public LinkClosure(String pageName, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		this.pageName = pageName;
		this.closureLoader = closureLoader;
		this.battleEffectsLoader = battleEffectsLoader;
	}

	@Override
	public boolean execute(GameState gameState) {
		Document targetPageDocument = null;
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			targetPageDocument = documentBuilder.parse(new File(gameState.getPagesDirectory(), pageName + ".xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (targetPageDocument != null) {
			gameState.setPageState(new PageState(pageName, this.closureLoader, this.battleEffectsLoader, targetPageDocument, gameState));

			for (Closure closure : gameState.getPageState().getImmediateCommands()) {
				closure.execute(gameState);
			}

			gameState.setPageLoaded(false);
		}

		return true;
	}

}