package me.neoblade298.neosessions.commands.session;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neosessions.NeoSessions;
import me.neoblade298.neosessions.sessions.SessionManager;

public class CmdSessionsEnd implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("session"));

	@Override
	public String getDescription() {
		return "Ends a session";
	}

	@Override
	public String getKey() {
		return "end";
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
		SessionManager.getSessions().get(args[0]).end();
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
