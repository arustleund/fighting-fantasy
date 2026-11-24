package rustleund.fightingfantasy.gamesave

import com.google.gson.*
import org.apache.commons.logging.LogFactory
import rustleund.fightingfantasy.framework.base.BattleEffects
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.parseElement
import java.lang.reflect.Type

private const val SOURCE_XML = "sourceXml"

/**
 * Writes [BattleEffects] as the XML they were loaded from, since the closures in them can't be serialized.
 */
class BattleEffectsSerializer : JsonSerializer<BattleEffects> {

    override fun serialize(src: BattleEffects, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val sourceXml = src.sourceXml
            ?: throw JsonIOException("Battle effects that weren't loaded from XML can't be saved")
        return JsonObject().apply { addProperty(SOURCE_XML, sourceXml) }
    }
}

/**
 * Loads [BattleEffects] again from the XML written by [BattleEffectsSerializer].
 */
class BattleEffectsDeserializer(private val battleEffectsLoader: BattleEffectsLoader) : JsonDeserializer<BattleEffects> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BattleEffects {
        val result = BattleEffects()
        val sourceXml = json.asJsonObject[SOURCE_XML]?.asString
        if (sourceXml == null) {
            // Saves written before battle effects were saved as XML have nothing to load them from
            log.warn("Saved battle effects have no XML to load them from, so they have been dropped: $json")
        } else {
            val effectsTag = runCatching { parseElement(sourceXml) }
                .getOrElse { throw JsonParseException("Unable to parse saved battle effects: $sourceXml", it) }
            battleEffectsLoader.loadBattleEffectsFromTag(result, effectsTag)
        }
        return result
    }

    companion object {
        private val log = LogFactory.getLog(BattleEffectsDeserializer::class.java)
    }
}
