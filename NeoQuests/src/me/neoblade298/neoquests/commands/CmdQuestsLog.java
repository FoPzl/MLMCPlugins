package me.neoblade298.neoquests.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.lumine.mythic.bukkit.utils.lib.lang3.StringUtils;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.PaginatedList;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neoquests.quests.CompletedQuest;
import me.neoblade298.neoquests.quests.Quester;
import me.neoblade298.neoquests.quests.QuestsManager;

public class CmdQuestsLog implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player", false),
			new CommandArgument("page", false)));

	@Override
	public String getDescription() {
		return "Lists all quests you've completed";
	}

	@Override
	public String getKey() {
		return "log";
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = (Player) s;
		int offset = 0;
		if (args.length > 0 && Bukkit.getPlayer(args[0]) != null) {
			p = Bukkit.getPlayer(args[0]);
			offset++;
		}
		
		Quester quester = QuestsManager.initializeOrGetQuester(p);
		if (quester.getCompletedQuests().size() == 0) {
			Util.msg(s, "&cYou don't yet have any completed quests!");
			return;
		}
		
		if (args.length > offset && !StringUtils.isNumeric(args[offset])) {
			Util.msg(s, "&cInvalid argument! Must be a page number.");
			return;
		}
		
		PaginatedList<CompletedQuest> list = new PaginatedList<CompletedQuest>(quester.getCompletedQuests());
		int page = args.length > offset ? Integer.parseInt(args[offset]) - 1 : 0;
		if (page < 0 || page >= list.pages()) {
			Util.msg(s, "&cInvalid page number! Max page is " + list.pages());
			return;
		}
		
		Util.msg(s, "&6-[Quest Log]-", false);
		for (CompletedQuest cq : list.get(page)) {
			String msg = "&7- ";
			msg += cq.isSuccess() ? "&a" : "&c";
			msg += cq.getQuest().getDisplay();
			Util.msg(s, msg, false);
		}
		String nextCmd = "/quests log " + p.getName() + " "+ (page + 2);
		String prevCmd = "/quests log " + p.getName() + " " + page;
		list.displayFooter(s, page, nextCmd, prevCmd);
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
