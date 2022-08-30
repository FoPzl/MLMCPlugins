package me.fopzl.vote;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class VoteInfo {
	public HashMap<UUID, VoteStats> playerStats;
	public HashMap<UUID, HashMap<String, Integer>> queuedRewards;

	public VoteStats getStats(Player p) {
		UUID uuid = p.getUniqueId();
		if(playerStats.containsKey(uuid)) {
			return playerStats.get(uuid);
		} else {
			VoteStats vs = new VoteStats();
			playerStats.put(uuid, vs);
			return vs;
		}
	}
}

class VoteStats {
	private static long streakLimit; // votes
	private static long streakResetTime; // days
	
	boolean needToSave;
	
	int totalVotes; // ever
	int voteStreak; // current
	LocalDateTime lastVoted;
	HashMap<String, Integer> monthlySiteCounts; // key is voteSite, value is votes this month
	// TODO: handle player being logging in as month crosses over to next
	
	public static void setStreakLimit(long numVotes) {
		streakLimit = numVotes;
	}
	
	public static void setStreakResetTime(long numDays) {
		streakResetTime = numDays;
	}
	
	public VoteStats() {
		needToSave = true;
		
		totalVotes = 0;
		voteStreak = 0;
		lastVoted = LocalDateTime.MIN;
		monthlySiteCounts = new HashMap<String, Integer>();
	}
	
	public VoteStats(int totalVotes, int voteStreak, LocalDateTime lastVoted) {
		needToSave = false;
		
		this.totalVotes = totalVotes;
		this.voteStreak = voteStreak;
		this.lastVoted = lastVoted;
		monthlySiteCounts = new HashMap<String, Integer>();
	}
	
	public void addVote(String site) {
		totalVotes++;
		
		if(voteStreak >= streakLimit || LocalDateTime.now().isAfter(lastVoted.plusDays(streakResetTime))) {
			voteStreak = 0;
		}		
		voteStreak++;
		
		lastVoted = LocalDateTime.now();
		
		monthlySiteCounts.put(site, monthlySiteCounts.getOrDefault(site, 0) + 1);
		
		needToSave = true;
	}
}