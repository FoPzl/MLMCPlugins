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
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.io.IOComponent;
import me.neoblade298.neocore.scheduler.ScheduleInterval;
import me.neoblade298.neocore.scheduler.SchedulerAPI;

public class VoteIO implements IOComponent {
	Vote main;
	
	public VoteIO(Vote main) {
		this.main = main;
		
		loadQueue();
		
		SchedulerAPI.scheduleRepeating("FoPzlVote-Autosave-Queue", ScheduleInterval.FIFTEEN_MINUTES, new Runnable() {
			public void run() {
				new BukkitRunnable() {
					public void run() {
						saveQueue();
					}
				}.runTaskAsynchronously(main);
			}
		});
	}
	
	@Override
	public String getKey() {
		return "FoPzlVoteIO";
	}
	
	@Override
	public void cleanup(Statement insert, Statement delete) {
		saveQueue();
	}

	@Override
	public void preloadPlayer(OfflinePlayer arg0, Statement arg1) {}

	@Override
	public void savePlayer(Player p, Statement insert, Statement delete) {
		autosavePlayer(p, insert, delete);
		main.getVoteInfo().playerStats.remove(p.getUniqueId());
	}
	
	@Override
	public void autosavePlayer(Player p, Statement insert, Statement delete) {
		UUID uuid = p.getUniqueId();
		if(!main.getVoteInfo().playerStats.containsKey(uuid)) return;
		
		VoteStats vs = main.getVoteInfo().playerStats.get(uuid);
		if(!vs.needToSave) return;
		vs.needToSave = false;
		
		try {
			insert.addBatch("replace into fopzlvote_playerStats values ('" + uuid + "', " + vs.totalVotes + ", " + vs.voteStreak + ", '" + vs.lastVoted.toString() + "');");
			
			int year = LocalDateTime.now().getYear();
			int month = LocalDateTime.now().getMonthValue();
			VoteMonth now = new VoteMonth(year, month);
			if(month == 1) {
				year--;
				month = 12;
			}
			VoteMonth prev = new VoteMonth(year, month);
			
			// only save this month and the last
			HashMap<String, Integer> currCounts = vs.monthlySiteCounts.get(now);
			if(currCounts != null) {
				for(Entry<String, Integer> entry : currCounts.entrySet()) {
					insert.addBatch("replace into fopzlvote_playerHist values ('" + uuid + "', " + year + ", " + month + ", '" + entry.getKey() + "', " + entry.getValue() + ");");
				}
			}
			
			HashMap<String, Integer> prevCounts = vs.monthlySiteCounts.get(prev);
			if(prevCounts != null) {
				for(Entry<String, Integer> entry : prevCounts.entrySet()) {
					insert.addBatch("replace into fopzlvote_playerHist values ('" + uuid + "', " + year + ", " + month + ", '" + entry.getKey() + "', " + entry.getValue() + ");");
				}
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
			
			rs = stmt.executeQuery("select * from fopzlvote_playerHist where uuid = '" + uuid + "';");
			while(rs.next()) {
				VoteMonth voteMonth = new VoteMonth(rs.getInt("year"), rs.getInt("month"));
				String voteSite = rs.getString("voteSite");
				int numVotes = rs.getInt("numVotes");
				
				HashMap<String, Integer> monthCounts = vs.monthlySiteCounts.getOrDefault(voteMonth, new HashMap<String, Integer>());
				monthCounts.put(voteSite, numVotes);
				vs.monthlySiteCounts.putIfAbsent(voteMonth, monthCounts);
			}
			
			main.getVoteInfo().playerStats.put(uuid, vs);
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to load vote data for player " + p.getName());
			e.printStackTrace();
		}
	}
	
	public void saveQueue() {
		Statement stmt = NeoCore.getStatement();
		
		try {
			stmt.execute("delete from fopzlvote_voteQueue");
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to clear queue data");
			e.printStackTrace();
		}
		
		for(Entry<UUID, HashMap<String, Integer>> entry : main.getVoteInfo().queuedRewards.entrySet()) {
			UUID uuid = entry.getKey();
			try {
				for(Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
					stmt.addBatch("replace into fopzlvote_voteQueue values ('" + uuid + "', '" + subEntry.getKey() + "', " + subEntry.getValue() + ");");
				}
			} catch (SQLException e) {
				Bukkit.getLogger().warning("Failed to save queue data for uuid " + uuid);
				e.printStackTrace();
			}
		}
		
		try {
			stmt.executeBatch();
			stmt.close();
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to save queue data batch");
			e.printStackTrace();
		}
	}
	
	public void loadQueue() {
		HashMap<UUID, HashMap<String, Integer>> newQueue = new HashMap<UUID, HashMap<String, Integer>>();
		Statement stmt = NeoCore.getStatement();
		
		try {
			ResultSet rs = stmt.executeQuery("select * from fopzlvote_voteQueue");
			while(rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				String voteSite = rs.getString("voteSite");
				int numVotes = rs.getInt("numVotes");
				
				HashMap<String, Integer> pq = newQueue.getOrDefault(uuid, new HashMap<String, Integer>());
				pq.put(voteSite, numVotes);
				newQueue.putIfAbsent(uuid, pq);
			}
			stmt.close();
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to load queue data");
			e.printStackTrace();
		}
		
		main.getVoteInfo().queuedRewards = newQueue;
	}
	
	// month is 1-indexed
	// returned list items are indexed as: [0] - uuid (as UUID), [1] = # of votes (as int)
	public List<Object[]> getTopVoters(int year, int month, int numVoters) {
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