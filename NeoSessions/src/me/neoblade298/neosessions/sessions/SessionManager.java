package me.neoblade298.neosessions.sessions;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerLoadCompleteEvent;
import com.sucy.skill.api.player.PlayerData;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.bungee.BungeeAPI;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neosessions.NeoSessions;

// Only loaded in session hosts, NOT from sender hosts
public class SessionManager implements Listener {
	private static HashMap<UUID, SessionPlayer> players = new HashMap<UUID, SessionPlayer>();
	private static HashMap<String, Session> sessions = new HashMap<String, Session>();
	private static HashMap<String, SessionInfo> info;
	private static Location spawn;
	
	private static final String DEFAULT_DIRECTOR = "Towny";
	
	public SessionManager(ConfigurationSection cfg) {
		spawn = Util.stringToLoc(cfg.getString("spawn"));
		info = NeoSessions.getSessionInfo();
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		new BukkitRunnable() {
			public void run() {
				Player p = e.getPlayer();
				UUID uuid = p.getUniqueId();

				if (p.isDead()) {
					p.spigot().respawn();
				}

				try {
					Statement stmt = NeoCore.getStatement();
					ResultSet rs = stmt.executeQuery("SELECT * FROM neosessions_players WHERE uuid = '" + uuid + "';");
					if (!rs.next()) {
						p.teleport(spawn);
						Bukkit.getServer().getLogger().warning("[NeoSessions] Failed to send " + p.getName()
						+ " to session, no such session player exists in SQL.");
						return;
					}
					
					// Create session player
					String sessionKey = rs.getString(2);
					SessionPlayer sp = new SessionPlayer(p, sessionKey);
					SessionInfo si = info.get(sessionKey);
					players.put(p.getUniqueId(), sp);
					
					// Find session, create it if it doesn't exist
					Session s = null;
					if (!sessions.containsKey(sessionKey)) {
						rs = stmt.executeQuery("SELECT * FROM neosessions_sessions WHERE `key` = '" + sessionKey +
								"' AND instance = '" + NeoCore.getInstanceKey() + "';");
						if (!rs.next()) {
							p.teleport(spawn);
							Bukkit.getServer().getLogger().warning("[NeoSessions] Failed to send " + p.getName()
									+ " to session " + sessionKey + ", no such session exists in SQL.");
							return;
						}
						String from = rs.getString("from");
						int numPlayers = rs.getInt("numplayers");
						int multiplier = rs.getInt("multiplier");
						s = info.get(sessionKey).createSession(from, numPlayers, multiplier);
						Bukkit.getServer().getLogger()
						.info("[NeoSessions] " + p.getName() + " created session " + sessionKey + " from " + from +
								" with " + numPlayers + " players and multiplier " + multiplier + ".");
					}
					
					// Add and send player to session
					s.addPlayer(sp);
					p.teleport(si.getSpawn());
					Bukkit.getServer().getLogger()
					.info("[NeoSessions] " + p.getName() + " sent to session " + sessionKey + ".");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.runTaskAsynchronously(NeoSessions.inst());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onLoad(PlayerLoadCompleteEvent e) {
		Player p = e.getPlayer();
		if (!players.containsKey(p.getUniqueId())) {
			Bukkit.getServer().getLogger().warning("[NeoSessions] Failed to run onLoad for " + p.getName()
			+ ", sessionplayer doesn't exist.");
			return;
		}
		SessionPlayer sp = players.get(p.getUniqueId());
		if (sp.getSession() == null) {
			Bukkit.getServer().getLogger().warning("[NeoSessions] Failed to run onLoad for " + p.getName()
			+ ", sessionplayer doesn't belong to a session.");
			return;
		}
		sp.setStatus(PlayerStatus.AWAITING_PLAYERS);
		Session s = sp.getSession();

		// If last one to load, start the session
		if (s.canStart()) {
			// Heal players
			new BukkitRunnable() {
				public void run() {
					for (SessionPlayer sp : s.getSessionPlayers().values()) {
						Player p = sp.getPlayer();
						// Second heal to be safe
						p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
						PlayerData pd = SkillAPI.getPlayerData(p);
						if (pd.getClass("class") != null && pd.getClass("class").getData().getManaName().contains("MP")) {
							pd.setMana(pd.getMaxMana());
						}
					}
				}
			}.runTaskLater(NeoSessions.inst(), 40L);

			// Wait 3 seconds so everyone can reorient themselves
			new BukkitRunnable() {
				public void run() {
					s.start();
				}
			}.runTaskLater(NeoSessions.inst(), 60L);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		new BukkitRunnable() {
			public void run() {
				Player p = e.getPlayer();
				if (!players.containsKey(p.getUniqueId())) return; // The session player doesn't exist
				SessionPlayer sp = players.get(p.getUniqueId());
				// If a player logged in already dead
				if (sp.getStatus() == PlayerStatus.JOINING) {
					sp.getPlayer().spigot().respawn();
				}
			}
		}.runTaskLater(NeoSessions.inst(), 20L);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		UUID uuid = p.getUniqueId();
		SessionPlayer sp = players.get(uuid);
		if (sp == null) return;

		// Remove player from fight, add to spectate
		if (sp.getStatus() == PlayerStatus.PARTICIPATING) {
			sp.setStatus(PlayerStatus.SPECTATING);
			sp.getPlayer().spigot().respawn();
			Bukkit.getServer().getLogger()
			.info("[NeoSessions] " + p.getName() + " switched from fighting to spectating " + sp.getSessionKey() + ".");
		}
		if (!sp.getSession().isEmpty()) {
			sp.getPlayer().teleport(sp.getSession().getLastCheckpoint());
		}
		
		handleParticipantLeave(sp);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		handleLeave(e.getPlayer());
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		handleLeave(e.getPlayer());
	}
	
	@EventHandler
	public void onMythicDespawn(MythicMobDespawnEvent e) {
		for (Session s : sessions.values()) {
			if (s instanceof BossSession) {
				ActiveMob comp = ((BossSession) s).getBossSpawn();
				if (comp != null && comp == e.getMob()) {
					Bukkit.getLogger().warning("[NeoSessions] Spawn mob of " + s.getSessionInfo().getKey() + " was despawned.");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMythicmobKill(MythicMobDeathEvent e) {
		try {
			if (e.getKiller() == null || !(e.getKiller() instanceof Player)) return;
			
			Player p = (Player) e.getKiller();
			SessionPlayer sp = players.get(p.getUniqueId());
			if (sp.getSession() instanceof DungeonSession) {
				DungeonSession s = (DungeonSession) sp.getSession();
				if (e.getMob().getSpawner() != null) {
					s.getAliveSpawners().remove(e.getMob().getSpawner().getInternalName());
					if (s.getAliveSpawners().size() == 0) {
						s.end();
					}
				}
				
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void handleLeave(Player p) {
		SkillAPI.saveSingle(p);
		if (SkillAPI.getPlayerData(p).getSkillBar().isEnabled()) {
			SkillAPI.getPlayerData(p).getSkillBar().toggleEnabled();
		}
		
		if (players.containsKey(p.getUniqueId())) {
			SessionPlayer sp = players.get(p.getUniqueId());
			switch (sp.getStatus()) {
			case PARTICIPATING:
				handleParticipantLeave(sp);
				break;
			case SPECTATING:
				sp.setStatus(PlayerStatus.LEAVING);
				break;
			default:
				break;
			}
		}
	}
	
	// Called whenever a participant is lost, aka leave server or die
	private void handleParticipantLeave(SessionPlayer sp) {
		Session s = sp.getSession();
		// Session is empty, end it
		if (s.isEmpty()) {
			Bukkit.getServer().getLogger().info(
					"[NeoSessions] Session " + s.getSessionInfo().getKey() + " ended.");
			sp.getPlayer().teleport(spawn);
			s.end();
			sessions.remove(s.getSessionInfo().getKey());
		}
	}
	
	public static void endSession(String key) {
		sessions.remove(key).end();
	}
	
	public static void returnPlayer(Player p) {
		SkillAPI.saveSingle(p);
		
		SessionPlayer sp = players.get(p.getUniqueId());
		sp.setStatus(PlayerStatus.LEAVING);
		String from = sp == null ? DEFAULT_DIRECTOR : sp.getSession().getFrom();
		Util.msg(p, "Sending you back in 3 seconds...");
		
		new BukkitRunnable() {
			public void run() {
				BungeeAPI.sendPlayer(p, from);
			}
		}.runTaskLater(NeoSessions.inst(), 60L);
	}
	
	public static SessionPlayer getPlayer(Player p) {
		return players.get(p.getUniqueId());
	}
}
