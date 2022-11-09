package me.neoblade298.neopvp.commands;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neopvp.wars.War;
import me.neoblade298.neopvp.wars.WarManager;

public class CmdWarBase implements Subcommand {
	private static final CommandArguments args = new CommandArguments();

	@Override
	public String getDescription() {
		return "Views current war status";
	}

	@Override
	public String getKey() {
		return "";
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
		if (WarManager.getOngoingWars().size() > 0) {
			Util.msg(s, "&7Ongoing Wars:", false);
			for (War war : WarManager.getOngoingWars().values()) {
				war.display(s);
			}
		}
		else {
			Util.msg(s, "&7There are currently no ongoing wars.", false);
		}

		if (WarManager.getWars().size() > 0) {
			Util.msg(s, "&7Scheduled Wars:", false);
			for (War war : WarManager.getWars().values()) {
				war.displayCreator(s);
			}
		}
		else {
			Util.msg(s, "&7There are currently no scheduled wars.", false);
		}
	}
	
	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
