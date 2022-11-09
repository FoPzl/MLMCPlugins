package com.gmail.holubvojtech.tractor.commands;

import com.gmail.holubvojtech.tractor.Message;
import com.gmail.holubvojtech.tractor.PlayerData;
import com.gmail.holubvojtech.tractor.Tractor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TractorCmd implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		PlayerData data = Tractor.getPlayerData(player);
		if (args.length == 0) {
			if (data.tractorEnabled()) {
				data.setSize(0);
				Message.TRACTOR_DISABLED.send(player);
			} else {
				Message.CMD_USAGE.send(player);
			}
			return true;
		}
		int zSize = 0;
		int xSize = 0;
		try {
			xSize = zSize = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			Message.CMD_USAGE.send(player);
			return true;
		}
		if (args.length > 1) {
			try {
				zSize = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				Message.CMD_USAGE.send(player);
				return true;
			}
		}
		if ((xSize <= 0) || (zSize <= 0)) {
			data.setSize(0);
			Message.TRACTOR_DISABLED.send(player);
			return true;
		}
		String permission = "tractor.use." + xSize;
		if (xSize != zSize) {
			permission = permission + "." + zSize;
		}
		if (player.hasPermission(permission)) {
			data.setXSize(xSize);
			data.setZSize(zSize);
			Message.TRACTOR_ENABLED.send(player, new Object[] { Integer.valueOf(xSize), Integer.valueOf(zSize) });
		} else {
			Message.NO_PERMISSION.send(player);
		}
		return true;
	}
}
