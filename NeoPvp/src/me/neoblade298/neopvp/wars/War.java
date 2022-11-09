package me.neoblade298.neopvp.wars;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

import me.neoblade298.neocore.bungee.BungeeAPI;
import me.neoblade298.neocore.scheduler.SchedulerAPI;
import me.neoblade298.neocore.scheduler.SchedulerAPI.CoreRunnable;
import me.neoblade298.neocore.util.Util;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class War {
	private String key, display;
	private int maxPlayers;
	private Calendar date = Calendar.getInstance();
	private long endTime;
	private WarTeam[] teams = new WarTeam[2];
	private World w;
	private boolean isOngoing = false;
	private CoreRunnable endTask;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd hh:mm a z");
	
	private static final int DEFAULT_WAR_HOUR = 14;
	
	public War(String key, String display) {
		this.key = key;
		this.display = display;
	}
	
	public War(ResultSet war, ResultSet teams) {
		try {
			war.next();
			this.key = war.getString(1);
			this.display = war.getString(2);
			this.maxPlayers = war.getInt(3);
			this.date.setTimeInMillis(war.getLong(4));
			
			teams.next();
			this.teams[0] = new WarTeam(this.key, teams);
			teams.next();
			this.teams[1] = new WarTeam(this.key, teams);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void schedule() {
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				start();
			}
		};
		SchedulerAPI.schedule("War-" + this.key, this.date.getTimeInMillis(), runnable);
	}
	
	public void start() {
		if (isOngoing) return;
		isOngoing = true;
		endTime = System.currentTimeMillis() + (1000 * 60 * 60); // 1 hr
		for (WarTeam team : teams) {
			team.startWar();
		}
		BungeeAPI.broadcast("&7&lA war has started &7(&c" + this.display + "&7)&7&l! &4/war");
		endTask = SchedulerAPI.schedule("WarEnd-" + this.key, endTime, new Runnable() {
			public void run() {
				end();
			}
		});
	}
	
	public void end() {
		if (!isOngoing) return;
		endTask.setCancelled(true);
		BungeeAPI.broadcast("&7&lA war has ended &7(&c" + this.display + "&7)&7&l!");
		int t1 = teams[0].calculateTotalPoints();
		int t2 = teams[1].calculateTotalPoints();
		BungeeAPI.broadcast("&7Final score:");
		BungeeAPI.broadcast("&6" + teams[0].getDisplay() + "&7: &e" + t1);
		BungeeAPI.broadcast("&6" + teams[1].getDisplay() + "&7: &e" + t2);
		if (t1 > t2) {
			BungeeAPI.broadcast("&a&lWinner: " + teams[0].getDisplay());
			Bukkit.getLogger().info("[NeoPvp] War winner for " + this.key + " was " + teams[0].getDisplay());
		}
		else if (t2 > t1) {
			BungeeAPI.broadcast("&a&lWinner: " + teams[1].getDisplay());
			Bukkit.getLogger().info("[NeoPvp] War winner for " + this.key + " was " + teams[1].getDisplay());
		}
		else {
			BungeeAPI.broadcast("&e&lTie!");
			Bukkit.getLogger().info("[NeoPvp] War " + this.key + " was a tie");
		}
		for (WarTeam team : teams) {
			team.endWar();
		}
	}
	
	public boolean isOngoing() {
		return isOngoing;
	}
	
	public void setDate(String src) {
		setDate(src, DEFAULT_WAR_HOUR);
	}
	
	public void setDate(String src, int hour) {
		try {
			this.date.setTime(new SimpleDateFormat("MM/dd/yyyy").parse(src));
			this.date.set(Calendar.HOUR_OF_DAY, hour);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	
	public void createTeam(int num, String key, String display) {
		teams[num - 1] = new WarTeam(this, key, display);
	}
	
	public void addTeamNation(int num, Nation n) {
		teams[num - 1].addNation(n);
	}
	
	public void removeTeamNation(int num, Nation n) {
		teams[num - 1].removeNation(n);
	}
	
	public void setTeamSpawn(int num, Location loc) {
		teams[num - 1].setSpawn(loc);
	}
	
	public void setMascotSpawn(int num, Location loc) {
		teams[num - 1].setMascotSpawn(loc);
	}
	
	public void setDisplay(String display) {
		this.display = display;
	}
	
	public void displayCreator(CommandSender s) {
		Util.msg(s, "§6§l>§8§m--------§c§l» " + this.display + "&c&l «§8§m--------§6§l<", false);
		Util.msg(s, "&6Date&7: " + sdf.format(date.getTime()), false);
		Util.msg(s, "&6World&7: " + (w == null ? "Not set" : w.getName()), false);
		Util.msg(s, "&6Max Players per Team&7: &e" + maxPlayers, false);
		
		displayTeamCreator(1, s);
		displayTeamCreator(2, s);
	}
	
	public void display(CommandSender s) {
		Util.msg(s, "§6§l>§8§m--------§c§l» " + this.display + "&c&l «§8§m--------§6§l<", false);
		displayTeam(1, s);
		displayTeam(2, s);
	}
	
	private void displayTeamCreator(int num, CommandSender s) {
		if (teams[num - 1] != null) {
			WarTeam team = teams[num - 1];
			String msg = "&6Team " + num + " &7(&c" + team.getDisplay() + "&7) - ";
			
			// nations
			Iterator<Nation> iter = team.getNations().iterator();
			while (iter.hasNext()) {
				msg += "&e" + iter.next().getName();
				if (iter.hasNext()) {
					msg += "&7, ";
				}
			}
			Util.msg(s, msg, false);

			// Towns
			Iterator<Town> itertown = team.getWhitelistedTowns().iterator();
			msg = "&7Towns: ";
			while (itertown.hasNext()) {
				msg += itertown.next().getName();
				if (itertown.hasNext()) {
					msg += ", ";
				}
			}
			Util.msg(s, msg, false);
			
			// Players
			msg = "&7Players: ";
			Iterator<String> iterplayer = team.getWhitelistedPlayers().iterator();
			while (iterplayer.hasNext()) {
				msg += iterplayer.next();
				if (iterplayer.hasNext()) {
					msg += ", ";
				}
			}
			Util.msg(s, msg, false);
			
			Util.msg(s, "&7- &6Spawn&7: &e" + (team.getSpawn() == null ? "Not set" : Util.locToString(team.getSpawn(), true, false)), false);
			Util.msg(s, "&7- &6Mascot Spawn&7: &e" + (team.getMascotSpawn() == null ? "Not set" : Util.locToString(team.getMascotSpawn(), true, false)), false);
		}
		else {
			Util.msg(s, "&6Team " + num + "&7: null", false);
		}
	}
	
	private void displayTeam(int num, CommandSender s) {
		WarTeam team = teams[num - 1];
		WarTeam enemy = teams[num == 1 ? 1 : 0];
		int healthLost = enemy.getMascotHealthLost() / 100;
		int points = team.getPoints();
		int total = points + healthLost;
		String msg = "&6Team " + num + " &7(&c" + team.getDisplay() + "&7): &f" + total;

		ComponentBuilder builder = new ComponentBuilder(Util.translateColors(msg))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(buildTeamHover(team, enemy))));
		s.spigot().sendMessage(builder.create());
	}
	
	private String buildTeamHover(WarTeam team, WarTeam enemy) {
		String msg = "";
		
		// Nations
		Iterator<Nation> iter = team.getNations().iterator();
		while (iter.hasNext()) {
			msg += "&e" + iter.next().getName();
			if (iter.hasNext()) {
				msg += "&7, ";
			}
		}
		msg += "\n";
		
		// Towns
		Iterator<Town> itertown = team.getWhitelistedTowns().iterator();
		while (itertown.hasNext()) {
			msg += "&e" + itertown.next().getName();
			if (itertown.hasNext()) {
				msg += "&7, ";
			}
		}
		msg += "\n";
		
		// Players
		Iterator<String> iterplayer = team.getWhitelistedPlayers().iterator();
		while (iterplayer.hasNext()) {
			msg += "&e" + iterplayer.next();
			if (iterplayer.hasNext()) {
				msg += "&7, ";
			}
		}
		msg += "\n";
		
		msg += "&6Kills&7: &e" + team.getKills() + "\n&6Deaths&7: &e" + team.getDeaths() + "\n&6Enemy Mascot Damage&7: &e" + enemy.getMascotHealthLost();
		return Util.translateColors(msg);
	}
	
	public String getKey() {
		return key;
	}

	public String getDisplay() {
		return display;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public Calendar getDate() {
		return date;
	}

	public WarTeam[] getTeams() {
		return teams;
	}
	
	public World getWorld() {
		return w;
	}
	
	public void setWorld(World w) {
		this.w = w;
	}
	
	public long getTimeToEnd() {
		return this.endTime;
	}
	
	public void serialize(Statement stmt) throws SQLException {
		stmt.addBatch("INSERT INTO neopvp_wars VALUES('" + key + "','" + display +"'," + maxPlayers + "," + date.getTimeInMillis() + ");");
		for (WarTeam team : teams) {
			team.serialize(stmt);
		}
	}
}
