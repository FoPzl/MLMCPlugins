package me.neoblade298.neosessions.sessions;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.neoblade298.neocore.info.BossInfo;
import me.neoblade298.neocore.info.InfoAPI;
import me.neoblade298.neocore.util.Util;

public class RaidBoss {
	private String key;
	private BossInfo bi;
	private MythicMob mob;
	private Location playerSpawn, mobSpawn;
	
	public RaidBoss(ConfigurationSection cfg) {
		this.key = cfg.getName();
		this.bi = InfoAPI.getBossInfo(key);
		this.mob = MythicBukkit.inst().getMobManager().getMythicMob(cfg.getString("mob")).get();
		this.playerSpawn = Util.stringToLoc(cfg.getString("player-spawn"));
		this.mobSpawn = Util.stringToLoc(cfg.getString("mob-spawn"));
	}
	
	public void spawn(int multiplier) {
		mob.spawn(BukkitAdapter.adapt(mobSpawn), multiplier);
	}
	
	public Location getPlayerSpawn() {
		return playerSpawn;
	}
	
	public Location getMobSpawn() {
		return mobSpawn;
	}
	
	public String getKey() {
		return key;
	}
	
	public BossInfo getBossInfo() {
		return bi;
	}
}
