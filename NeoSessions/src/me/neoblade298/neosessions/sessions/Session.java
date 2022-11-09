package me.neoblade298.neosessions.sessions;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.scheduler.SchedulerAPI;
import me.neoblade298.neocore.scheduler.SchedulerAPI.CoreRunnable;
import me.neoblade298.neosessions.NeoSessions;
import me.neoblade298.neosessions.sessions.stats.Stats;

public abstract class Session {
	private Location lastCheckpoint;
	private int numPlayers, multiplier;
	private SessionInfo info;
	private String from;
	private long startTime = System.currentTimeMillis();
	private HashMap<UUID, SessionPlayer> players = new HashMap<UUID, SessionPlayer>();
	private HashMap<String, Stats> stats = new HashMap<String, Stats>();
	private ArrayList<CoreRunnable> tasks = new ArrayList<CoreRunnable>();

	public Session(SessionInfo info, String from, int numPlayers, int multiplier) {
		this.from = from;
		this.numPlayers = numPlayers;
		this.multiplier = multiplier;
		this.info = info;
		this.lastCheckpoint = info.getSpawn();
	}

	public void start() {
		if (info.getTimeLimitInMinutes() != -1) {
			long timeLimit = System.currentTimeMillis() + (info.getTimeLimitInMinutes() * 60 * 1000);
			tasks.add(SchedulerAPI.schedule("session-" + info.getKey(), timeLimit, new Runnable() {
				public void run() {
					end();
				}
			}));
		}
		for (SessionPlayer sp : players.values()) {
			sp.setStatus(PlayerStatus.PARTICIPATING);
		}
	}

	public void end() {
		new BukkitRunnable() {
			public void run() {
				for (SessionPlayer sp : players.values()) {
					Player p = sp.getPlayer();
					if (p.isOnline()) {
						SessionManager.returnPlayer(p);
					}
				}

				try {
					Statement stmt = NeoCore.getStatement();
					stmt.addBatch("DELETE FROM sessions_sessions WHERE `key` = '" + info.getKey() + "';");
					stmt.addBatch("DELETE FROM sessions_players WHERE session_key = '" + info.getKey() + "';");
					stmt.executeBatch();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.runTaskLater(NeoSessions.inst(), 20L);
	}
	
	public void setCheckpoint(String key) {
		lastCheckpoint = info.getCheckpoint(key);
	}

	public void addPlayer(SessionPlayer sp) {
		players.put(sp.getUUID(), sp);
		sp.setSession(this);
	}

	public void removePlayer(SessionPlayer sp) {
		players.remove(sp.getUUID());
		sp.setSession(null);
	}

	public HashMap<UUID, SessionPlayer> getSessionPlayers() {
		return players;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public abstract SessionInfo getSessionInfo();

	public boolean canStart() {
		if (players.size() < numPlayers) {
			return false;
		}
		for (SessionPlayer sp : players.values()) {
			if (sp.getStatus() != PlayerStatus.AWAITING_PLAYERS) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		for (SessionPlayer sp : players.values()) {
			if (sp.getStatus() == PlayerStatus.PARTICIPATING) {
				return false;
			}
		}
		return true;
	}

	public Location getLastCheckpoint() {
		return lastCheckpoint;
	}

	public void setLastCheckpoint(Location loc) {
		this.lastCheckpoint = loc;
	}
	
	public String getFrom() {
		return from;
	}
	
	public void startStats(String key, String display) {
		stats.put(key, new Stats(key, display, multiplier, players.values()));
	}
	
	public Stats stopStats(String key) {
		return stats.remove(key);
	}
	
	public Stats getStats(String key) {
		return stats.get(key);
	}
	
	public Collection<Stats> getStats() {
		return stats.values();
	}
	
	public long getStartTime() {
		return startTime;
	}
}
