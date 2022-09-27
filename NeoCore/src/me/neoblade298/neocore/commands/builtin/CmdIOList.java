package me.neoblade298.neocore.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.io.IOComponent;
import me.neoblade298.neocore.io.IOManager;
import me.neoblade298.neocore.util.Util;

public class CmdIOList implements Subcommand {
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
		return "list";
	}

	@Override
	public String getDescription() {
		return "Lists IO Components by order of priority";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		for (IOComponent io : IOManager.getComponents()) {
			Util.msg(s, "&7- &6" + io.getKey() + " (&e" + io.getPriority() + "&6)", false);
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
