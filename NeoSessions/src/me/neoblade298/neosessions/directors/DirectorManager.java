package me.neoblade298.neosessions.directors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neosessions.NeoSessions;
import me.neoblade298.neosessions.sessions.SessionInfo;

public class DirectorManager implements Listener {
	private static HashMap<String, HashMap<UUID, Long>> cooldowns = new HashMap<String, HashMap<UUID, Long>>();
	private static List<String> sessionHosts;
	private static HashMap<String, SessionInfo> info;
	private static boolean sessionsDisabled = false;
	public static final String NO_INSTANCE_AVAILABLE = "No Instances Available";
	public static final String SQL_FAILED = "SQL Failed";
	
	public DirectorManager(ConfigurationSection cfg) {
		sessionHosts = cfg.getStringList("session-hosts");
		info = NeoSessions.getSessionInfo();
		
		new BukkitRunnable() {
			public void run() {
				try {
					Statement stmt = NeoCore.getStatement();
					stmt.executeUpdate("DELETE FROM sessions_sessionhosts");
					for (String host : sessionHosts) {
						stmt.addBatch("INSERT INTO sessions_sessionhosts VALUES('" + host + "');");
					}
					stmt.executeBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(NeoSessions.inst());
	}

	public static String findSessionHost(String sessionKey) {
		try {
			Statement stmt = NeoCore.getStatement();
			ResultSet rs = stmt.executeQuery("SELECT instance FROM sessions_sessionhosts WHERE instance NOT IN(" +
					"SELECT instance FROM sessions_sessions WHERE session = '" + sessionKey + "' GROUP BY instance)");
			if (rs.next()) {
				return rs.getString(1);
			}
			else {
				return "All Instances Full";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Bukkit.getLogger().warning("[NeoSessions] Failed to run SQL to find session host.");
		return null;
	}
	
	public static void sendToSessionHost(Player p, String sessionHost, String sessionKey, boolean bypassRestrictions) {
		
	}
	
	public static boolean sessionsDisabled() {
		return sessionsDisabled;
	}
	
	public static HashMap<String, HashMap<UUID, Long>> getCooldowns() {
		return cooldowns;
	}
}
