package me.neoblade298.neoleaderboard.points;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class PlayerEntry implements Comparable<PlayerEntry> {
	private UUID uuid;
	private String display;
	private HashMap<PlayerPointType, Double> points = new HashMap<PlayerPointType, Double>();
	private HashMap<PlayerPointType, Double> contributedPoints = new HashMap<PlayerPointType, Double>();
	private double contributed;
	private static double LIMIT = 250;
	private Nation n;
	private Town t;
	private NationEntry ne;
	private TownEntry te;
	
	public PlayerEntry(UUID uuid) {
		this.uuid = uuid;
		this.display = Bukkit.getOfflinePlayer(uuid).getName();
		
		// Must be loaded after NationEntry and TownEntry for this
		TownyAPI api = TownyAPI.getInstance();
		Resident r = api.getResident(uuid);
		t = r.getTownOrNull();
		n = t.getNationOrNull();
		if (n == null) return;
		ne = PointsManager.getNationEntry(n.getUUID());
		te = ne.getTownEntry(t.getUUID());
	}
	
	public void setPoints(double amount, PlayerPointType type) {
		points.put(type, amount);
	}
	
	public void setContributedPoints(double amount, PlayerPointType type) {
		contributedPoints.put(type, amount);
	}
	
	public double calculateContributed() {
		double sum = 0;
		for (double d : contributedPoints.values()) {
			sum += d;
		}
		contributed = sum;
		return contributed;
	}
	
	public void setTown(UUID town) {
		this.t = TownyAPI.getInstance().getTown(town);
		this.n = t.getNationOrNull();
		if (n == null) return;
		this.ne = PointsManager.getNationEntry(n.getUUID());
		this.te = ne.getTownEntry(t.getUUID());
	}

	// Return the amount that can be contributed
	public double addPoints(double amount, PlayerPointType type) {
		points.put(type, points.getOrDefault(type, 0D) + amount);

		double before = contributedPoints.getOrDefault(type, 0.0D);
		double after = before + amount;
		double contributable = 0;
		
		if (amount >= 0) {
			if (contributed >= PointsManager.getMaxContribution()) {
				return 0;
			}
			else if (before < LIMIT && after > LIMIT) {
				contributable = LIMIT - before;
				contributedPoints.put(type, LIMIT);
				contributed += contributable;
				return contributable; // Will be positive
			}
			else if (before < LIMIT && after < LIMIT) {
				contributedPoints.put(type, after);
				contributed += amount;
				return amount;
			}
			// before > LIMIT && after > LIMIT we don't do anything
			// before > LIMIT && after < LIMIT not possible
		}
		else {
			if (before > LIMIT && after < LIMIT) {
				contributable = after - LIMIT;
				contributedPoints.put(type, after);
				contributed += contributable;
				return contributable; // Will be negative
			}
			else if (before < LIMIT && after < LIMIT) {
				contributedPoints.put(type, after);
				contributed += amount;
				return amount;
			}
			// before > LIMIT && after > LIMIT we don't do anything
			// before < LIMIT && after > LIMIT not possible
		}
		
		return 0;
	}
	
	public double takePoints(double amount, PlayerPointType type) {
		return addPoints(-amount, type);
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public String getDisplay() {
		return this.display;
	}
	
	public void setDisplay(String display) {
		this.display = display;
	}
	
	public double getContributed() {
		return contributed;
	}
	
	public HashMap<PlayerPointType, Double> getContributedPoints() {
		return contributedPoints;
	}
	
	public double getContributedPoints(PlayerPointType type) {
		return contributedPoints.getOrDefault(type, 0D);
	}
	
	public HashMap<PlayerPointType, Double> getTotalPoints() {
		return points;
	}
	
	public boolean isEmpty() {
		return points.isEmpty();
	}
	
	public static double getLimit() {
		return LIMIT;
	}
	
	public Town getTown() {
		return t;
	}
	
	public Nation getNation() {
		return n;
	}
	
	public NationEntry getNationEntry() {
		return ne;
	}
	
	public TownEntry getTownEntry() {
		// Unlike nation entries, town entries may not exist at 0 points
		if (te == null) {
			te = ne.getTownEntry(t.getUUID());
		}
		return te;
	}

	@Override
	public int compareTo(PlayerEntry o) {
		if (this.contributed > o.contributed) {
			return 1;
		}
		if (this.contributed < o.contributed) {
			return -1;
		}
		else {
			// Since we sort in descending, this will make it ascending
			return o.display.compareTo(this.display);
		}
	}
	
	public void clear() {
		getTownEntry().clearPlayer(this);
	}
}	
