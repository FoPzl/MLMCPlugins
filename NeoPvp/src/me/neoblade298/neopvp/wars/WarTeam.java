package me.neoblade298.neopvp.wars;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.util.Util;

public class WarTeam {
	private War war;
	private String key, display;
	private int points, kills, deaths;
	private ActiveMob mascot;
	private Location spawn, mascotSpawn;
	private HashSet<Nation> nations = new HashSet<Nation>();
	private HashSet<Town> whitelistedTowns = new HashSet<Town>();
	private HashSet<String> whitelistedPlayers = new HashSet<String>();
	
	private static final int NATIONTYPE = 0, TOWNTYPE = 1, PLAYERTYPE = 2;
	
	private static final String MASCOT_NAME = "WarMascot";
	
	public WarTeam(String war, ResultSet team) throws SQLException {
		this.war = WarManager.getWar(team.getString(1));
		this.key = team.getString(2);
		this.display = team.getString(3);
		this.spawn = Util.stringToLoc(team.getString(4));
		this.mascotSpawn = Util.stringToLoc(team.getString(5));
		TownyAPI api = TownyAPI.getInstance();

		Statement stmt = NeoCore.getStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM neopvp_warwhitelists WHERE war = '" + war + "' AND team = '" + this.key + "';");
		while (rs.next()) {
			if (rs.getInt(3) == NATIONTYPE) {
				Nation n = api.getNation(UUID.fromString(rs.getString(4)));
				if (n != null) {
					nations.add(n);
				}
				else {
					Bukkit.getLogger().warning("[NeoPvp] Failed to load nation " + rs.getString(4) + " for war " + this.war.getKey());
				}
			}
			else if (rs.getInt(3) == TOWNTYPE) {
				Town t = api.getTown(UUID.fromString(rs.getString(4)));
				if (t != null) {
					whitelistedTowns.add(t);
				}
				else {
					Bukkit.getLogger().warning("[NeoPvp] Failed to load town " + rs.getString(4) + " for war " + this.war.getKey());
				}
			}
			else {
				whitelistedPlayers.add(rs.getString(4));
			}
		}
	}
	
	public WarTeam(War war, String key, String display) {
		this.war = war;
		this.key = key;
		this.display = display;
	}
	
	public void addNation(Nation n) {
		nations.add(n);
	}
	
	public void removeNation(Nation n) {
		nations.remove(n);
	}
	
	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}
	
	public void setMascotSpawn(Location mascotSpawn) {
		this.mascotSpawn = mascotSpawn;
	}

	public String getDisplay() {
		return display;
	}

	public int getPoints() {
		return points;
	}
	
	public int getKills() {
		return kills;
	}
	
	public int calculateTotalPoints() {
		return points - (getMascotHealthLost() / 100);
	}
	
	public void addKill() {
		kills++;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public void addDeath() {
		deaths++;
	}
	
	public int getMascotHealthLost() {
		return (int) (mascot.getEntity().getMaxHealth() - mascot.getEntity().getHealth());
	}
	
	public void addPoints(int amount) {
		points += amount;
	}

	public Location getSpawn() {
		return spawn;
	}

	public Location getMascotSpawn() {
		return mascotSpawn;
	}

	public HashSet<Nation> getNations() {
		return nations;
	}
	
	public HashSet<Town> getWhitelistedTowns() {
		return whitelistedTowns;
	}
	
	public HashSet<String> getWhitelistedPlayers() {
		return whitelistedPlayers;
	}
	
	public void startWar() {
		mascot = MythicBukkit.inst().getMobManager().spawnMob(MASCOT_NAME, mascotSpawn);
		mascot.getEntity().setCustomName(display);
	}
	
	public void endWar() {
		mascot.despawn();
	}
	
	public ActiveMob getMascot() {
		return mascot;
	}
	
	public void addWhitelistedTown(Town town) {
		whitelistedTowns.add(town);
	}
	
	public void removeWhitelistedTown(Town town) {
		whitelistedTowns.remove(town);
	}
	
	public void addWhitelistedPlayer(String name) {
		whitelistedPlayers.add(name.toUpperCase());
	}
	
	public void removeWhitelistedPlayer(String name) {
		whitelistedPlayers.remove(name.toUpperCase());
	}
	
	public boolean isMember(Player p) {
		TownyAPI api = TownyAPI.getInstance();
		Resident r = api.getResident(p);
		Town t = api.getResidentTownOrNull(r);
		Nation n = api.getResidentNationOrNull(r);
		
		if (n != null && nations.contains(n)) {
			return true;
		}
		if (t != null && whitelistedTowns.contains(t)) {
			return true;
		}
		return whitelistedPlayers.contains(p.getName().toUpperCase());
	}
	
	public void serialize(Statement stmt) throws SQLException {
		stmt.addBatch("INSERT INTO neopvp_warteams VALUES('" + war.getKey() + "','" + key + "','" + display + "','" +
				Util.locToString(spawn, true, false) + "','" + Util.locToString(mascotSpawn, true, false) + ");");
		
		for (Nation n : nations) {
			stmt.addBatch("INSERT INTO neopvp_warwhitelists VALUES ('" + war.getKey() + "','" + key + "'," +
					NATIONTYPE + ",'" + n.getUUID() + "');");
		}
		for (Town t : whitelistedTowns) {
			stmt.addBatch("INSERT INTO neopvp_warwhitelists VALUES ('" + war.getKey() + "','" + key + "'," +
					TOWNTYPE + ",'" + t.getUUID() + "');");
		}
		for (String s : whitelistedPlayers) {
			stmt.addBatch("INSERT INTO neopvp_warwhitelists VALUES ('" + war.getKey() + "','" + key + "'," +
					PLAYERTYPE + ",'" + s+ "');");
		}
	}
}
