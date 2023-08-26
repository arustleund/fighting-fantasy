package rustleund.fightingfantasy.gamesave

import com.google.gson.GsonBuilder
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.closures.impl.ChainedClosure
import rustleund.fightingfantasy.framework.closures.impl.LinkClosure
import rustleund.fightingfantasy.framework.closures.impl.RestoreScaleClosure
import rustleund.fightingfantasy.framework.closures.impl.SetOnPlayerDeathClosure
import spock.lang.Specification


class ClosureSerializationSpec extends Specification {

    def closureLoader = Stub(ClosureLoader)
    def battleEffectsLoader = Stub(BattleEffectsLoader)

    def "serialize and deserialize"() {
        given:
        def restoreScaleClosure = new RestoreScaleClosure("stamina", "dreamtime")
        def setOnPlayerDeathClosure = new SetOnPlayerDeathClosure(null)
        def linkClosure = new LinkClosure("281", closureLoader, battleEffectsLoader)
        def closureToTest = new ChainedClosure([restoreScaleClosure, setOnPlayerDeathClosure, linkClosure])
        def gsonBuilder = new GsonBuilder()
        gsonBuilder.registerTypeHierarchyAdapter(SerializableClosure.class, new ClosureSerializer())
        def gson = gsonBuilder.create()

        def gsonDBuilder = new GsonBuilder()
        gsonDBuilder.registerTypeAdapter(Closure.class, new ClosureDeserializer())
        gsonDBuilder.registerTypeAdapter(LinkClosure.class, new LinkClosureDeserializer(closureLoader, battleEffectsLoader))
        def gsonD = gsonDBuilder.create()

        when:
        def result = gson.toJson(closureToTest)
        def backAgain = gsonD.fromJson(result, Closure.class)

        then:
        result != null
        backAgain != null
    }
}