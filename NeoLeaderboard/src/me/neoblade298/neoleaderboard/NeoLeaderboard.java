package me.neoblade298.neoleaderboard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.commands.CommandManager;
import me.neoblade298.neocore.instancing.InstanceType;
import me.neoblade298.neoleaderboard.commands.*;
import me.neoblade298.neoleaderboard.listeners.InstanceListener;
import me.neoblade298.neoleaderboard.listeners.PointsListener;
import me.neoblade298.neoleaderboard.listeners.TownyListener;
import me.neoblade298.neoleaderboard.points.PointsManager;
import net.md_5.bungee.api.ChatColor;

public class NeoLeaderboard extends JavaPlugin {
	private static NeoLeaderboard inst;
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("NeoLeaderboard Enabled");
		inst = this;
		
		if (NeoCore.getInstanceType() != InstanceType.SESSIONS) {
			NeoCore.registerIOComponent(this, new PointsManager());

			PointsManager.initialize();
			initCommands();
			
			Bukkit.getPluginManager().registerEvents(new PointsListener(), this);
			Bukkit.getPluginManager().registerEvents(new TownyListener(), this);
		}
		else if (NeoCore.getInstanceType() == InstanceType.SESSIONS) {
			Bukkit.getPluginManager().registerEvents(new InstanceListener(), this);
		}
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoLeaderboard Disabled");
	    super.onDisable();
	}
	
	private void initCommands() {
		CommandManager mngr = new CommandManager("nations", this);
		mngr.register(new CmdNations());
		mngr.registerCommandList("help");
		
		mngr = new CommandManager("nl", this);
		mngr.register(new CmdNLNation());
		mngr.register(new CmdNLTown());
		mngr.register(new CmdNLBase());
		mngr.registerCommandList("help");

		mngr = new CommandManager("nlc", this);
		mngr.register(new CmdNLCBase());
		mngr.register(new CmdNLCTop());
		mngr.register(new CmdNLCNation());
		mngr.register(new CmdNLCTown());
		mngr.registerCommandList("help");

		mngr = new CommandManager("nladmin", "neoleaderboard.admin", ChatColor.DARK_RED, this);
		mngr.registerCommandList("");
		mngr.register(new CmdNLAAddPlayer());
		mngr.register(new CmdNLAAddNation());
		mngr.register(new CmdNLAFinalize());
		mngr.register(new CmdNLAReset());
	}
	
	public static NeoLeaderboard inst() {
		return inst;
	}
}
