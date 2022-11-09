package me.neoblade298.townychatbridge;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.instancing.InstanceType;
import me.neoblade298.townychatbridge.other.InstanceListener;
import me.neoblade298.townychatbridge.towny.TownyListener;

public class TownyChatBridge extends JavaPlugin implements Listener {
	private static TownyChatBridge inst;
	public static final int CHAT_TIMEOUT = 10000; // How long in ms before a cross-server chat expires
	
	public void onEnable() {
		inst = this;
		
		Bukkit.getServer().getLogger().info("TownyChatBridge Enabled");
		Bukkit.getPluginManager().registerEvents(this, this);
		// initCommands();
		
		if (NeoCore.getInstanceType() == InstanceType.TOWNY) {
			Bukkit.getPluginManager().registerEvents(new TownyListener(), this);
		}
		else {
			Bukkit.getPluginManager().registerEvents(new InstanceListener(), this);
		}
		
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("TownyChatBridge Disabled");
	    super.onDisable();
	}
	
	/*
	private void initCommands() {
		CommandManager mngr = new CommandManager("disenchant", this);
	}
	*/
	
	public static TownyChatBridge inst() {
		return inst;
	}
}
