package me.neoblade298.neosessions.commands.director;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neosessions.directors.DirectorManager;

public class CmdDSessionsResetCD implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player"),
			new CommandArgument("session", false)));

	@Override
	public String getDescription() {
		return "Resets a player's cooldowns";
	}

	@Override
	public String getKey() {
		return "reset";
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			for (String key : DirectorManager.getCooldowns().keySet()) {
				DirectorManager.getCooldowns().get(key).remove(Bukkit.getPlayer(args[0]).getUniqueId());
			}
			Util.msg(s, "Successfully reset all cooldowns for player.");
		}
		else {
			if (DirectorManager.getCooldowns().containsKey(args[1])) {
				Util.msg(s, "&cFailed to reset cooldown, session doesn't exist.");
			}
			else {
				DirectorManager.getCooldowns().get(args[1]).remove(Bukkit.getPlayer(args[0]).getUniqueId());
				Util.msg(s, "Successfully reset cooldown for player.");
			}
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
