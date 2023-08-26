package rustleund.fightingfantasy.framework.closures.impl

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.gamesave.SerializableClosure

class ChainedClosure(private val closures: List<Closure> = listOf()) : SerializableClosure {

    override fun execute(gameState: GameState) = closures.all { it.execute(gameState) }

    override fun serialize(context: JsonSerializationContext): JsonElement {
        val result = JsonObject()
        result.add("closures", context.serialize(closures))
        return result
    }

    override fun toString(): String {
        return closures.toString()
    }
}