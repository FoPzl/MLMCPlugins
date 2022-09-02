package me.neoblade298.neosessions.sessions;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.info.InfoAPI;
import me.neoblade298.neocore.instancing.InstanceType;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neosessions.NeoSessions;
import me.neoblade298.neosessions.directors.DirectorManager;

public abstract class SessionInfo {
	private String key, display;
	private Location spawn;
	private int cooldownMinutes, maxPlayers;
	private ProtectedRegion teleportRegion;
	public SessionInfo(ConfigurationSection cfg) {
		this.key = cfg.getName();
		this.display = cfg.getString("display", InfoAPI.getBossInfo(key).getDisplay());
		this.spawn = Util.stringToLoc(cfg.getString("player-spawn"));
		this.cooldownMinutes = cfg.getInt("cooldown-in-minutes");
		this.maxPlayers = cfg.getInt("max-players", 6);
		if (NeoCore.getInstanceType() != InstanceType.SESSIONS) {
			String region = cfg.getString("teleport-region");
			teleportRegion = NeoSessions.container.get(BukkitAdapter.adapt(Bukkit.getWorld("Argyll"))).getRegion(region);
		}
	}
	
	public abstract Session createSession(String from, int numPlayers, int multiplier);
	
	public Location getSpawn() {
		return spawn;
	}
	
	public String getKey() {
		return key;
	}
	
	public boolean isInTeleportRegion(Location loc) {
		return teleportRegion.contains(BukkitAdapter.asBlockVector(loc));
	}
	
	public void directToSession(Player trigger) {
		// First calculate everyone being sent
		ArrayList<Player> players = new ArrayList<Player>(maxPlayers);
		players.add(trigger);
		for (Entity e : trigger.getNearbyEntities(20, 20, 20)) {
			if (e instanceof Player && isInTeleportRegion(e.getLocation())) {
				players.add((Player) e);
			}
		}
		
		// Disabled sessions
		if (DirectorManager.sessionsDisabled()) {
			Util.msgGroup(players, "&cSessions are currently disabled!");
			return;
		}
		
		// Max players
		if (players.size() > maxPlayers) {
			Util.msgGroup(players, "&cToo many players! The max # of players for this session is " + maxPlayers + "!");
			return;
		}

		// Cooldowns
		boolean onCooldown = false;
		String msg = "&4[&c&lMLMC&4] §cThe following players are still on cooldown:";
		for (Player p : players) {
    		long lastUse = DirectorManager.getCooldowns().get(key).getOrDefault(p.getUniqueId(), 0L);
    		long currTime = System.currentTimeMillis();
    		long cooldown = cooldownMinutes * 60 * 1000;
    		if (currTime < lastUse + cooldown) {
				int time = (int) (((lastUse + cooldown) - currTime) / 1000);
				int minutes = time / 60;
				int seconds = time % 60;
				msg += "\n&7- &e" + p.getName() + "&7: " + String.format("§c%d:%02d", minutes, seconds);
				onCooldown = true;
    		}
		}
		if (onCooldown) {
			Util.msgGroup(players, msg);
			return;
		}
		
		//Make it find and send to instance
		String instance = DirectorManager.findSessionHost(key);
		if (instance.equals(DirectorManager.NO_INSTANCE_AVAILABLE) || instance.equals(DirectorManager.SQL_FAILED)) {
			Util.msgGroup(players, "&cCould not start session: " + instance + ". Try again later!");
			return;
		}
		
		// Actually send them
		int level = (int) main.settings.getValue(p.getUniqueId(), boss);
		if (level >= 1) {
			level = Math.max((int) main.settings.getValue(p.getUniqueId(), boss), targets.size());
		}
	}
}
