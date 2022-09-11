package me.neoblade298.neosessions.commands.session;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neosessions.sessions.SessionManager;

public class CmdSessionsAdd implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player", false),
			new CommandArgument("session")));

	@Override
	public String getDescription() {
		return "Adds a player to an existing session";
	}

	@Override
	public String getKey() {
		return "add";
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
		if (args.length == 2) {
			SessionManager.addToSession(Bukkit.getPlayer(args[0]), args[1]);
		}
		else {
			SessionManager.addToSession((Player) s, args[0]);
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
