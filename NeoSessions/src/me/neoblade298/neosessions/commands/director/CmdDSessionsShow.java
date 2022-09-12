package me.neoblade298.neosessions.commands.director;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neosessions.sessions.SessionManager;

public class CmdDSessionsShow implements Subcommand {
	private static final CommandArguments args = new CommandArguments();

	@Override
	public String getDescription() {
		return "Leave current session";
	}

	@Override
	public String getKey() {
		return "leave";
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
		SessionManager.returnPlayer((Player) s);
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
