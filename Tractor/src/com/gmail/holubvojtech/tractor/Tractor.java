package com.gmail.holubvojtech.tractor;

import com.gmail.holubvojtech.tractor.commands.TractorCmd;
import com.gmail.holubvojtech.tractor.listeners.BlockListener;
import com.gmail.holubvojtech.tractor.listeners.PlayerListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Tractor extends JavaPlugin {
	public static Logger logger = null;
	public static Tractor plugin = null;
	private Config config;
	private Map<UUID, PlayerData> playerData = new HashMap();
	public static final Map<Material, Material> SOIL_OVERRIDE = new HashMap<Material, Material>() {
		private static final long serialVersionUID = 2558397491751251351L;
	};

	public static Config getCfg() {
		return plugin.config;
	}

	public static PlayerData getPlayerData(Player player) {
		UUID uuid = player.getUniqueId();
		PlayerData data = (PlayerData) plugin.playerData.get(uuid);
		if (data == null) {
			data = new PlayerData();
			plugin.playerData.put(uuid, data);
		}
		return data;
	}

	public static boolean hasPlayerData(Player player) {
		return plugin.playerData.containsKey(player.getUniqueId());
	}

	public Map<UUID, PlayerData> getPlayerData() {
		return this.playerData;
	}

	public void onDisable() {
		logger.info("Disabled");
	}

	public void onEnable() {
		plugin = this;
		logger = getLogger();
		
		SOIL_OVERRIDE.put(Material.NETHER_WART, Material.SOUL_SAND);

		initFiles();

		this.config = new Config(new File(getDataFolder(), "config.yml"));
		this.config.loadFromFile();

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new BlockListener(this), this);

		getCommand("tractor").setExecutor(new TractorCmd());

		logger.info("Enabled");
	}

	private void initFiles() {
		if ((!getDataFolder().exists()) && (!getDataFolder().mkdir())) {
			logger.severe("Could not create plugin folder!");
		}
		String[] res = { "config.yml" };
		String[] arrayOfString1;
		int j = (arrayOfString1 = res).length;
		for (int i = 0; i < j; i++) {
			String name = arrayOfString1[i];
			File file = new File(getDataFolder(), name);
			if (!file.exists()) {
				InputStream inputStream = getResource(name);
				try {
					Files.copy(inputStream, file.toPath(), new CopyOption[0]);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
}
