package me.fopzl.vote;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.io.IOComponent;

public class VoteIO implements IOComponent {
	private HashMap<UUID, VoteStats> playerStats;
	
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
		autosavePlayer(p, insert, delete);
		playerStats.remove(p.getUniqueId());
	}
	
	@Override
	public void autosavePlayer(Player p, Statement insert, Statement delete) {
		UUID uuid = p.getUniqueId();
		if(!playerStats.containsKey(uuid)) return;
		
		VoteStats vs = playerStats.get(uuid);
		if(!vs.needToSave) return;
		vs.needToSave = false;
		
		try {
			insert.addBatch("replace into fopzlvote_playerStats values ('" + uuid + "', " + vs.totalVotes + ", " + vs.voteStreak + ", '" + vs.lastVoted.toString() + "')");
			
			int year = LocalDateTime.now().getYear();
			int month = LocalDateTime.now().getMonthValue();
			for(Entry<String, Integer> entry : vs.monthlySiteCounts.entrySet()) {
				insert.addBatch("replace into fopzlvote_playerHist values ('" + uuid + "', " + year + ", " + month + ", '" + entry.getKey() + "', " + entry.getValue() + ")");
			}
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to save vote data for player " + p.getName());
			e.printStackTrace();
		}
	}

	@Override
	public void loadPlayer(Player p, Statement stmt) {
		UUID uuid = p.getUniqueId();
		
		try {
			ResultSet rs = stmt.executeQuery("select * from fopzlvote_playerStats where uuid = '" + uuid + "';");
			VoteStats vs = new VoteStats(rs.getInt("totalVotes"), rs.getInt("voteStreak"), rs.getObject("whenLastVoted", LocalDateTime.class));
			//rs.close(); // TODO: verify if this is needed
			
			rs = stmt.executeQuery("select * from fopzlvote_playerHist where uuid = '" + uuid + "' and year = " + LocalDateTime.now().getYear() + " and month = " + LocalDateTime.now().getMonthValue() + ";");
			while(rs.next()) {
				vs.monthlySiteCounts.put(rs.getString("voteSite"), rs.getInt("numVotes"));
			}
			
			playerStats.put(uuid, vs);
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to load vote data for player " + p.getName());
			e.printStackTrace();
		}
	}
	
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
	
	// month is 1-indexed
	// returned list items are indexed as: [0] - uuid (as UUID), [1] = # of votes (as int)
	public List<Object[]> getTopVoters(int year, int month, int numVoters) {
		// TODO: async this
		List<Object[]> topVoters = new ArrayList<Object[]>();
		
		try {
			Statement stmt = NeoCore.getStatement();
			
			ResultSet rs = stmt.executeQuery("select uuid, sum(numVotes) sumVotes from fopzlvote_playerHist where year = " + year + " and month = " + month + " group by uuid order by sumVotes desc limit " + numVoters + ";");
			while(rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				int sumVotes = rs.getInt("sumVotes");
				
				topVoters.add(new Object[] { uuid, sumVotes });
			}
			
			stmt.close();
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to get vote leaderboard from sql");
			e.printStackTrace();
		}
		
		return topVoters;
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