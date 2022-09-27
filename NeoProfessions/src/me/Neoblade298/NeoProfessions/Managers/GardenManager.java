package me.Neoblade298.NeoProfessions.Managers;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.Neoblade298.NeoProfessions.Professions;
import me.Neoblade298.NeoProfessions.Gardens.Fertilizer;
import me.Neoblade298.NeoProfessions.Gardens.Garden;
import me.Neoblade298.NeoProfessions.Gardens.GardenSlot;
import me.Neoblade298.NeoProfessions.Inventories.GardenInventory;
import me.Neoblade298.NeoProfessions.Minigames.MinigameParameters;
import me.Neoblade298.NeoProfessions.Objects.Manager;
import me.Neoblade298.NeoProfessions.Objects.Rarity;
import me.Neoblade298.NeoProfessions.PlayerProfessions.ProfessionType;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.io.FileLoader;
import me.neoblade298.neocore.io.IOComponent;

public class GardenManager implements IOComponent, Manager {
	public static Professions main;
	private static HashMap<UUID, HashMap<ProfessionType, Garden>> gardens = new HashMap<UUID, HashMap<ProfessionType, Garden>>();
	private static HashMap<UUID, ProfessionType> playersGardening = new HashMap<UUID, ProfessionType>();
	private static HashMap<Integer, Fertilizer> fertilizers = new HashMap<Integer, Fertilizer>();
	private static HashMap<UUID, ArrayList<BukkitTask>> gardenMsgs = new HashMap<UUID, ArrayList<BukkitTask>>();
	private static FileLoader fertilizerLoader;
	
	static {
		fertilizerLoader = (yaml, file) -> {
			for (String key : yaml.getKeys(false)) {
				ConfigurationSection cfg = yaml.getConfigurationSection(key);
				int id = Integer.parseInt(key);
				double timeMult = cfg.getDouble("time-multiplier", 1);
				double amountMult = cfg.getDouble("amount-multiplier", 1);
				
				HashMap<Rarity, Double> rarityWeightMults = new HashMap<Rarity, Double>();
				ConfigurationSection rwcfg = cfg.getConfigurationSection("rarity-weight-multipliers");
				if (rwcfg != null) {
					for (String rar : rwcfg.getKeys(false)) {
						rarityWeightMults.put(Rarity.valueOf(rar.toUpperCase()), rwcfg.getDouble(rar));
					}
				}
				MinigameParameters params = new MinigameParameters(amountMult, rarityWeightMults);
				fertilizers.put(id, new Fertilizer(id, params, timeMult));
			}
		};
	}
	
	public GardenManager(Professions main) {
		GardenManager.main = main;
		reload();
	}
	
	@Override
	public void reload() {
		Bukkit.getLogger().log(Level.INFO, "[NeoProfessions] Loading Garden manager...");
		fertilizers.clear();
		try {
			NeoCore.loadFiles(new File(main.getDataFolder(), "fertilizers"), fertilizerLoader);
		} catch (NeoIOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void preloadPlayer(OfflinePlayer p, Statement stmt) {	}
	
	@Override
	public void loadPlayer(Player p, Statement stmt) {

		HashMap<ProfessionType, Garden> pgardens = new HashMap<ProfessionType, Garden>();
		gardens.put(p.getUniqueId(), pgardens);
		for (ProfessionType type : ProfessionType.values()) {
			if (type.equals(ProfessionType.CRAFTER)) {
				continue;
			}
			pgardens.put(type, new Garden());
		}
		
		try {
			// Set up gardens
			boolean hasHarvestable = false;
			ResultSet rs = stmt.executeQuery("SELECT * FROM professions_gardens WHERE UUID = '" + p.getUniqueId() + "';");
			while (rs.next()) {
				pgardens.get(ProfessionType.valueOf(rs.getString(2))).setSize(rs.getInt(3));
			}

			// Set up garden slots
			rs = stmt.executeQuery("SELECT * FROM professions_gardenslots WHERE UUID = '" + p.getUniqueId() + "';");
			while (rs.next()) {
				if (rs.getInt(4) == -1) { // Ignore empty slots
					continue;
				}
				GardenSlot gslot = new GardenSlot(rs.getInt(4), GardenManager.getFertilizer(rs.getInt(6)), rs.getLong(5));
				if (gslot.canHarvest()) {
					hasHarvestable = true;
				}
				else {
					addGardenMessage(p.getUniqueId(), gslot);
				}
				pgardens.get(ProfessionType.valueOf(rs.getString(2))).getSlots().put(rs.getInt(3), gslot);
			}
			
			if (hasHarvestable) {
				// Only send if player is on by now
				new BukkitRunnable() {
					public void run() {
						Player online = Bukkit.getPlayer(p.getUniqueId());
						if (online != null) {
							online.sendMessage("§4[§c§lMLMC§4] §7Your §6/gardens §7have harvestable crops!");
						}
					}
				}.runTaskLaterAsynchronously(main, 100L);
			}
		}
		catch (Exception e) {
			Bukkit.getLogger().log(Level.WARNING, "Professions failed to load or init gardens for user " + p.getName());
			e.printStackTrace();
		}
	}

	@Override
	public void savePlayer(Player p, Statement insert, Statement delete) {
		UUID uuid = p.getUniqueId();
		HashMap<ProfessionType, Garden> pgardens = gardens.get(uuid);
		try {
			for (ProfessionType type : pgardens.keySet()) {
				Garden garden = pgardens.get(type);
				// Save garden size
				insert.addBatch("REPLACE INTO professions_gardens "
						+ "VALUES ('" + uuid + "', '" + type + "', " + pgardens.get(type).getSize() + ");");
				
				// Save garden slots
				for (int slot = 0; slot < garden.getSize(); slot++) {
					GardenSlot gslot = garden.getSlots().get(slot);
					if (gslot != null) {
						int fid = gslot.getFertilizer() != null ? gslot.getFertilizer().getId() : -1;
						insert.addBatch("REPLACE INTO professions_gardenslots "
								+ "VALUES ('" + uuid + "', '" + type + "'," + slot + "," + gslot.getId() + "," +
								gslot.getEndTime() + "," + fid +");");
					}
					else {
						insert.addBatch("REPLACE INTO professions_gardenslots "
								+ "VALUES ('" + uuid + "', '" + type + "'," + slot + ",-1,-1,-1);");
					}
				}
			}
			
			if (gardenMsgs.get(p.getUniqueId()) != null) {
				for (BukkitTask task : gardenMsgs.get(p.getUniqueId())) {
					task.cancel();
				}
				gardenMsgs.remove(p.getUniqueId());
			}
		}
		catch (Exception e) {
			Bukkit.getLogger().log(Level.WARNING, "Professions failed to save gardens for user " + p.getName());
			e.printStackTrace();
		}
	}
	
	public static void addGardenMessage(UUID uuid, GardenSlot gs) {
		BukkitTask task = new BukkitRunnable() {
			public void run() {
				Player online = Bukkit.getPlayer(uuid);
				if (online != null) {
					online.sendMessage("§4[§c§lMLMC§4] §7Your " + StorageManager.getItem(gs.getId()).getDisplay() + " §7can now be harvested!");
				}
			}
		}.runTaskLater(main, gs.getTicksRemaining());
		
		if (gardenMsgs.containsKey(uuid)) {
			gardenMsgs.get(uuid).add(task);
		}
		else {
			ArrayList<BukkitTask> list = new ArrayList<BukkitTask>();
			list.add(task);
			gardenMsgs.put(uuid, list);
		}
	}
	
	@Override
	public void cleanup(Statement insert, Statement delete) {
		if (!Professions.isInstance) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				savePlayer(p, insert, delete);
			}
		}
	}
	
	public static Garden getGarden(Player p, ProfessionType type) {
		return gardens.get(p.getUniqueId()).get(type);
	}
	
	public static Fertilizer getFertilizer(int id) {
		return fertilizers.get(id);
	}
	
	@Override
	public String getKey() {
		return "GardenManager";
	}
	
	public static void addPlayerGardening(Player p, ProfessionType type) {
		playersGardening.put(p.getUniqueId(), type);
	}
	
	public static void removePlayerGardening(Player p) {
		playersGardening.remove(p.getUniqueId());
	}
	
	public static void tryReturnToGarden(Player p, ProfessionType type) {
		if(playersGardening.get(p.getUniqueId()) == type) {
			removePlayerGardening(p);
			new GardenInventory(p, type);
		}
	}
}
