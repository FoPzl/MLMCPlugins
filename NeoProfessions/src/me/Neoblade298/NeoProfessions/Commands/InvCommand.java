package me.Neoblade298.NeoProfessions.Commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.Neoblade298.NeoProfessions.Professions;
import me.Neoblade298.NeoProfessions.Inventories.StorageSelectInventory;
import me.Neoblade298.NeoProfessions.Utilities.Util;


public class InvCommand implements CommandExecutor {
	Professions main;
	
	public InvCommand(Professions main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length == 0 && sender instanceof Player) {
			Player p = (Player) sender;
			if (p.getGameMode() == GameMode.CREATIVE) {
				Util.sendMessage(sender, "&cYou can't use this command in creative mode!");
				return true;
			}
			new StorageSelectInventory((Player) sender);
			return true;
		}
		return false;
	}
}