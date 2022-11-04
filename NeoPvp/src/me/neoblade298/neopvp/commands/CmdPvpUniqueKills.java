package me.neoblade298.neopvp.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neopvp.PvpManager;

public class CmdPvpUniqueKills implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player", false),
			new CommandArgument("page", false)));

	@Override
	public String getDescription() {
		return "Lists unique kills";
	}

	@Override
	public String getKey() {
		return "uniquekills";
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			PvpManager.getAccount((Player) s).displayUniqueKills(s, 0);
		}
		else {
			Player p = null;
			int offset = 0;
			if (Bukkit.getPlayer(args[0]) != null) {
				p = Bukkit.getPlayer(args[0]);
				offset = 1;
			}
			else {
				p = (Player) s;
			}
			
			// If there's still an arg to use
			int page = 0;
			if (args.length > offset) {
				page = Integer.parseInt(args[offset]);
			}
			PvpManager.getAccount(p).displayUniqueKills(s, page);
		}
	}
	
	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
