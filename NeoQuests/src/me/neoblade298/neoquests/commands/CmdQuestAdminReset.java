package me.neoblade298.neoquests.commands;

import java.sql.Statement;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.lumine.mythic.bukkit.utils.lib.lang3.StringUtils;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neoquests.conversations.ConversationManager;
import me.neoblade298.neoquests.quests.Quester;
import me.neoblade298.neoquests.quests.QuestsManager;

public class CmdQuestAdminReset implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player", false),
			new CommandArgument("account #", false)));

	@Override
	public String getDescription() {
		return "Resets a player's completed quests";
	}

	@Override
	public String getKey() {
		return "reset";
	}

	@Override
	public String getPermission() {
		return "neoquests.admin";
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = null;
		int offset = 0;
		if (args.length == 0) {
			p = (Player) s;
		}
		else if (args.length == 1 && StringUtils.isNumeric(args[0])) {
			p = (Player) s;
		}
		else if (Bukkit.getPlayer(args[0]) != null) {
			p = Bukkit.getPlayer(args[0]);
			offset = 1;
		}
		else {
			Util.msg(s, "&cSomething's wrong with the command arguments!");
		}
		
		// Reset entire player
		Statement stmt = NeoCore.getStatement();
		try {
			if (args.length == offset) {
				stmt.execute("DELETE FROM quests_completed WHERE uuid = '" + p.getUniqueId() + "';");
				stmt.execute("DELETE FROM quests_accounts WHERE uuid = '" + p.getUniqueId() + "';");
				stmt.execute("DELETE FROM quests_questlines WHERE uuid = '" + p.getUniqueId() + "';");
				stmt.execute("DELETE FROM quests_quests WHERE uuid = '" + p.getUniqueId() + "';");
				for (Quester quester : QuestsManager.getAllAccounts(p)) {
					quester.reset();
				}
			}
			else {
				if (!StringUtils.isNumeric(args[offset])) {
					Util.msg(s, "&cAccount must be a number!");
				}
				int acct = Integer.parseInt(args[offset]);
				Quester quester = QuestsManager.getQuester(p, acct);
				if (quester == null) {
					Util.msg(s, "&cThis account doesn't exist!");
				}
				stmt.execute("DELETE FROM quests_completed WHERE uuid = '" + p.getUniqueId() + "' AND account = " + args[1] + ";");
				stmt.execute("DELETE FROM quests_accounts WHERE uuid = '" + p.getUniqueId() + "' AND account = " + args[1] + ";");
				stmt.execute("DELETE FROM quests_questlines WHERE uuid = '" + p.getUniqueId() + "' AND account = " + args[1] + ";");
				stmt.execute("DELETE FROM quests_quests WHERE uuid = '" + p.getUniqueId() + "' AND account = " + args[1] + ";");
				quester.reset();
			}
			ConversationManager.endConversation(p, false);
			Util.msg(s, "&7Successfully reset player &6" + p.getName() + ".");
		}
		catch (Exception e) {
			Util.msg(s, "&cCommand failed! Stack trace in console.");
			e.printStackTrace();
		} 
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}