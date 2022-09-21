package me.neoblade298.neoleaderboard.commands;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neoleaderboard.NeoLeaderboard;
import me.neoblade298.neoleaderboard.points.NationEntry;
import me.neoblade298.neoleaderboard.points.NationPointType;
import me.neoblade298.neoleaderboard.points.PlayerPointType;
import me.neoblade298.neoleaderboard.points.PointType;
import me.neoblade298.neoleaderboard.points.PointsManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.hover.content.Text;

public class CmdNLCTop implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("category"));

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getKey() {
		return "top";
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
	public CommandArguments getArgs() {
		return args;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		PointType type;
		if (PlayerPointType.valueOf(args[0].toUpperCase()) != null) {
			type = PlayerPointType.valueOf(args[0].toUpperCase());
		}
		else {
			type = NationPointType.valueOf(args[0].toUpperCase());
		}
		
		if (type == null) {
			Util.msg(s, "&cInvalid category");
			return;
		}
		
		new BukkitRunnable() {
			public void run() {
				Comparator<NationEntry> comp = new Comparator<NationEntry>() {
					@Override
					public int compare(NationEntry n1, NationEntry n2) {
						if (n1.getPoints(type) > n2.getPoints(type)) {
							return 1;
						}
						else if (n1.getPoints(type) < n2.getPoints(type)) {
							return -1;
						}
						else {
							return n2.getNation().getName().compareTo(n1.getNation().getName());
						}
					}
				};
				TreeSet<NationEntry> sorted = new TreeSet<NationEntry>(comp);
				sorted.addAll(PointsManager.getNationEntries());
				Iterator<NationEntry> iter = sorted.descendingIterator();
				
				ComponentBuilder builder = new ComponentBuilder("§c§lTop Nations: §e" + type.getDisplay());
				int i = 0;
				while (iter.hasNext() && i++ < 10) {
					NationEntry e = iter.next();
					String name = e.getNation().getName();
					// double effective = PointsManager.calculateEffectivePoints(e, e.getPoints(type));
					builder.append("\n§6§l" + i + ". §e" + name + " §7- §f" + PointsManager.formatPoints(e.getPoints(type)), FormatRetention.NONE);
					if (type instanceof PlayerPointType) {
						builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/nlc nation " + name + " " + type)))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nlc nation " + name + " " + type));
					}
				}
				s.spigot().sendMessage(builder.create());
			}
		}.runTaskAsynchronously(NeoLeaderboard.inst());
	}
}
