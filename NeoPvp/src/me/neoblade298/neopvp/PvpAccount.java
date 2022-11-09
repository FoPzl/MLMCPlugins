package me.neoblade298.neopvp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.scheduler.SchedulerAPI;
import me.neoblade298.neocore.scheduler.SchedulerAPI.CoreRunnable;
import me.neoblade298.neocore.util.PaginatedList;
import me.neoblade298.neocore.util.Util;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class PvpAccount {
	private UUID uuid;
	private Player p;
	private HashSet<UUID> uniqueKills;
	private double pvpBalance;
	private int elo, killstreak, wins, losses;
	private long protectionExpires = -1;
	public static final DecimalFormat df = new DecimalFormat("#.##");
	
	private CoreRunnable cr;
	
	// Exclusively for first time login
	public PvpAccount(UUID uuid) {
		this.uuid = uuid;
		this.protectionExpires = System.currentTimeMillis() + (1000 * 60 * 60 * 24); // 24 hours
		this.elo = 1800;
		this.uniqueKills = new HashSet<UUID>();
		
		cr = SchedulerAPI.schedule("neopvp-protectionexpires-" + uuid, protectionExpires, new Runnable() {
			public void run() {
				removeProtection();
			}
		});
	}
	
	public PvpAccount(UUID uuid, ResultSet rs) throws SQLException {
		this.uuid = uuid;
		killstreak = rs.getInt(2);
		wins = rs.getInt(3);
		losses = rs.getInt(4);
		elo = rs.getInt(5);
		pvpBalance = rs.getDouble(6);
		protectionExpires = rs.getLong(7);
		
		if (protectionExpires > System.currentTimeMillis()) {
			cr = SchedulerAPI.schedule("neopvp-protectionexpires-" + uuid, protectionExpires, new Runnable() {
				public void run() {
					removeProtection();
				}
			});
		}
	}
	
	public void loadPlayer() {
		this.p = Bukkit.getPlayer(this.uuid);
	}
	
	public void removeProtection() {
		// If statement for in case a scheduler removes protection when it's already gone
		if (this.protectionExpires != -1) {
			this.protectionExpires = -1;
			Util.msg(p, "Your protection was removed.");
		}
	}
	
	public void addProtection(long timeInMillis) {
		if (protectionExpires < System.currentTimeMillis()) {
			protectionExpires = System.currentTimeMillis();
		}
		protectionExpires += timeInMillis;
		double timeRemaining = protectionExpires - System.currentTimeMillis();
		double hoursLeft = timeRemaining / 1000 / 60 / 60;
		Util.msg(p, "&7Your pvp protection now expires in &e" + df.format(hoursLeft) + "h&7.");
		
		if (cr != null) {
			cr.setCancelled(true);
		}
		
		cr = SchedulerAPI.schedule("neopvp-protectionexpires-" + p.getUniqueId(), protectionExpires, new Runnable() {
			public void run() {
				removeProtection();
			}
		});
	}
	
	public void addElo(int amount) {
		elo += amount;
	}
	
	public void takeElo(int amount) {
		addElo(-amount);
	}
	
	public int getElo() {
		return elo;
	}
	
	public double getBalance() {
		return pvpBalance;
	}
	
	public void setBalance(double balance) {
		pvpBalance = balance;
	}
	
	public void addBalance(double balance) {
		pvpBalance += balance;
	}
	
	public int getNumUniqueKills() {
		return uniqueKills.size();
	}
	
	public PaginatedList<UUID> getUniqueKills() {
		return new PaginatedList<UUID>(uniqueKills);
	}
	
	public void addUniqueKill(Player p) {
		uniqueKills.add(p.getUniqueId());
	}
	
	public void clearUniqueKills() {
		uniqueKills.clear();
	}
	
	public void incrementKillstreak() {
		killstreak++;
	}
	
	public int getKillstreak() {
		return killstreak;
	}
	
	public void clearKillstreak() {
		killstreak = 0;
	}
	
	public void incrementWins() {
		wins++;
	}
	
	public void incrementLosses() {
		losses++;
	}
	
	public void setUniqueKills(HashSet<UUID> uniqueKills) {
		this.uniqueKills = uniqueKills;
	}
	
	public int getWins() {
		return wins;
	}
	
	public int getLosses() {
		return losses;
	}
	
	public boolean isProtected() {
		return protectionExpires > System.currentTimeMillis();
	}
	
	public void displayAccount(CommandSender s) {
		if (p == null) {
			loadPlayer();
		}
		
		String prot = "§6Protection: ";
		Util.msg(s, "&6===[&e" + p.getName() + "&6]===", false);
		boolean displayToSelf = s instanceof Player && (Player) s == this.p;
		if (protectionExpires < System.currentTimeMillis()) {
			ComponentBuilder b = new ComponentBuilder(prot + "§4N/A ");
			if (displayToSelf) {
				b.append("§7§o[Click to buy (§e5000g §7/ §e30m§7)]")
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pvp buyprotection"))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/pvp buyprotection")));
			}
			s.spigot().sendMessage(b.create());
		}
		else {
			double millisToExpiration = protectionExpires - System.currentTimeMillis();
			double hoursLeft = millisToExpiration / 1000 / 60 / 60;
			ComponentBuilder b = new ComponentBuilder(prot + "§eExpires in " + df.format(hoursLeft) + "h ");
			if (displayToSelf) {
				b.append("§7§o[Click to remove]", FormatRetention.NONE)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pvp removeprotection"))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/pvp removeprotection")));
			}
			s.spigot().sendMessage(b.create());
		}
		Util.msg(s, "&6Rating: &e" + elo, false);
		ComponentBuilder b = new ComponentBuilder("§6Pvp Balance: §e" + pvpBalance);
		if (displayToSelf) {
			b.append(" §7§o[Click to redeem]", FormatRetention.NONE)
			.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pvp redeem"))
			.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/pvp redeem")));
		}
		s.spigot().sendMessage(b.create());
		Util.msg(s, "&6Killstreak: &e" + killstreak, false);
		Util.msg(s, "&6Wins: &e" + wins, false);
		Util.msg(s, "&6Losses: &e" + losses, false);
		b = new ComponentBuilder("§6# of Unique Kills: §e" + uniqueKills.size())
			.append(" §7§o[Click to view]", FormatRetention.NONE)
			.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pvp uniquekills " + p.getName()))
			.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/pvp uniquekills " + p.getName())));
		s.spigot().sendMessage(b.create());
	}
	
	public void redeemBounty(CommandSender s) {
		NeoCore.getEconomy().depositPlayer(p, pvpBalance);
		Util.msg(s, "&7Successfully redeemed &e" + pvpBalance + "g&7.");
		pvpBalance = 0;
		uniqueKills.clear();
	}
	
	public void displayUniqueKills(CommandSender s, int page) {
		PaginatedList<UUID> pagedKills = new PaginatedList<UUID>(uniqueKills);
		if (uniqueKills.size() == 0) {
			Util.msg(s, "&cThis player doesn't have any unique kills yet!");
		}
		else if (pagedKills.pages() <= page) {
			Util.msg(s, "&cThis page doesn't exist!");
		}
		else {
			Util.msg(s, "&6Unique Kills:");
			for (UUID uuid : pagedKills.get(page)) {
				Util.msg(s, "&7- &e" + Bukkit.getOfflinePlayer(uuid).getName());
			}
			pagedKills.displayFooter(s, page, "/pvp uniquekills " + p.getName() + " " + (page + 1), "/pvp uniquekills " + p.getName() + " " + (page - 1));
		}
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public Player getPlayer() {
		if (this.p == null) {
			loadPlayer();
		}
		return this.p;
	}
	
	public long getProtectionExpiration() {
		return protectionExpires;
	}

	public void setPvpBalance(double pvpBalance) {
		this.pvpBalance = pvpBalance;
	}

	public void setElo(int elo) {
		this.elo = elo;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}
}
