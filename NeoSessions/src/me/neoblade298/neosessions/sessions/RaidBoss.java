package me.neoblade298.neosessions.sessions;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.neoblade298.neocore.util.Util;

public class RaidBoss {
	private MythicMob mob;
	private Location playerSpawn, mobSpawn;
	
	public RaidBoss(ConfigurationSection cfg) {
		this.mob = MythicBukkit.inst().getMobManager().getMythicMob(cfg.getString("mob")).get();
		this.playerSpawn = Util.stringToLoc(cfg.getString("player-spawn"));
		this.mobSpawn = Util.stringToLoc(cfg.getString("mob-spawn"));
	}
	
	public void spawn(int multiplier) {
		mob.spawn(BukkitAdapter.adapt(mobSpawn), multiplier);
	}
}
