package me.Neoblade298.NeoConsumables.objects;

public class PlayerCooldowns {
	private long instantCooldown, durationCooldown;
	
	public PlayerCooldowns() {
		instantCooldown = 0;
		durationCooldown = 0;
	}

	public long getInstantCooldown() {
		return instantCooldown;
	}

	public void setInstantCooldown(long instantCooldown) {
		this.instantCooldown = instantCooldown;
	}

	public long getDurationCooldown() {
		return durationCooldown;
	}

	public void setDurationCooldown(long durationCooldown) {
		this.durationCooldown = durationCooldown;
	}
	
	public String toString() {
		return instantCooldown + " " + durationCooldown + " " + System.currentTimeMillis();
	}
}
