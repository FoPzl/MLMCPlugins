package me.neoblade298.neosessions.sessions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.neoblade298.neosessions.NeoSessions;

public abstract class SessionInfo {
	private String key;
	private Location spawn;
	private String teleportRegion;
	public SessionInfo(String key, Location spawn) {
		this.key = key;
		this.spawn = spawn;
	}
	
	public abstract Session createSession(String from, int numPlayers, int multiplier);
	
	public Location getSpawn() {
		return spawn;
	}
	
	public String getKey() {
		return key;
	}
	
	public boolean isInTeleportRegion(Player p) {
		ApplicableRegionSet set = NeoSessions.container.createQuery().getApplicableRegions(BukkitAdapter.adapt(p.getLocation()));
		for (ProtectedRegion r : set) {
			if (r.getId().equals(teleportRegion)) return true;
		}
		return false;
	}
}
