package me.neoblade298.neoleaderboard.commands;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.TreeSet;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neoleaderboard.NeoLeaderboard;
import me.neoblade298.neoleaderboard.points.NationEntry;
import me.neoblade298.neoleaderboard.points.PointsManager;
import me.neoblade298.neoleaderboard.points.TownEntry;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.hover.content.Text;

public class CmdNations implements Subcommand {
	private static final CommandArguments args = new CommandArguments();
	private static final Formatter fmt = new Formatter();
	private static final String month;
	
	static {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -1);
		month = fmt.format("%tB", c).toString();
	}

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
	public CommandArguments getArgs() {
		return args;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		String temp = "N/A";
		
		new BukkitRunnable() {
			public void run() {
				TreeSet<NationEntry> sorted = new TreeSet<NationEntry>(PointsManager.getNationEntries());
				Iterator<NationEntry> iter = sorted.descendingIterator();
				
				ComponentBuilder builder = new ComponentBuilder("§c§l» §6§lTop Nation of " + month + ": §e§l§n" + temp + "§c§l «\n")
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click for details: §e/nations previous")))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nations previous"));
				builder.append("§6§l>§8§m--------§c§l» Nation Leaderboard «§8§m--------§6§l<", FormatRetention.NONE);
				int i = 0;
				while (iter.hasNext() && i++ <= 10) {
					NationEntry e = iter.next();
					String name = e.getNation().getName();
					builder.append("\n§6§l" + i + ". §e" + name + " §7- §f" + PointsManager.formatPoints(e.getTotalPoints()), FormatRetention.NONE)
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(buildNationHover(e))))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nl nation " + name));
				}
				s.spigot().sendMessage(builder.create());
			}
		}.runTaskAsynchronously(NeoLeaderboard.inst());
	}
	
	private String buildNationHover(NationEntry e) {
		String hovertext = "Click for details: §e/nl nation " + e.getNation().getName() + "\n";
		hovertext += "§6Top town contributors:";
		
		TreeSet<TownEntry> townOrder = e.getTopTowns();
		Iterator<TownEntry> iter = townOrder.descendingIterator();
		for (int i = 1; i <= 10 && iter.hasNext(); i++) {
			TownEntry te = iter.next();
			// double effective = PointsManager.calculateEffectivePoints(e, te.getTotalPoints());
			hovertext += "\n§6§l" + i + ". §e" + te.getTown().getName() + " §7- §f" + PointsManager.formatPoints(te.getTotalPoints());
		}
		return hovertext;
	}
}
