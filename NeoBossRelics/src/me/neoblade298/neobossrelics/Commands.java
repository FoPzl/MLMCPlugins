package me.neoblade298.neobossrelics;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commands implements CommandExecutor{
	
	NeoBossRelics main;
	
	public Commands(NeoBossRelics main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length == 0 && sender.hasPermission("mycommand.staff")) {
			sender.sendMessage("§c/relic debug - Toggle debug messages");
			sender.sendMessage("§c/relic check [player] - Checks a player's set");
			sender.sendMessage("§c/relic reload - Reloads config");
		}
		
		else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			main.loadConfig();
			sender.sendMessage("§4[§c§lMLMC§4] §7Successfully reloaded config!");
		}
		
		else if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
			main.debug = !main.debug;
			if (main.debug) sender.sendMessage("§4[§c§lMLMC§4] §7Toggled on debug mode!"); 
			if (!main.debug) sender.sendMessage("§4[§c§lMLMC§4] §7Toggled off debug mode!"); 
		}
		
		else if (args.length == 1 && args[0].equalsIgnoreCase("check") && sender instanceof Player) {
			Player p = (Player) sender;
			UUID uuid = p.getUniqueId();
			if (main.playersets.containsKey(uuid)) {
				String set = main.playersets.get(uuid).getSet().getName();
				int num = main.playersets.get(uuid).getNumRelics();
				p.sendMessage("§4[§c§lMLMC§4] §e" + p.getName() + " §7has set §e" + set + " §7with §e" + num + " §7relics."); 
			}
			else {
				p.sendMessage("§4[§c§lMLMC§4] §e" + p.getName() + " §7doesn't have a set active."); 
			}
		}
		
		else if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
			Player p = Bukkit.getPlayer(args[1]);
			UUID uuid = p.getUniqueId();
			if (main.playersets.containsKey(uuid)) {
				String set = main.playersets.get(uuid).getSet().getName();
				int num = main.playersets.get(uuid).getNumRelics();
				sender.sendMessage("§4[§c§lMLMC§4] §e" + p.getName() + " §7has set §e" + set + " §7with §e" + num + " §7relics."); 
			}
			else {
				sender.sendMessage("§4[§c§lMLMC§4] §e" + p.getName() + " §7doesn't have a set active."); 
			}
		}
		return true;
	}
}