/*
 * Created on Jul 7, 2004
 */
package rustleund.nightdragon.framework.closures;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.Closure;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(GameState gameState) {
		Document targetPageDocument = null;
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			targetPageDocument = documentBuilder.parse(ClassLoader.getSystemResourceAsStream("nightdragon/pages/" + pageName + ".xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (targetPageDocument != null) {
			gameState.setPageState(new PageState(targetPageDocument, gameState));

			for (Closure closure : gameState.getPageState().getImmediateCommands()) {
				closure.execute(gameState);
			}

			gameState.setPageLoaded(false);
		}
	}

}