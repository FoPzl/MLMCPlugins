package me.fopzl.vote;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.io.IOComponent;

public class VoteIO implements IOComponent {
	HashMap<UUID, VoteStats> playerStats;
	
	@Override
	public String getKey() {
		return "VoteManager";
	}
	
	@Override
	public void cleanup(Statement insert, Statement delete) {}

	@Override
	public void preloadPlayer(OfflinePlayer arg0, Statement arg1) {}

	@Override
	public void savePlayer(Player p, Statement insert, Statement delete) {
		// TODO
	}
	
	@Override
	public void autosavePlayer(Player p, Statement insert, Statement delete) {
		// TODO
	}

	@Override
	public void loadPlayer(Player p, Statement stmt) {
		// TODO
	}
	
	public VoteStats getStats(Player p) {
		// TODO
	}
	
	// month is 1-indexed
	public HashMap<UUID, Integer> getTopVoters(int year, int month) {
		// TODO
	}
}

class VoteStats {
	final static long streakResetTime = 2; // days
	// TODO: configurable?
	
	int totalVotes; // ever
	int voteStreak; // current
	LocalDateTime lastVoted;
	HashMap<String, Integer> monthlySiteCounts; // key is voteSite, value is votes this month
	
	public void addVote(String site) {
		totalVotes++;
		
		if(LocalDateTime.now().isAfter(lastVoted.plusDays(streakResetTime))) {
			voteStreak = 0;
		}
		voteStreak++;
		
		lastVoted = LocalDateTime.now();
		
		monthlySiteCounts.put(site, monthlySiteCounts.getOrDefault(site, 0) + 1);
	}
}