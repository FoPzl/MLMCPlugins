package me.neoblade298.neoquests.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neoquests.quests.Quester;
import me.neoblade298.neoquests.quests.QuestsManager;

public class CmdQuestBase implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("player", false));

	@Override
	public String getDescription() {
		return null;
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
		Player p;
		if (args.length == 0) {
			p = (Player) s;
		}
		else {
			p = Bukkit.getPlayer(args[0]);
			if (p == null) {
				Util.msg(s, "&cThis player is not online!");
				return;
			}
		}
		
		Quester q = QuestsManager.getQuester(p);
		if (q == null) {
			Util.msg(s, "&cThis account hasn't loaded in yet! Try again in a few seconds.");
			return;
		}
		q.displayQuests(s);
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
