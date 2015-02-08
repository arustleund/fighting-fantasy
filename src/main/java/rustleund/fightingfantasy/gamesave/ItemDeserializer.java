package rustleund.fightingfantasy.gamesave;

import java.lang.reflect.Type;

import rustleund.fightingfantasy.framework.base.GameController;
import rustleund.fightingfantasy.framework.base.Item;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ItemDeserializer implements JsonDeserializer<Item> {

	private GameController gameController;

	@Override
	public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject asJsonObject = json.getAsJsonObject();

		Item item = this.gameController.getItemUtil().getItem(asJsonObject.get("id").getAsInt());
		item.setCount(asJsonObject.get("count").getAsInt());

		return item;
	}
}
