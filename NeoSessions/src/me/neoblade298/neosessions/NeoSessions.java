package me.neoblade298.neosessions;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.commands.CommandManager;
import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.instancing.InstanceType;
import me.neoblade298.neosessions.directors.DirectorManager;
import me.neoblade298.neosessions.sessions.BossSessionInfo;
import me.neoblade298.neosessions.sessions.DungeonSessionInfo;
import me.neoblade298.neosessions.sessions.RaidSessionInfo;
import me.neoblade298.neosessions.sessions.SessionInfo;
import me.neoblade298.neosessions.sessions.SessionManager;

public class NeoSessions extends JavaPlugin {
	private static NeoSessions inst;
	private static HashMap<String, SessionInfo> info = new HashMap<String, SessionInfo>();
	public static RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
	
	public void onEnable() {
		inst = this;
		Bukkit.getServer().getLogger().info("NeoSessions Enabled");
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
		try {
			NeoCore.loadFiles(new File(NeoSessions.inst().getDataFolder(), "bosses.yml"), (cfg, file) -> {
				for (String key : cfg.getKeys(false)) {
					info.put(key, new BossSessionInfo(cfg.getConfigurationSection(key)));
				}
			});
			NeoCore.loadFiles(new File(NeoSessions.inst().getDataFolder(), "dungeons.yml"), (cfg, file) -> {
				for (String key : cfg.getKeys(false)) {
					info.put(key, new DungeonSessionInfo(cfg.getConfigurationSection(key)));
				}
			});
			NeoCore.loadFiles(new File(NeoSessions.inst().getDataFolder(), "raids.yml"), (cfg, file) -> {
				for (String key : cfg.getKeys(false)) {
					info.put(key, new RaidSessionInfo(cfg.getConfigurationSection(key)));
				}
			});
		} catch (NeoIOException e) {
			e.printStackTrace();
		}
		
		if (NeoCore.getInstanceType() == InstanceType.SESSIONS) {
			initSessionCommands();
			Bukkit.getPluginManager().registerEvents(new SessionManager(config.getConfigurationSection("sessions")), this);
		}
		else {
			initDirectorCommands();
			Bukkit.getPluginManager().registerEvents(new DirectorManager(config.getConfigurationSection("directors")), this);
		}
		
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoSessions Disabled");
	    super.onDisable();
	}
	
	private void initSessionCommands() {
		CommandManager mngr = new CommandManager("session", this);
	}
	
	private void initDirectorCommands() {
		
	}
	
	public static NeoSessions inst() {
		return inst;
	}
	
	public static HashMap<String, SessionInfo> getSessionInfo() {
		return info;
	}
}
