package rustleund.fightingfantasy.gamesave;

import java.lang.reflect.Type;

import rustleund.fightingfantasy.framework.base.Item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ItemSerializer implements JsonSerializer<Item> {

	@Override
	public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.addProperty("id", src.getId());
		result.addProperty("count", src.getCount());
		return result;
	}
}
