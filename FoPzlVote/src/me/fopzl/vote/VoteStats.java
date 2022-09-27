package me.fopzl.vote;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class VoteStats {
	private static long streakLimit; // votes
	private static long streakResetTime; // days
	
	boolean needToSave;
	
	int totalVotes; // ever
	int voteStreak; // current
	LocalDateTime lastVoted;
	Map<VoteMonth, Map<String, Integer>> monthlySiteCounts; // value is <voteSite, votes this month>
	
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
		monthlySiteCounts = new HashMap<VoteMonth, Map<String, Integer>>();
	}
	
	public VoteStats(int totalVotes, int voteStreak, LocalDateTime lastVoted) {
		needToSave = false;
		
		this.totalVotes = totalVotes;
		this.voteStreak = voteStreak;
		this.lastVoted = lastVoted;
		monthlySiteCounts = new HashMap<VoteMonth, Map<String, Integer>>();
	}
	
	public void addVote(String site) {
		totalVotes++;
		
		if(voteStreak >= streakLimit || LocalDateTime.now().isAfter(lastVoted.plusDays(streakResetTime))) {
			voteStreak = 0;
		}		
		voteStreak++;
		
		lastVoted = LocalDateTime.now();
		
		VoteMonth now = new VoteMonth(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
		Map<String, Integer> currCounts = monthlySiteCounts.getOrDefault(now, new HashMap<String, Integer>());
		currCounts.put(site, currCounts.getOrDefault(site, 0) + 1);
		monthlySiteCounts.putIfAbsent(now, currCounts);
		
		needToSave = true;
	}
	
	public int getTotalVotes() {
		return totalVotes;
	}
	
	public int getVotesThisMonth() {
		VoteMonth now = new VoteMonth(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
		int sum = 0;
		for(int votes : monthlySiteCounts.get(now).values()) {
			sum += votes;
		}
		return sum;
	}
	
	public int getStreak() {
		return voteStreak;
	}
}

class VoteMonth {
	int yearNum;
	int monthNum;
	
	public VoteMonth(int y, int m) {
		yearNum = y;
		monthNum = m;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		
		VoteMonth other = (VoteMonth)obj;
		return other.yearNum == this.yearNum && other.monthNum == this.monthNum;
	}
	
	@Override
	public int hashCode() {
		return 31 * monthNum + yearNum;
	}
}