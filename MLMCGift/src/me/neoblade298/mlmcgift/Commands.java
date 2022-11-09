package me.neoblade298.mlmcgift;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	Main main;

	public Commands(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;

		if (args.length == 1) {
			if (args[0].equals("accept")) {
				main.acceptRequest(player);
			} else if (args[0].equals("deny")) {
				main.denyRequest(player);
				return true;
			} else {
				main.sendRequest(player, args[0]);
			}
			return true;
		} else {
			return false;
		}
	}
}