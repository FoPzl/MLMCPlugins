package me.neoblade298.neosessions.sessions;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.RandomInt;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import me.neoblade298.neocore.util.Util;

public class RaidSession extends Session {
	private DungeonSessionInfo info;
	private int totalSpawners;
	private HashSet<String> spawnersAlive = new HashSet<String>();
	public RaidSession(DungeonSessionInfo info, String from, int numPlayers, int multiplier) {
		super(info, from, numPlayers, multiplier);
		this.info = info;
	}
	

	@Override
	public void start() {
		super.start(); // Always run first
		super.startStats(info.getKey(), info.getDisplay());
		for (Entry<String, Integer> e : info.getSpawners().entrySet()) {
			String spawner = e.getKey();
			int max = e.getValue();
			for (int i = 1; i <= max; i++) {
				MythicSpawner ms = MythicBukkit.inst().getSpawnerManager()
						.getSpawnerByName(spawner + i);
				if (ms != null) {
					totalSpawners++;
					ms.setMobLevel(new RandomInt("" + super.getMultiplier()));
					ms.setRemainingCooldownSeconds(0L);
					spawnersAlive.add(ms.getInternalName());
				}
				else {
					Bukkit.getLogger().log(Level.WARNING,
							"[NeoBossInstances] Failed to load spawner " + spawner + i);
				}
			}
		}
	}

	@Override
	public void end() {
		for (SessionPlayer sp : super.getSessionPlayers().values()) {
			Util.msg(sp.getPlayer(), "The dungeon has been cleared!");
		}
		super.end(); // Always run last
	}

	@Override
	public DungeonSessionInfo getSessionInfo() {
		return info;
	}
	
	public HashSet<String> getAliveSpawners() {
		return spawnersAlive;
	}
	
	public int getTotalSpawners() {
		return totalSpawners;
	}
}
