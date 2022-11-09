package me.neoblade298.neopvp.commands;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neopvp.wars.War;
import me.neoblade298.neopvp.wars.WarManager;

public class CmdAWarNew implements Subcommand {

	@Override
	public String getDescription() {
		return "Start creation of a war";
	}

	@Override
	public String getKey() {
		return "new";
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
		WarManager.newWar(s, new War(args[0], Util.translateColors(Util.connectArgs(args, 1))));
		WarManager.displayWarCreation(s);
	}
	
	@Override
	public String getArgOverride() {
		return "[key] [display]";
	}

}
