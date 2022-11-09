package me.neoblade298.neosessions.sessions;

import org.bukkit.Bukkit;

public class RaidSession extends Session {
	private RaidSessionInfo info;
	public RaidSession(RaidSessionInfo info, String from, int numPlayers, int multiplier) {
		super(info, from, numPlayers, multiplier);
		this.info = info;
	}
	

	@Override
	public void start() {
		super.start(); // Always run first
		super.startStats(info.getKey(), info.getDisplay());
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), info.getStartCommand());
	}

	@Override
	public void end() {
		super.end(); // Always run last
	}

	@Override
	public RaidSessionInfo getSessionInfo() {
		return info;
	}
	
	public void spawnBoss(String key) {
		RaidBoss rb = info.getBosses().get(key);
		rb.spawn(super.getMultiplier());
		super.setCheckpoint(key);
		super.startStats(key, rb.getBossInfo().getDisplay());
		
		for (SessionPlayer sp : super.getSessionPlayers().values()) {
			sp.getPlayer().teleport(rb.getPlayerSpawn());
		}
	}
}
