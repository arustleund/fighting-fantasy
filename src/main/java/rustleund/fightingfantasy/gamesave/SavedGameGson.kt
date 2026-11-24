package rustleund.fightingfantasy.gamesave

import com.google.common.collect.Range
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import rustleund.fightingfantasy.framework.base.BattleEffects
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.GameController
import rustleund.fightingfantasy.framework.base.Item
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure

/**
 * The [Gson] instances that write and read [SavedGame]s.
 */
object SavedGameGson {

    @JvmStatic
    fun forSaving(): Gson = GsonBuilder()
        .registerTypeAdapter(Item::class.java, ItemSerializer())
        .registerTypeAdapter(Range::class.java, IntegerRangeSerializer())
        .registerTypeAdapter(BattleEffects::class.java, BattleEffectsSerializer())
        .registerTypeHierarchyAdapter(SerializableClosure::class.java, ClosureSerializer())
        .create()

    @JvmStatic
    fun forLoading(
        gameController: GameController,
        closureLoader: ClosureLoader,
        battleEffectsLoader: BattleEffectsLoader
    ): Gson = GsonBuilder()
        .registerTypeAdapter(Item::class.java, ItemDeserializer(gameController))
        .registerTypeAdapter(Range::class.java, IntegerRangeDeserializer())
        .registerTypeAdapter(BattleEffects::class.java, BattleEffectsDeserializer(battleEffectsLoader))
        .registerTypeAdapter(Closure::class.java, ClosureDeserializer())
        .registerTypeAdapter(LinkClosure::class.java, LinkClosureDeserializer(closureLoader, battleEffectsLoader))
        .create()
}
