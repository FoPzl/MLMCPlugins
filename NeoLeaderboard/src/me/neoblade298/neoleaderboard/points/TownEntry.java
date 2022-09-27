package me.neoblade298.neoleaderboard.points;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.UUID;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class TownEntry implements Comparable<TownEntry> {
	private HashMap<PlayerPointType, Double> playerPoints = new HashMap<PlayerPointType, Double>();
	private HashMap<UUID, PlayerEntry> players = new HashMap<UUID, PlayerEntry>();
	private double total;
	private int contributors;
	private Town town;
	private Nation nation;
	private NationEntry ne;
	
	private TreeSet<PlayerEntry> topPlayers = new TreeSet<PlayerEntry>();
	private HashMap<PlayerPointType, TreeSet<PlayerEntry>> topPlayerCategories = new HashMap<PlayerPointType, TreeSet<PlayerEntry>>();
	private HashMap<PlayerPointType, Long> playerCategoryLastSorted = new HashMap<PlayerPointType, Long>();
	private long playersLastSorted = 0L;
	private static final long SORT_COOLDOWN = 1000 * 60 * 15;
	
	public TownEntry(UUID uuid, UUID nation, int contributors) {
		this.contributors = contributors;
		this.town = TownyAPI.getInstance().getTown(uuid);
		this.nation = TownyAPI.getInstance().getNation(nation);
		this.ne = PointsManager.getNationEntry(nation);
		
		for (PlayerPointType type : PlayerPointType.values()) {
			Comparator<PlayerEntry> comp = new Comparator<PlayerEntry>() {
				@Override
				public int compare(PlayerEntry p1, PlayerEntry p2) {
					if (p1.getContributedPoints(type) > p2.getContributedPoints(type)) {
						return 1;
					}
					else if (p1.getContributedPoints(type) < p2.getContributedPoints(type)) {
						return -1;
					}
					else {
						// Since we sort in descending, this will make name ascending
						return p2.getDisplay().compareTo(p1.getDisplay());
					}
				}
			};
			topPlayerCategories.put(type, new TreeSet<PlayerEntry>(comp));
			playerCategoryLastSorted.put(type, 0L);
		}
	}
	
	public void addPlayerPoints(double amount, PlayerPointType type, UUID uuid) {
		playerPoints.put(type, playerPoints.getOrDefault(type, 0D) + amount);
		total += amount;
		
		PlayerEntry pe = PointsManager.getPlayerEntry(uuid);
		players.putIfAbsent(uuid, pe);
	}
	
	public void takePlayerPoints(double amount, PlayerPointType type, UUID uuid) {
		addPlayerPoints(-amount, type, uuid);
	}
	
	public HashMap<PlayerPointType, Double> getPlayerPoints() {
		return playerPoints;
	}
	
	public double getPlayerPoints(PlayerPointType type) {
		return playerPoints.getOrDefault(type, 0D);
	}
	
	public int getContributors() {
		return contributors;
	}
	
	public Town getTown() {
		return town;
	}
	
	public double getTotalPoints() {
		return total;
	}
	
	public HashMap<UUID, PlayerEntry> getAllPlayerPoints() {
		return players;
	}
	
	public Nation getNation() {
		return nation;
	}
	
	public NationEntry getNationEntry() {
		return ne;
	}


	@Override
	public int compareTo(TownEntry o) {
		if (this.total > o.total) {
			return 1;
		}
		else if (this.total < o.total) {
			return -1;
		}
		else {
			// Since we sort in descending, this will make it ascending
			return o.town.getName().compareTo(this.town.getName());
		}
	}
	
	public void clearPlayer(PlayerEntry pe) {
		players.remove(pe.getUuid());
	}

	public TreeSet<PlayerEntry> getTopPlayers() {
		if (System.currentTimeMillis() > playersLastSorted + SORT_COOLDOWN) {
			topPlayers.clear();
			for (PlayerEntry te : players.values()) {
				topPlayers.add(te);
			}
			playersLastSorted = System.currentTimeMillis();
		}
		return topPlayers;
	}

	public TreeSet<PlayerEntry> getTopPlayers(PlayerPointType type) {
		if (System.currentTimeMillis() > playersLastSorted + SORT_COOLDOWN) {
			topPlayers.clear();
			for (PlayerEntry pe : players.values()) {
				topPlayers.add(pe);
			}
			playerCategoryLastSorted.put(type, System.currentTimeMillis());
		}
		return topPlayers;
	}
}
