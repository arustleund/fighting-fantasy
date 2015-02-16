/*
 * Created on Jun 26, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.ChainedClosure;

/**
 * @author rustlea
 */
public class PageState {

	private ClosureLoader closureLoader;
	private BattleEffectsLoader battleEffectsLoader;

	private String pageName;
	private GameState gameState = null;
	private String pagetext = null;
	private Map<String, Integer> keepMinimums = null;
	private Map<Integer, Element> testLucks = null;
	private List<Closure> immediateCommands = null;
	private Map<Integer, Closure> multiCommands = null;
	private Map<Integer, String> texts = null;
	private Map<Integer, BattleState> battles = null;

	public PageState(String pageName, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader, Document document, GameState gameState) {
		this.closureLoader = closureLoader;
		this.battleEffectsLoader = battleEffectsLoader;

		this.pageName = pageName;
		this.gameState = gameState;

		loadPagetext(document);
		loadImmediate(document);
		loadMultis(document);
		loadBattles(document);
		loadTexts(document);
		loadTestLucks(document);
		loadKeepMinimum(document);
	}

	private void loadPagetext(Document document) {
		Element pageTextElement = XMLUtil.getChildElementByName(document.getDocumentElement(), "pagetext");
		pagetext = writeTag(XMLUtil.getChildElementByName(pageTextElement, "html"));
	}

	private void loadKeepMinimum(Document document) {
		keepMinimums = new HashMap<>();
		XMLUtil.getChildElementsByName(document.getDocumentElement(), "keepminimum").forEach(e -> {
			keepMinimums.put(e.getAttribute("scale"), Integer.valueOf(e.getAttribute("value")));
		});
	}

	private void loadTestLucks(Document document) {
		testLucks = new HashMap<>();
		XMLUtil.getChildElementsByName(document.getDocumentElement(), "testluck").forEach(e -> {
			testLucks.put(Integer.valueOf(e.getAttribute("id")), e);
		});
	}

	private void loadImmediate(Document document) {
		immediateCommands = new ArrayList<>();
		Element immediateTag = XMLUtil.getChildElementByName(document.getDocumentElement(), "immediate");
		if (immediateTag != null) {
			NodeList immediateCommandTags = immediateTag.getChildNodes();
			for (int i = 0; i < immediateCommandTags.getLength(); i++) {
				Node thisImmediateCommandTag = immediateCommandTags.item(i);
				if (thisImmediateCommandTag instanceof Element) {
					immediateCommands.add(this.closureLoader.loadClosureFromElement((Element) thisImmediateCommandTag));
				}
			}
		}
	}

	private void loadTexts(Document document) {
		texts = new HashMap<>();
		XMLUtil.getChildElementsByName(document.getDocumentElement(), "text").forEach(e -> {
			texts.put(Integer.valueOf(e.getAttribute("id")), writeTag(e));
		});
	}

	private void loadMultis(Document document) {
		this.multiCommands = new HashMap<>();
		XMLUtil.getChildElementsByName(document.getDocumentElement(), "multicommand").forEach(thisMultiTag -> {
			Integer thisMultiId = Integer.valueOf(thisMultiTag.getAttribute("id"));
			NodeList thisMultiTagsCommandTags = thisMultiTag.getChildNodes();
			List<Closure> subclosures = new ArrayList<>();
			for (int j = 0; j < thisMultiTagsCommandTags.getLength(); j++) {
				Node thisMultiTagsCommandTag = thisMultiTagsCommandTags.item(j);
				if (thisMultiTagsCommandTag instanceof Element) {
					subclosures.add(this.closureLoader.loadClosureFromElement((Element) thisMultiTagsCommandTag));
				}
			}
			this.multiCommands.put(thisMultiId, new ChainedClosure(subclosures));
		});
	}

	private void loadBattles(Document document) {
		battles = new HashMap<>();
		XMLUtil.getChildElementsByName(document.getDocumentElement(), "battle").forEach(e -> {
			battles.put(Integer.valueOf(e.getAttribute("id")), new BattleState(e, this, this.closureLoader, this.battleEffectsLoader));
		});
	}

	public BattleState getBattle(int battleId) {
		return battles.get(battleId);
	}

	public Closure getMultiCommands(int multiCommandId) {
		return this.multiCommands.get(multiCommandId);
	}

	public String getSuccessfulLuckText(int testluckId) {
		return getLuckTextHelper(testluckId, "successful");
	}

	public String getUnsuccessfulLuckText(int testluckId) {
		return getLuckTextHelper(testluckId, "unsuccessful");
	}

	private String getLuckTextHelper(int testluckId, String successType) {
		Element successTypeTag = XMLUtil.getChildElementByName(testLucks.get(testluckId), successType);
		return writeTag(XMLUtil.getChildElementByName(successTypeTag, "p"));
	}

	public boolean hasKeepMinimumForScale(String scaleType) {
		return keepMinimums.containsKey(scaleType);
	}

	public int getKeepMinimumForScale(String scaleType) {
		if (keepMinimums.containsKey(scaleType)) {
			return keepMinimums.get(scaleType);
		}
		return -1;
	}

	public String getPagetext() {
		return pagetext;
	}

	public void setPagetext(String string) {
		pagetext = string;
	}

	public List<Closure> getImmediateCommands() {
		return immediateCommands;
	}

	public void addToPagetext(String addition) {
		int endHtmlIndex = pagetext.indexOf("</html>");
		setPagetext(pagetext.substring(0, endHtmlIndex) + addition + "</html>");
	}

	public void replacePagetext(String startString, String endString, String replaceString) {
		int startIndex = pagetext.indexOf(startString);
		int endIndex = pagetext.indexOf(endString);
		if (startIndex == -1 || endIndex == -1) {
			addToPagetext(replaceString);
		} else {
			setPagetext(pagetext.substring(0, startIndex) + replaceString + pagetext.substring(endIndex));
		}
	}

	private String writeTag(Node tag) {
		String result = null;
		try {

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty("omit-xml-declaration", "yes");

			DOMSource source = new DOMSource(tag);

			StringWriter writer = new StringWriter();
			StreamResult streamResult = new StreamResult(writer);

			transformer.transform(source, streamResult);

			result = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public Map<Integer, String> getTexts() {
		return texts;
	}

	public String getPageName() {
		return this.pageName;
	}
}