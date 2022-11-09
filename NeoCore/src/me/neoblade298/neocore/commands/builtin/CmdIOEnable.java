package me.neoblade298.neocore.commands.builtin;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.io.IOManager;
import me.neoblade298.neocore.io.IOType;
import me.neoblade298.neocore.util.Util;

public class CmdIOEnable implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("action", true),
			new CommandArgument("io key", false)));

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
		return "enable";
	}

	@Override
	public String getDescription() {
		return "Enables an IO action: save, preload, load, cleanup, autosave";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		IOType type = IOType.valueOf(args[0].toUpperCase());
		if (args.length == 1) {
			IOManager.enableIO(type);
			Util.msg(s, "Successfully set " + type + " to enabled.");
		}
		else {
			IOManager.enableIO(type, args[1]);
			Util.msg(s, "Successfully set " + type + " to enabled for manager " + args[1] + ".");
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
