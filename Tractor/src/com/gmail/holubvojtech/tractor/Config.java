package com.gmail.holubvojtech.tractor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	private File file;
	private FileConfiguration config;
	private Map<Material, Material> seedTypes = new HashMap();
	private int height;

	public Config(File file) {
		this.file = file;
	}

	public void loadFromFile() {
		this.config = YamlConfiguration.loadConfiguration(this.file);
		loadMessages();

		this.seedTypes.clear();
		ConfigurationSection seedTypes = this.config.getConfigurationSection("seed_types");
		for (String seedBlock : seedTypes.getKeys(false)) {
			String seedItem = seedTypes.getString(seedBlock);

			Material block = Material.matchMaterial(seedBlock);
			Material item = Material.matchMaterial(seedItem);
			if ((block == null) || (!block.isBlock())) {
				Tractor.logger.warning("Unknown seed block material: " + seedBlock);
			} else if ((item == null)) {
				Tractor.logger.warning("Unknown seed item material: " + seedItem);
			} else {
				this.seedTypes.put(block, item);
			}
		}
		this.height = this.config.getInt("height", 1);
	}

	private void loadMessages() {
		Message[] arrayOfMessage;
		int j = (arrayOfMessage = Message.values()).length;
		for (int i = 0; i < j; i++) {
			Message msg = arrayOfMessage[i];
			String text = this.config.getString("messages." + msg.name().toLowerCase(), msg.getMessage());
			text = ChatColor.translateAlternateColorCodes('&', text);
			msg.setMessage(text);
		}
	}

	public Map<Material, Material> getSeedTypes() {
		return this.seedTypes;
	}

	public int getHeight() {
		return this.height;
	}
}
