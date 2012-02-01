/*
 * Created on Oct 14, 2005
 */
package rustleund.nightdragon.framework.closures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.Closure;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.Scale;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestStatClosure extends AbstractCommand {

	private static final Map<String, List<Boolean>> VALUES_MAPPINGS = new HashMap<String, List<Boolean>>();

	static {
		VALUES_MAPPINGS.put("lessThan", Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.FALSE }));
		VALUES_MAPPINGS.put("greaterThan", Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.FALSE, Boolean.TRUE }));
		VALUES_MAPPINGS.put("atLeast", Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE, Boolean.TRUE }));
		VALUES_MAPPINGS.put("atMost", Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.TRUE, Boolean.FALSE }));
		VALUES_MAPPINGS.put("equal", Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE, Boolean.FALSE }));
		VALUES_MAPPINGS.put("notEqual", Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.TRUE }));
	}

	private List<Boolean> acceptableValues;
	private Integer valueToCompare;
	private String stat;
	private Closure successful;
	private Closure unsuccessful;
	private boolean useInitialValue = false;

	public TestStatClosure(Element element) {
		this.stat = element.getAttribute("stat");

		for (String attributeTest : VALUES_MAPPINGS.keySet()) {
			if (element.hasAttribute(attributeTest)) {
				this.acceptableValues = VALUES_MAPPINGS.get(attributeTest);
				this.valueToCompare = Integer.valueOf(element.getAttribute(attributeTest));
				break;
			}
		}

		this.successful = AbstractCommandLoader.loadClosureFromChildTag(element, "successful");
		this.unsuccessful = AbstractCommandLoader.loadClosureFromChildTag(element, "unsuccessful");

		this.useInitialValue = element.hasAttribute("useInitialValue") && "true".equals(element.getAttribute("useInitialValue"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(GameState gameState) {
		try {
			PlayerState playerState = gameState.getPlayerState();

			Scale statScale = (Scale) PropertyUtils.getProperty(playerState, this.stat);
			Integer statValue = null;
			if (this.useInitialValue) {
				statValue = statScale.getUpperBound();
			} else {
				statValue = Integer.valueOf(statScale.getCurrentValue());
			}

			boolean valueIsAcceptable = false;
			int compareResult = statValue.compareTo(this.valueToCompare);

			valueIsAcceptable |= acceptableValues.get(0) && (compareResult < 0);
			valueIsAcceptable |= acceptableValues.get(1) && (compareResult == 0);
			valueIsAcceptable |= acceptableValues.get(2) && (compareResult > 0);

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