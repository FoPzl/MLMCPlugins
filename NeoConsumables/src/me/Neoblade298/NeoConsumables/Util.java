package me.Neoblade298.NeoConsumables;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bungee.BungeeAPI;

public class Util {
	public static void serverCommand(String cmd) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
	}
	
	public static void sendMessage(CommandSender s, String msg) {
		s.sendMessage(("&4[&c&lMLMC&4] " + msg).replaceAll("&", "§"));
	}
	
	public static void serverBroadcast(String msg) {
		BungeeAPI.broadcast(msg);
	}
}
