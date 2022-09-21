package me.neoblade298.neoleaderboard.commands;

import java.util.Iterator;
import java.util.TreeSet;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;

import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neoleaderboard.NeoLeaderboard;
import me.neoblade298.neoleaderboard.points.NationEntry;
import me.neoblade298.neoleaderboard.points.PlayerEntry;
import me.neoblade298.neoleaderboard.points.PointsManager;
import me.neoblade298.neoleaderboard.points.TownEntry;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.hover.content.Text;

public class CmdNLNation implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("nation"));

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getKey() {
		return "nation";
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
		Nation n = TownyUniverse.getInstance().getNation(args[0]);
		if (n == null) {
			Util.msg(s, "&cThis nation doesn't exist!");
			return;
		}

		new BukkitRunnable() {
			public void run() {
				NationEntry ne = PointsManager.getNationEntry(n.getUUID());
				Iterator<TownEntry> iter = ne.getTopTowns().descendingIterator();
				
				ComponentBuilder builder = new ComponentBuilder("§6§l>§8§m--------§c§l» §6Point Contribution: §e" + n.getName() + " §c§l«§8§m--------§6§l<");
				int i = 0;
				while (iter.hasNext() && i++ < 10) {
					TownEntry e = iter.next();
					String name = e.getTown().getName();
					// double effective = PointsManager.calculateEffectivePoints(ne, e.getTotalPoints());
					builder.append("\n§6§l" + i + ". §e" + name + " §7- §f" + PointsManager.formatPoints(e.getTotalPoints()), FormatRetention.NONE)
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(buildTownHover(e))))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nl town " + name));
				}
				s.spigot().sendMessage(builder.create());
			}
		}.runTaskAsynchronously(NeoLeaderboard.inst());
	}
	
	private String buildTownHover(TownEntry e) {
		String hovertext = "Click for details: §e/nl town " + e.getTown().getName() + "\n";
		hovertext += "§6Top town contributors:";
		
		TreeSet<PlayerEntry> playerOrder = e.getTopPlayers();
		Iterator<PlayerEntry> iter = playerOrder.descendingIterator();
		for (int i = 1; i <= 10 && iter.hasNext(); i++) {
			PlayerEntry pe = iter.next();
			// double effective = PointsManager.calculateEffectivePoints(PointsManager.getNationEntry(e.getNation().getUUID()), pe.getContributed());
			hovertext += "\n§6§l" + i + ". §e" + pe.getDisplay() + " §7- §f" + PointsManager.formatPoints(pe.getContributed());
		}
		return hovertext;
	}
}
