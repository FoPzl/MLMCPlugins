package me.fopzl.vote;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.bungee.BungeeAPI;

public class VoteParty {
	private Vote main;
	private VotePartyConfig cfg;
	
	private int points;
	
	public VoteParty(Vote main) {
		this.main = main;
		points = 0;
	}
	
	public void loadConfig(YamlConfiguration cfg) {
		// TODO
	}
	
	public void showStatus(Player p) {
		Util.sendMessageFormatted(p, "&4[&c&lMLMC&4] &e" + points + " / " + cfg.pointsToStart + " &7votes for a vote party to commence!");
	}
	
	public void addPoints(int pts) {
		points += pts;
		tick();
	}
	
	public void setPoints(int pts) {
		points = pts;
	}
	
	private void tick() {
		if(points % cfg.notifyInterval == 0) {
			// TODO: placeholders
			Util.broadcastFormatted(cfg.notifyCommand);
		}
		
		for(int i : cfg.specificNotifies.keySet()) {
			if(points % i == 0) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cfg.specificNotifies.get(i));
			}
		}
		
		if(points <= 0) {
			tryStartCountdown();
		}
	}
	
	private void tryStartCountdown() {
		if(points > cfg.pointsToStart) {
			points = 0;
			
			for(Entry<Integer, String> entry : cfg.countdownCommands.entrySet()) {
				new BukkitRunnable() {
					@Override
					public void run() {
						BungeeAPI.broadcast(entry.getValue());
				}}.runTaskLater(main, 20 * entry.getKey());
			}
			
			new BukkitRunnable() {
				@Override
				public void run() {
					startParty();
			}}.runTaskLater(main, 20 * cfg.countdownLength);
		}
	}
	
	private void startParty() {
		for(String cmd : cfg.partyCommands) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}
}

class VotePartyConfig {
	public int pointsToStart;
	public String[] partyCommands; // ordered
	
	public int notifyInterval; // in points
	public String notifyCommand;
	
	public HashMap<Integer, String> specificNotifies;

	public int countdownLength; // in seconds
	public HashMap<Integer, String> countdownCommands; // key in seconds
}