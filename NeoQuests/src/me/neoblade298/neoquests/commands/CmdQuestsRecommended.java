package me.neoblade298.neoquests.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;

import io.lumine.mythic.bukkit.utils.lib.lang3.StringUtils;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.PaginatedList;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neoquests.conditions.ConditionManager;
import me.neoblade298.neoquests.quests.QuestRecommendation;
import me.neoblade298.neoquests.quests.Quester;
import me.neoblade298.neoquests.quests.QuestsManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class CmdQuestsRecommended implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player", false),
			new CommandArgument("page", false)));

	@Override
	public String getDescription() {
		return "Lists recommended quests for you to take";
	}

	@Override
	public String getKey() {
		return "recommended";
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
		run(s, args, false);
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

	public static void run(CommandSender s, String[] args, boolean challenges) {
		Player p = (Player) s;
		
		int page = 0;
		int offset = 0;
		if (args.length > 0 && Bukkit.getPlayer(args[0]) != null) {
			p = Bukkit.getPlayer(args[0]);
			offset++;
		}
		
		if (args.length > offset) {
			if (StringUtils.isNumeric(args[offset])) {
				page = Integer.parseInt(args[offset]) - 1;
			}
			else {
				Util.msg(s, "&cInvalid argument, must be a page number!");
				return;
			}
		}
		
		int level = 1;
		if (SkillAPI.getPlayerData(p) != null && SkillAPI.getPlayerData(p).getMainClass() != null) {
			level = SkillAPI.getPlayerData(p).getMainClass().getLevel();
		}

		ArrayList<QuestRecommendation> recs = challenges ? QuestsManager.getChallenges() : QuestsManager.getRecommendations();
		PaginatedList<QuestRecommendation> pages = new PaginatedList<QuestRecommendation>();
		Quester q = QuestsManager.getQuester(p);
		for (QuestRecommendation rec : recs) {
			int min = rec.getMin(), max = rec.getMax();
			if (min != -1 && min > level) continue;
			if (max != -1 && max < level) continue;
			if (ConditionManager.getBlockingCondition(p, rec.getQuest().getConditions()) != null) continue;
			if (q.hasActiveQuest(rec.getQuest().getKey())) continue;

			pages.add(rec);
		}
		
		if (page < 0 || page > pages.pages() - 1) {
			Util.msg(s, "&cPage out of bounds!");
			return;
		}
		
		if (pages.size() == 0) {
			if (challenges) {
				Util.msg(s, "&7No challenges at this level! Try &c/quests list&7, this lists ANY quest you can take that you haven't finished!");
			}
			else {
				Util.msg(s, "&7No recommendations at this level! Try &c/quests challenges&7!");
			}
			return;
		}
		
		Util.msg(s, "&7Recommended quests for you:");
		for (QuestRecommendation rec : pages.get(page)) {
			ComponentBuilder text = new ComponentBuilder("§7- §6" + rec.getQuest().getDisplay());
			if (rec.getEndpoint() == null) {
				ComponentBuilder take = new ComponentBuilder(" §7§o[Click to Take]")
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quests take " + rec.getQuest().getKey()));
				s.spigot().sendMessage(text.append(take.create()).create());
			}
			else {
				ComponentBuilder nav = new ComponentBuilder(" §7§o[Click for GPS]")
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nav to " + rec.getEndpoint()));
				s.spigot().sendMessage(text.append(nav.create()).create());
			}
		}
		pages.displayFooter(s, page, "/quests recommended " + (page + 2), "/quests recommended " + page);
	}
}
