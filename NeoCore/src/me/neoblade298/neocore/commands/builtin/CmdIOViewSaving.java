package me.neoblade298.neocore.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.io.IOManager;
import me.neoblade298.neocore.util.Util;

public class CmdIOViewSaving implements Subcommand {
	private static final CommandArguments args = new CommandArguments();

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "viewsaving";
	}

	@Override
	public String getDescription() {
		return "Shows list of players who are currently saving cross-server";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Util.msg(s, IOManager.getSavingUsers().toString(), false);
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
