/*
 * Created on Oct 7, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

import com.google.common.base.Function;

/**
 * @author rustlea
 */
public class DefaultClosureLoader implements ClosureLoader {

	private Map<String, Function<Element, Closure>> mappings;

	public DefaultClosureLoader(Map<String, Function<Element, Closure>> mappings) {
		this.mappings = mappings;
	}

	@Override
	public Closure loadClosureFromElement(Element element) {
		String commandTagType = element.getNodeName();
		if (this.mappings.containsKey(commandTagType)) {
			return this.mappings.get(commandTagType).apply(element);
		}
		return null;
	}

	@Override
	public Closure loadClosureFromChildren(Element element) {
		List<Closure> closures = new ArrayList<Closure>();
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode instanceof Element) {
				closures.add(loadClosureFromElement((Element) childNode));
			}
		}
		return new ChainedClosure(closures);
	}

	@Override
	public Closure loadClosureFromChild(Element element, String childName) {
		NodeList possibles = element.getElementsByTagName(childName);
		for (int i = 0; i < possibles.getLength(); i++) {
			Element possible = (Element) possibles.item(i);
			if (possible.getParentNode().isSameNode(element)) {
				return loadClosureFromChildren(possible);
			}
		}
		return new ChainedClosure();
	}

}