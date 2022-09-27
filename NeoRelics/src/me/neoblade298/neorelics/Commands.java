package me.neoblade298.neorelics;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Commands implements CommandExecutor, TabCompleter {
	
	NeoRelics main;
	
	public Commands(NeoRelics main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length == 0 && sender.hasPermission("mycommand.staff")) {
			sender.sendMessage("§c/relic debug - Toggle debug messages");
			sender.sendMessage("§c/relic get/give {player} [relic]- Gives a relic");
			sender.sendMessage("§c/relic check [player] - Checks a player's set");
			sender.sendMessage("§c/relic reload - Reloads config");
		}
		
		else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			NeoRelics.reload();
			sender.sendMessage("§4[§c§lMLMC§4] §7Successfully reloaded config!");
		}
		
		else if (args.length >= 2 && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("give"))) {
			int offset = 0;
			Player p;
			if (args.length == 3) {
				p = Bukkit.getPlayer(args[1]);
				offset++;
			}
			else {
				p = (Player) sender;
			}
			ItemStack relic = NeoRelics.getRelics().get(args[1 + offset]).getItem();
			Bukkit.getLogger().info("[NeoRelics] Gave relic " + args[1 + offset] + " to player " + p.getName());
			HashMap<Integer, ItemStack> failed = p.getInventory().addItem(relic);
			if (!failed.isEmpty()) {
				for (Entry<Integer, ItemStack> e : failed.entrySet()) {
					p.getWorld().dropItem(p.getLocation(), e.getValue());
				}
			}
		}
		
		else if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
			NeoRelics.debug = !NeoRelics.debug;
			if (NeoRelics.debug) sender.sendMessage("§4[§c§lMLMC§4] §7Toggled on debug mode!"); 
			if (!NeoRelics.debug) sender.sendMessage("§4[§c§lMLMC§4] §7Toggled off debug mode!"); 
		}
		
		else if (args.length == 1 && args[0].equalsIgnoreCase("check") && sender instanceof Player) {
			Player p = (Player) sender;
			UUID uuid = p.getUniqueId();
			if (NeoRelics.playersets.containsKey(uuid)) {
				String set = NeoRelics.playersets.get(uuid).getSet().getKey();
				int num = NeoRelics.playersets.get(uuid).getNumRelics();
				p.sendMessage("§4[§c§lMLMC§4] §e" + p.getName() + " §7has set §e" + set + " §7with §e" + num + " §7relics."); 
			}
			else {
				p.sendMessage("§4[§c§lMLMC§4] §e" + p.getName() + " §7doesn't have a set active."); 
			}
		}
		
		else if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
			Player p = Bukkit.getPlayer(args[1]);
			UUID uuid = p.getUniqueId();
			if (NeoRelics.playersets.containsKey(uuid)) {
				String set = NeoRelics.playersets.get(uuid).getSet().getKey();
				int num = NeoRelics.playersets.get(uuid).getNumRelics();
				sender.sendMessage("§4[§c§lMLMC§4] §e" + p.getName() + " §7has set §e" + set + " §7with §e" + num + " §7relics."); 
			}
			else {
				sender.sendMessage("§4[§c§lMLMC§4] §e" + p.getName() + " §7doesn't have a set active."); 
			}
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (args[0].equalsIgnoreCase("get") && args.length == 2) {
			return NeoRelics.getRelics().keySet().stream().filter(relic -> relic.startsWith(args[1])).collect(Collectors.toList());
		}
		else if (args[0].equalsIgnoreCase("give") && args.length == 3) {
			return NeoRelics.getRelics().keySet().stream().filter(relic -> relic.startsWith(args[2])).collect(Collectors.toList());
		}
		return null;
	}
}