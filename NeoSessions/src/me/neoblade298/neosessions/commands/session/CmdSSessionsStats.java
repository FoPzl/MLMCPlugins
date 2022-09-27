package me.neoblade298.neosessions.commands.session;

import org.bukkit.command.CommandSender;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neosessions.sessions.Session;
import me.neoblade298.neosessions.sessions.SessionManager;

public class CmdSSessionsStats implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("session"),
			new CommandArgument("key", false));

	@Override
	public String getDescription() {
		return "Shows the stats for a session";
	}

	@Override
	public String getKey() {
		return "stats";
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
		Session sess = SessionManager.getSessions().get(args[0]);
		if (args.length == 2) {
			sess.getStats(args[1]).display(s);
		}
		else {
			sess.getStats(args[0]).display(s);
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
