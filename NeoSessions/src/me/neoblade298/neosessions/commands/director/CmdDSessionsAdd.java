package me.neoblade298.neosessions.commands.director;

import java.sql.SQLException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neosessions.NeoSessions;
import me.neoblade298.neosessions.directors.DirectorManager;
import me.neoblade298.neosessions.sessions.SessionInfo;

public class CmdDSessionsAdd implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player", false),
			new CommandArgument("session"), new CommandArgument("session host")));

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
		int offset = 0;
		Player p;
		if (args.length == 3) {
			p = Bukkit.getPlayer(args[0]);
			offset = 1;
		}
		else {
			p = (Player) s;
		}
		try {
			SessionInfo si = NeoSessions.getSessionInfo().get(args[offset]);
			DirectorManager.sendToSessionHost(p, args[offset + 1], si, NeoCore.getStatement(), false);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
