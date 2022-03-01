package rustleund.fightingfantasy.framework.base

interface GameAction {

    /**
     * Do some action that will probably update the [GameState] in some way
     */
    fun doAction()
}