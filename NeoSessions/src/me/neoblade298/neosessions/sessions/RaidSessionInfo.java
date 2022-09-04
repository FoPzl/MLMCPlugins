package me.neoblade298.neosessions.sessions;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.neoblade298.neocore.info.BossInfo;
import me.neoblade298.neocore.info.InfoAPI;
import me.neoblade298.neocore.util.Util;

public class RaidSessionInfo extends SessionInfo {
	private HashMap<String, RaidBoss> bosses = new HashMap<String, RaidBoss>();
	
	public RaidSessionInfo(ConfigurationSection cfg) {
		super(cfg);
		ConfigurationSection sec = cfg.getConfigurationSection("bosses");
		for (String key : sec.getKeys(false)) {
			bosses.put(key, new RaidBoss(sec.getConfigurationSection(key)));
		}
	}

	@Override
	public Session createSession(String from, int numPlayers, int multiplier) {
		return new BossSession(this, from, numPlayers, multiplier);
	}
	
	public HashMap<String, RaidBoss> getBosses() {
		return bosses;
	}
}
