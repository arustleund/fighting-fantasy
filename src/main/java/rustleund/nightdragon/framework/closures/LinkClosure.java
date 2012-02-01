/*
 * Created on Jul 7, 2004
 */
package rustleund.nightdragon.framework.closures;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.Command;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PageState;

/**
 * @author rustlea
 */
public class LinkClosure extends AbstractCommand {

	private String pageName;

	public LinkClosure(Element element) {
		this(element.getAttribute("page"));
	}

	public LinkClosure(int pageNumber) {
		this(pageNumber + "");
	}

	public LinkClosure(String pageName) {
		this.pageName = pageName;
		this.executeSuccessful = true;
	}

	public void execute(GameState gameState) {
		try {
			File targetPage = new File(ClassLoader.getSystemResource("pages/" + pageName + ".xml").toURI());
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document targetPageDocument = documentBuilder.parse(targetPage);
			gameState.setPageState(new PageState(targetPageDocument, gameState));
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Command> immediateCommands = gameState.getPageState().getImmediateCommands();
		for (Command element : immediateCommands) {
			element.execute(gameState);
		}

	}

}