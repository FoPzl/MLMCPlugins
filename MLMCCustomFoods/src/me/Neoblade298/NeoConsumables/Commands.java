package me.Neoblade298.NeoConsumables;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.Neoblade298.NeoConsumables.objects.GeneratableConsumable;


public class Commands implements CommandExecutor, TabCompleter {
	Set<String> generatableConsumables;
	
	Consumables main;
	
	public Commands(Consumables main, Set<String> generatableConsumables) {
		this.main = main;
		this.generatableConsumables = generatableConsumables;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (sender.hasPermission("mycommand.staff")) {
			if (args.length == 0) {
				sender.sendMessage("§7Permission: mycommand.staff");
				sender.sendMessage("§c/cons get [food] {amount}");
				sender.sendMessage("§c/cons give [player] boss");
				sender.sendMessage("§c/cons debug");
				sender.sendMessage("§c/cons reload");
			}
			// /cons give [player] boss/[key] [amount]
			else if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("get")) {
				Player p = null;
				int offset = 0;
				int amt = 1;
				if (Bukkit.getPlayer(args[1]) != null) {
					p = Bukkit.getPlayer(args[1]);
					offset++;
				}
				else {
					p = (Player) sender;
				}
				
				if (args[1 + offset].equalsIgnoreCase("boss")) {
					p.getInventory().addItem(Items.getBossChestToken(p, System.currentTimeMillis()));
					sender.sendMessage("§4[§c§lMLMC§4] §7Successfully gave boss token to §e" + p.getName());
					return true;
				}
				else {
					GeneratableConsumable cons = (GeneratableConsumable) Consumables.getConsumable(args[1 + offset]);
					
					if (args.length > 2 + offset) {
						amt = Integer.parseInt(args[2 + offset]);
					}
					
					if (cons != null) {
						p.getInventory().addItem(cons.getItem(amt));
						sender.sendMessage("§4[§c§lMLMC§4] §7Successfully gave " + cons.getDisplay() + "§7 to §e" + p.getName());
						return true;
					}
					else {
						sender.sendMessage("§4[§c§lMLMC§4] §cInvalid key!");
						return true;
					}
				}
			}
			else if (args[0].equalsIgnoreCase("reload")) {
				sender.sendMessage("§4[§c§lMLMC§4] §7Reloaded consumables");
				main.reload();
				return true;
			}
			else if (args[0].equalsIgnoreCase("debug")) {
				Consumables.debug = !Consumables.debug;
				sender.sendMessage("§4[§c§lMLMC§4] §7Set debug to " + Consumables.debug);
				return true;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (args[0].equalsIgnoreCase("get") && args.length == 2) {
			return generatableConsumables.stream().filter(food -> food.startsWith(args[1])).collect(Collectors.toList());
		}
		else if (args[0].equalsIgnoreCase("give") && args.length == 3) {
			return generatableConsumables.stream().filter(food -> food.startsWith(args[2])).collect(Collectors.toList());
		}
		return null;
	}
}