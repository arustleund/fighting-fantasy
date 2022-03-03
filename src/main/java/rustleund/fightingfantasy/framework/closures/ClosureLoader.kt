package rustleund.fightingfantasy.framework.closures;

import org.w3c.dom.Element;

public interface ClosureLoader {

	Closure loadClosureFromElement(Element element);

	Closure loadClosureFromChildren(Element element);

	Closure loadClosureFromChild(Element element, String childName);

}
