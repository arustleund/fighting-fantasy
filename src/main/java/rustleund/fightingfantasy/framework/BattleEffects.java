package rustleund.fightingfantasy.framework;

public class BattleEffects {

	private Closure startBattle;
	private Closure startRound;
	private Closure playerFlee;
	private Closure playerHit;
	private Closure endBattle;
	private Closure endRound;

	public Closure getStartBattle() {
		return this.startBattle;
	}

	public void setStartBattle(Closure startBattle) {
		this.startBattle = startBattle;
	}

	public Closure getStartRound() {
		return this.startRound;
	}

	public void setStartRound(Closure startRound) {
		this.startRound = startRound;
	}

	public Closure getPlayerFlee() {
		return this.playerFlee;
	}

	public void setPlayerFlee(Closure playerFlee) {
		this.playerFlee = playerFlee;
	}

	public Closure getPlayerHit() {
		return this.playerHit;
	}

	public void setPlayerHit(Closure playerHit) {
		this.playerHit = playerHit;
	}

	public Closure getEndBattle() {
		return this.endBattle;
	}

	public void setEndBattle(Closure endBattle) {
		this.endBattle = endBattle;
	}

	public Closure getEndRound() {
		return this.endRound;
	}

	public void setEndRound(Closure endRound) {
		this.endRound = endRound;
	}

}
