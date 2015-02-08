package rustleund.fightingfantasy.gamesave;

import java.lang.reflect.Type;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class IntegerRangeDeserializer implements JsonDeserializer<Range<Integer>> {

	@Override
	public Range<Integer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject asJsonObject = json.getAsJsonObject();
		JsonElement lowerBoundType = asJsonObject.get("lowerBoundType");
		boolean hasLowerBoundType = lowerBoundType != null && !lowerBoundType.isJsonNull();
		JsonElement upperBoundType = asJsonObject.get("upperBoundType");
		boolean hasUpperBoundType = upperBoundType != null && !upperBoundType.isJsonNull();
		if (hasLowerBoundType && hasUpperBoundType) {
			return Range.range(asJsonObject.get("lowerBoundValue").getAsInt(), BoundType.valueOf(lowerBoundType.getAsString()),
					asJsonObject.get("upperBoundValue").getAsInt(), BoundType.valueOf(upperBoundType.getAsString()));
		}
		if (hasLowerBoundType) {
			return Range.downTo(asJsonObject.get("lowerBoundValue").getAsInt(), BoundType.valueOf(lowerBoundType.getAsString()));
		}
		if (hasUpperBoundType) {
			return Range.upTo(asJsonObject.get("upperBoundValue").getAsInt(), BoundType.valueOf(upperBoundType.getAsString()));
		}
		return Range.all();
	}
}
