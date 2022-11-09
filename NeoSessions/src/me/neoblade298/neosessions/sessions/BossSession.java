package me.neoblade298.neosessions.sessions;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;

public class BossSession extends Session {
	private BossSessionInfo info;
	private ActiveMob bossSpawn;
	public BossSession(BossSessionInfo info, String from, int numPlayers, int multiplier) {
		super(info, from, numPlayers, multiplier);
		this.info = info;
	}
	

	@Override
	public void start() {
		super.start(); // Always run first
		super.startStats(info.getKey(), info.getDisplay());
		bossSpawn = info.getBossMob().spawn(BukkitAdapter.adapt(info.getBossSpawn()), super.getMultiplier());
	}

	@Override
	public void end() {
		super.end(); // Always run last
	}

	@Override
	public BossSessionInfo getSessionInfo() {
		return info;
	}
	
	public ActiveMob getBossSpawn() {
		return bossSpawn;
	}
}
