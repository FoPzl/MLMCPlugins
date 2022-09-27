package me.neoblade298.neocore.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.io.IOManager;
import me.neoblade298.neocore.util.Util;

public class CmdIORemoveSaving implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("player", false));

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
		return "removesaving";
	}

	@Override
	public String getDescription() {
		return "Removes a player or everyone from isSaving list";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 1) {
			IOManager.getSavingUsers().remove(Bukkit.getPlayer(args[0]).getUniqueId());
			Util.msg(s, "Cleared user from saving list!");
		}
		else {
			IOManager.getSavingUsers().clear();
			Util.msg(s, "Cleared saving list!");
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
