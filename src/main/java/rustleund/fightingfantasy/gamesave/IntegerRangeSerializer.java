package rustleund.fightingfantasy.gamesave;

import java.lang.reflect.Type;

import com.google.common.collect.Range;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class IntegerRangeSerializer implements JsonSerializer<Range<Integer>> {

	@Override
	public JsonElement serialize(Range<Integer> src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		if (src.hasLowerBound()) {
			jsonObject.addProperty("lowerBoundType", src.lowerBoundType().name());
			jsonObject.addProperty("lowerBoundValue", src.lowerEndpoint());
		} else {
			jsonObject.add("lowerBoundType", JsonNull.INSTANCE);
		}
		if (src.hasUpperBound()) {
			jsonObject.addProperty("upperBoundType", src.upperBoundType().name());
			jsonObject.addProperty("upperBoundValue", src.upperEndpoint());
		} else {
			jsonObject.add("upperBoundType", JsonNull.INSTANCE);
		}
		return jsonObject;
	}
}
