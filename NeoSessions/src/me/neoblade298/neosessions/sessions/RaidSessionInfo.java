package me.neoblade298.neosessions.sessions;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;

public class RaidSessionInfo extends SessionInfo {
	private HashMap<String, RaidBoss> bosses = new HashMap<String, RaidBoss>();
	private String cmd;
	
	public RaidSessionInfo(ConfigurationSection cfg) {
		super(cfg);
		cmd = cfg.getString("start-command");
		ConfigurationSection sec = cfg.getConfigurationSection("bosses");
		for (String key : sec.getKeys(false)) {
			bosses.put(key, new RaidBoss(sec.getConfigurationSection(key)));
		}
	}

	@Override
	public Session createSession(String from, int numPlayers, int multiplier) {
		return new RaidSession(this, from, numPlayers, multiplier);
	}
	
	public HashMap<String, RaidBoss> getBosses() {
		return bosses;
	}
	
	public String getStartCommand() {
		return cmd;
	}
}
