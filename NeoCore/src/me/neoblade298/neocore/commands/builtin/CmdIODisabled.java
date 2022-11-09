package me.neoblade298.neocore.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.io.IOManager;
import me.neoblade298.neocore.io.IOType;
import me.neoblade298.neocore.util.Util;

public class CmdIODisabled implements Subcommand {
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
		return "disabled";
	}

	@Override
	public String getDescription() {
		return "Shows any disabled IO";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		for (IOType type : IOType.values()) {
			String msg = "&6" + type + "&7:";
			for (String key : IOManager.getDisabledIO().get(type)) {
				msg += " &e" + key;
			}
			Util.msg(s, msg, false);
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
