package me.Neoblade298.NeoProfessions.Commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Neoblade298.NeoProfessions.Professions;
import me.Neoblade298.NeoProfessions.Managers.CurrencyManager;
import me.Neoblade298.NeoProfessions.Utilities.Util;

public class EssenceVouchCommand implements CommandExecutor {

	Professions main;

	public EssenceVouchCommand(Professions main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {

		Player p = (Player) sender;
		
		if (p.getGameMode() == GameMode.CREATIVE) {
			Util.sendMessage(sender, "&cYou can't use this command in creative mode!");
			return true;
		}

		if (args.length != 2) {
			sender.sendMessage("§c/evouch [level] [amount]");
		}
		else {
			// Normalize essence level
			int level = Integer.parseInt(args[0]);
			level -= level % 5;
			if (!(level <= 60 && level > 0 && level % 5 == 0)) {
				Util.sendMessage(sender, "&cFailed to vouch essence, invalid level " + level + "!");
				return true;
			}
			int amount = Integer.parseInt(args[1]);
			if (amount <= 0 || amount >= 99999) {
				Util.sendMessage(sender, "&cFailed to vouch essence, invalid amount " + amount + "!");
				return true;
			}
			else if (!CurrencyManager.hasEnough(p, level, amount)) {
				Util.sendMessage(sender, "&cFailed to pay essence, Insufficient essence!");
				return true;
			}
			
			if (CurrencyManager.giveVoucher(p, level, amount)) {
				Util.sendMessage(p,
						"&7You created &e" + amount + " &6Lv " + level + " &7Essence vouchers");
			}
			return true;
		}
		return true;
	}
}