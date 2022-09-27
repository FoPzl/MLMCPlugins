package me.neoblade298.neopvp.commands;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neopvp.wars.WarManager;

public class CmdAWarCreate implements Subcommand {
	private static final CommandArguments args = new CommandArguments();

	@Override
	public String getDescription() {
		return "Completes war creation and schedules it";
	}

	@Override
	public String getKey() {
		return "create";
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
		if (WarManager.completeWarCreation(s)) {
			Util.msg(s, "Successfully completed war creation");
		}
	}
	
	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
