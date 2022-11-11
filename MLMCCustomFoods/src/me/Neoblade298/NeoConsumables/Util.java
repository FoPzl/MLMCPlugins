package me.Neoblade298.NeoConsumables;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Util {
	public static void serverCommand(String cmd) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
	}
	
	public static void sendMessage(CommandSender s, String msg) {
		s.sendMessage(("&4[&c&lMLMC&4] " + msg).replaceAll("&", "§"));
	}
	
	public static void serverBroadcast(String msg) {
		serverCommand("sync console all broadcast " + msg);
	}
}
