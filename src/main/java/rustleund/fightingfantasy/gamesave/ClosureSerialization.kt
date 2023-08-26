package rustleund.fightingfantasy.gamesave

import com.google.gson.*
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure
import java.lang.reflect.Type


interface SerializableClosure : Closure {

    fun serialize(context: JsonSerializationContext): JsonElement = GsonBuilder().create().toJsonTree(this)
}

class ClosureSerializer : JsonSerializer<SerializableClosure> {

    override fun serialize(src: SerializableClosure, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("clazz", src::class.java.name)
        jsonObject.add("closure", src.serialize(context))
        return jsonObject
    }
}

class ClosureDeserializer : JsonDeserializer<Closure> {

    override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Closure {
        val jsonObject: JsonObject = jsonElement.asJsonObject
        val prim = jsonObject["clazz"] as JsonPrimitive
        val className = prim.asString
        val klass = Class.forName(className)
        return context.deserialize(jsonObject["closure"], klass)
    }
}

class LinkClosureDeserializer(
    private val closureLoader: ClosureLoader,
    private val battleEffectsLoader: BattleEffectsLoader
) : JsonDeserializer<LinkClosure> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LinkClosure {
        return LinkClosure(json.asJsonObject["page"].asString, closureLoader, battleEffectsLoader)
    }
}