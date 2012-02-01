/*
 * Created on Oct 14, 2005
 */
package rustleund.nightdragon.framework.closures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Closure;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.Scale;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestStatClosure extends AbstractCommand {

	private static final Map VALUES_MAPPINGS = new HashMap();

	static {
		VALUES_MAPPINGS.put("lessThan", Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.FALSE }));
		VALUES_MAPPINGS.put("greaterThan", Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.FALSE, Boolean.TRUE }));
		VALUES_MAPPINGS.put("atLeast", Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE, Boolean.TRUE }));
		VALUES_MAPPINGS.put("atMost", Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.TRUE, Boolean.FALSE }));
		VALUES_MAPPINGS.put("equal", Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE, Boolean.FALSE }));
		VALUES_MAPPINGS.put("notEqual", Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.TRUE }));
	}

	private List acceptableValues = null;

	private Comparable valueToCompare = null;

	private String stat = null;

	private Closure successful = null;

	private Closure unsuccessful = null;

	private boolean useInitialValue = false;

	public TestStatClosure(Element element) {

		this.stat = element.getAttribute("stat");

		for (Iterator iter = VALUES_MAPPINGS.keySet().iterator(); iter.hasNext();) {
			String attributeTest = (String) iter.next();
			if (element.hasAttribute(attributeTest)) {
				this.acceptableValues = (List) VALUES_MAPPINGS.get(attributeTest);
				this.valueToCompare = new Integer(element.getAttribute(attributeTest));
				break;
			}
		}

		this.successful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("successful").item(0));
		this.unsuccessful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("unsuccessful").item(0));

		this.useInitialValue = element.hasAttribute("useInitialValue") && "true".equals(element.getAttribute("useInitialValue"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(Object arg0) {
		try {
			GameState gameState = (GameState) arg0;
			PlayerState playerState = gameState.getPlayerState();

			Scale statScale = (Scale) PropertyUtils.getProperty(playerState, this.stat);
			Comparable statValue = null;
			if (this.useInitialValue) {
				statValue = statScale.getUpperBound();
			} else {
				statValue = new Integer(statScale.getCurrentValue());
			}

			boolean valueIsAcceptable = false;
			int compareResult = statValue.compareTo(this.valueToCompare);

			valueIsAcceptable |= ((Boolean) acceptableValues.get(0)).booleanValue() && (compareResult < 0);
			valueIsAcceptable |= ((Boolean) acceptableValues.get(1)).booleanValue() && (compareResult == 0);
			valueIsAcceptable |= ((Boolean) acceptableValues.get(2)).booleanValue() && (compareResult > 0);

			if (valueIsAcceptable) {
				this.successful.execute(gameState);
			} else {
				this.unsuccessful.execute(gameState);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}