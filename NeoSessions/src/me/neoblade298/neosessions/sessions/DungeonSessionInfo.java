package me.neoblade298.neosessions.sessions;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.neoblade298.neocore.info.BossInfo;
import me.neoblade298.neocore.info.InfoAPI;
import me.neoblade298.neocore.util.Util;

public class DungeonSessionInfo extends SessionInfo {
	private HashMap<String, Integer> spawners = new HashMap<String, Integer>();
	
	public DungeonSessionInfo(ConfigurationSection cfg) {
		super(cfg);
		ConfigurationSection sec = cfg.getConfigurationSection("spawners");
		for (String spawner : sec.getKeys(false)) {
			spawners.put(spawner, sec.getInt(spawner));
		}
	}

	@Override
	public Session createSession(String from, int numPlayers, int multiplier) {
		return new DungeonSession(this, from, numPlayers, multiplier);
	}
	
	public HashMap<String, Integer> getSpawners() {
		return spawners;
	}
}
