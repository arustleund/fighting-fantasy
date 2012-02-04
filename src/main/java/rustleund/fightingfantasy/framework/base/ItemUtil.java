package rustleund.fightingfantasy.framework.base;

import java.io.File;

public interface ItemUtil {

	void init(File itemConfiguration);

	Item getItem(int itemId);

}
