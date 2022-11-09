package me.neoblade298.base;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.neoblade298.neocore.commands.CommandManager;

public class Main extends JavaPlugin {
	private static Main inst;
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("NeoPPRs Enabled");
		// Bukkit.getPluginManager().registerEvents(this, this);
		initCommands();
		
		inst = this;
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoPPRs Disabled");
	    super.onDisable();
	}
	
	private void initCommands() {
		CommandManager mngr = new CommandManager("disenchant", this);
	}
	
	public static Main inst() {
		return inst;
	}
}
