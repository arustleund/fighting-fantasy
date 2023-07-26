/*
 * Created on Jun 26, 2004
 */
package rustleund.fightingfantasy.framework.base;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;
import rustleund.fightingfantasy.framework.closures.impl.ChainedClosure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rustlea
 */
public class PageState {

    private final ClosureLoader closureLoader;
    private final BattleEffectsLoader battleEffectsLoader;

    private final String pageName;
    private GameState gameState;
    private String pagetext = null;
    private Map<String, Integer> keepMinimums = null;
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
        loadKeepMinimum(document);
    }

    private void loadPagetext(Document document) {
        Element pageTextElement = XMLUtilKt.getChildElementByName(document.getDocumentElement(), "pagetext");
        if (pageTextElement == null) throw new IllegalArgumentException("Missing pagetext element");
        pagetext = XMLUtilKt.writeTag(XMLUtilKt.getChildElementByName(pageTextElement, "html"));
    }

    private void loadKeepMinimum(Document document) {
        keepMinimums = new HashMap<>();
        XMLUtilKt.getChildElementsByName(document.getDocumentElement(), "keepminimum").iterator()
                .forEachRemaining(e -> keepMinimums.put(e.getAttribute("scale"), Integer.valueOf(e.getAttribute("value"))));
    }

    private void loadImmediate(Document document) {
        immediateCommands = new ArrayList<>();
        Element immediateTag = XMLUtilKt.getChildElementByName(document.getDocumentElement(), "immediate");
        if (immediateTag != null) {
            NodeList immediateCommandTags = immediateTag.getChildNodes();
            for (int i = 0; i < immediateCommandTags.getLength(); i++) {
                Node thisImmediateCommandTag = immediateCommandTags.item(i);
                if (thisImmediateCommandTag instanceof Element e) {
                    immediateCommands.add(this.closureLoader.loadClosureFromElement(e));
                }
            }
        }
    }

    private void loadTexts(Document document) {
        texts = new HashMap<>();
        XMLUtilKt.getChildElementsByName(document.getDocumentElement(), "text").iterator()
                .forEachRemaining(e -> texts.put(Integer.valueOf(e.getAttribute("id")), XMLUtilKt.writeTag(e)));
    }

    private void loadMultis(Document document) {
        this.multiCommands = new HashMap<>();
        XMLUtilKt.getChildElementsByName(document.getDocumentElement(), "multicommand").iterator()
                .forEachRemaining(thisMultiTag -> {
                    Integer thisMultiId = Integer.valueOf(thisMultiTag.getAttribute("id"));
                    NodeList thisMultiTagsCommandTags = thisMultiTag.getChildNodes();
                    List<Closure> subclosures = new ArrayList<>();
                    for (int j = 0; j < thisMultiTagsCommandTags.getLength(); j++) {
                        Node thisMultiTagsCommandTag = thisMultiTagsCommandTags.item(j);
                        if (thisMultiTagsCommandTag instanceof Element e) {
                            subclosures.add(this.closureLoader.loadClosureFromElement(e));
                        }
                    }
                    this.multiCommands.put(thisMultiId, new ChainedClosure(subclosures));
                });
    }

    private void loadBattles(Document document) {
        battles = new HashMap<>();
        XMLUtilKt.getChildElementsByName(document.getDocumentElement(), "battle").iterator()
                .forEachRemaining(e -> battles.put(Integer.valueOf(e.getAttribute("id")), new BattleState(e, this, this.closureLoader, this.battleEffectsLoader)));
    }

    public BattleState getBattle(int battleId) {
        return battles.get(battleId);
    }

    public Closure getMultiCommands(int multiCommandId) {
        return this.multiCommands.get(multiCommandId);
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