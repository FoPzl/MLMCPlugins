package me.neoblade298.neocore.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.io.IOManager;
import me.neoblade298.neocore.util.Util;

public class CmdIODebug implements Subcommand {
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
		return "debug";
	}

	@Override
	public String getDescription() {
		return "Toggles debug mode to view IO benchmarks";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (IOManager.toggleDebug()) {
			Util.msg(s, "&7Successfully enabled io debug mode!");
		}
		else {
			Util.msg(s, "&7Successfully disabled io debug mode!");
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
