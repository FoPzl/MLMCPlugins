package me.neoblade298.neoinstruments;

import org.bukkit.Bukkit;
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
		
		
		if(sender instanceof Player)
		{
			if (!sender.hasPermission("music.use")) {
				return false;
			}
			
			Player p = (Player) sender;

			if (args.length > 0) {

				switch (args[0]) {
				case "edit":
					main.editBook(p);
					break;
				case "tempo":
					if ( args.length > 1) {
						main.setTempo(p, Integer.parseInt(args[1]));
					}
					break;
				case "sync":
					if (args.length > 1) {
						main.askSync(p, Bukkit.getPlayer(args[1]));
					}
					break;
				case "unsync":
					main.unsync(p);
					break;
				case "confirm":
					if (args.length > 1) {
						main.confirmSync(p, Bukkit.getPlayer(args[1]));
					}
					break;
				case "deny":
					if (args.length > 1) {
						main.denySync(p, Bukkit.getPlayer(args[1]));
					}
					break;
				case "give":
					if (p.isOp() || p.hasPermission("*")) {
						if (args.length > 1 && args[1] != null) {
							main.getBook(Bukkit.getPlayer(args[1]));
						}
					}
					break;
				default:
					return false;
				}

				return true;
			}
			else {
				p.sendMessage("§c/music edit - §7Edit a signed sheet music owned by you");
				p.sendMessage("§c/music tempo [2-20] - §7Sets tick delay between notes, recommended 4");
				p.sendMessage("§c/music sync [player] - §7Requests syncing sheet music with a player");
				p.sendMessage("§c/music unsync [player] - §7Unsyncs");
				return true;
			}
		}
		else
		{
			if(args.length > 0 && args[0].equalsIgnoreCase("give"))
			{
				if (args.length > 1 && args[1] != null) {
					main.getBook(Bukkit.getPlayer(args[1]));
				}
			}
			return true;
		}
	}
}