package me.neoblade298.neoleaderboard.commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neoleaderboard.NeoLeaderboard;
import me.neoblade298.neoleaderboard.points.PlayerEntry;
import me.neoblade298.neoleaderboard.points.PlayerPointType;
import me.neoblade298.neoleaderboard.points.PointsManager;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;

public class CmdNLBase implements Subcommand {
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
	public CommandArguments getArgs() {
		return args;
	}

	@SuppressWarnings("deprecation") // Need to get uuid of offline player
	@Override
	public void run(CommandSender s, String[] args) {
		// Async in case it has to load offline player data
		new BukkitRunnable() {
			public void run() {
				if (args.length == 0 && !(s instanceof Player)) {
					Util.msg(s, "&cYou can't use this command on yourself as console!");
					return;
				}
				
				OfflinePlayer p;
				if (args.length == 0 && s instanceof Player) {
					p = (Player) s;
				}
				else {
					p = Bukkit.getPlayer(args[0]);
				}
				
				PlayerEntry pe = null;
				if (p != null) {
					 pe = PointsManager.getPlayerEntry(p.getUniqueId());
				}
				else {
					try {
						p = Bukkit.getOfflinePlayer(args[0]);
						pe = PointsManager.loadPlayerEntry(p.getUniqueId(), NeoCore.getStatement());
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				
				if (pe == null) {
					Util.msg(s, "&cThis player hasn't contributed anything yet!");
					return;
				}
				Resident r = TownyAPI.getInstance().getResident(pe.getUuid());
				if (r.getNationOrNull() == null) {
					Util.msg(s, "&cThis player isn't in a nation!");
					return;
				}
				HashMap<PlayerPointType, Double> cpoints = pe.getContributedPoints();
				ComponentBuilder builder = new ComponentBuilder("§6§l>§8§m--------§c§l» §6Player Contributions: §e" + p.getName() + " §c§l«§8§m--------§6§l<");
				builder.append("\n§6TOTAL: §f" + PointsManager.formatPoints(pe.getContributed()), FormatRetention.NONE);
				for (Entry<PlayerPointType, Double> e : cpoints.entrySet()) {
					// double effective = PointsManager.calculateEffectivePoints(ne, e.getValue());
					builder.append("\n§6" + e.getKey().getDisplay() + ": §f" + PointsManager.formatPoints(e.getValue()), FormatRetention.NONE);
				}
				/*
				builder.append("\n§6§l>§8§m--------§c§l» §6Player Totals: §e" + p.getName() + " §c§l«§8§m--------§6§l<");
				for (Entry<PlayerPointType, Double> e : totalPoints.entrySet()) {
					// double effective = PointsManager.calculateEffectivePoints(ne, e.getValue());
					builder.append("\n§6" + e.getKey().getDisplay() + ": §f" + PointsManager.formatPoints(e.getValue()), FormatRetention.NONE);
				}
				*/
				s.spigot().sendMessage(builder.create());
			}
		}.runTaskAsynchronously(NeoLeaderboard.inst());
	}
}
