package me.neoblade298.neosessions.sessions.stats;

public class StatsPlayer {
	private double damageDealt, damageTaken, selfHealed, allyHealed;
	
	public StatsPlayer() {
		damageDealt = 0;
		damageTaken = 0;
		selfHealed = 0;
		allyHealed = 0;
	}

	public void addDamageDealt(double amount) {
		damageDealt += amount;
	}
	
	public void addDamageTaken(double amount) {
		damageTaken += amount;
	}
	
	public void addSelfHeal(double amount) {
		selfHealed += amount;
	}
	
	public void addAllyHeal(double amount) {
		allyHealed += amount;
	}
	
	public double getDamageDealt() {
		return damageDealt;
	}
	
	public double getDamageTaken() {
		return damageTaken;
	}
	
	public double getSelfHealed() {
		return selfHealed;
	}
	
	public double getAllyHealed() {
		return allyHealed;
	}
	
}
