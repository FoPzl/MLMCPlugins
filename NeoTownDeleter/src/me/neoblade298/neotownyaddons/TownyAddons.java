package me.neoblade298.neotownyaddons;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.BonusBlockPurchaseCostCalculationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class TownyAddons extends JavaPlugin implements org.bukkit.event.Listener {
	
	public List<Town> deletableTowns = new ArrayList<Town>();
	public boolean debug = false;
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("NeoTownyAddons Enabled");
		getServer().getPluginManager().registerEvents(this, this);
		
	    // Get command listener
	    this.getCommand("tdelete").setExecutor(new Commands(this));
	    
	    // On enable, sweep after 2 minutes
		BukkitRunnable sweep = new BukkitRunnable() {
			public void run() {
				sweepTowns();
			}
		};
		sweep.runTaskLater(this, 2400L);
		
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoTownyAddons Disabled");
	    super.onDisable();
	}
	
	// Automatic version (Not a command)
	public boolean checkTownInactive(Town town) {
		try {
			// 1. Check mayor inactivity
			if (checkPlayerInactive(town.getMayor())) {
				// 2. Check less than 100 residents
				if (town.getNumResidents() < 100) {
					// 3. Check less than 1000 claimed plots
					if (town.getTownBlocks().size() < 1000) {
						// 4. Check if owned by NPC
						if (town.hasMayor() && !town.getMayor().isNPC()) {
							// 5. Check if all residents of rank assistant, advisor are inactive
							List<Resident> residents = town.getResidents();
							for (Resident res : residents) {
								List<String> ranks = res.getTownRanks();
								if (ranks.contains("assistant") ||
									ranks.contains("advisor")) {
									if (!checkPlayerInactive(res)) {
										return false;
									}
								}
							}
							return true;
						}
					}
				}
			}
		}
		catch (Exception e) {
			getServer().getLogger().log(Level.WARNING, "[NeoTownDeleter] Could not check if town: " + town.getName() + " was inactive.");
			return false;
		}
		return false;
	}
	
	// Command version (Command error included)
	public boolean checkTownInactive(Town town, Player p) {
		// 1. Check mayor inactivity
		if (checkPlayerInactive(town.getMayor())) {
			// 2. Check less than 100 residents
			if (town.getNumResidents() < 100) {
				// 3. Check less than 1000 claimed plots
				if (town.getTownBlocks().size() < 1000) {
					// 4. Check if owned by NPC
					if (!town.getMayor().isNPC()) {
						// 5. Check if all residents of rank assistant, advisor are inactive
						List<Resident> residents = town.getResidents();
						for (Resident res : residents) {
							List<String> ranks = res.getTownRanks();
							if (ranks.contains("assistant")) {
								if (!checkPlayerInactive(res)) {
									sendError(p, "§e" + res.getName() + " §7(Assistant) is not inactive.");
									return false;
								}
							}
							else if (ranks.contains("advisor")) {
								if (!checkPlayerInactive(res)) {
									sendError(p, "§e" + res.getName() + " §7(Advisor) is not inactive.");
									return false;
								}
							}
						}
						return true;
					}
					else {
						sendError(p, "Mayor is an NPC.");
					}
				}
				else {
					sendError(p, "Town has at least 1000 plots.");
				}
			}
			else {
				sendError(p, "Town has at least 100 residents.");
			}
		}
		else {
			sendError(p, "§e" + town.getMayor() + " §7(Mayor) is not inactive.");
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void sweepTowns() {
		deletableTowns.clear();
		List<Town> towns = TownyAPI.getInstance().getDataSource().getTowns();
		for (Town town : towns) {
			if (debug) {
				Bukkit.getServer().getLogger().info("Checking if " + town.getName() + " can be deleted");
			}
			// Make list of towns that can be deleted
			if (checkTownInactive(town)) {
				deletableTowns.add(town);
			}
		}
		
		// If there are less than 10 towns that can be deleted, delete them; otherwise wait for confirmation
		Bukkit.getServer().getLogger().info("Found " + deletableTowns.size() + " deletable towns");
		if (debug) {
			for (Town town : deletableTowns) {
				Bukkit.getServer().getLogger().info(town.getName() + " can be deleted");
			}
		}
		if (deletableTowns.size() < 10) {
			deleteTowns();
		}
	}
	
	public void deleteTowns() {
		for (Town town : deletableTowns) {
		    org.bukkit.Bukkit.getServer().getLogger().info("NeoTownDeleter deleted " + town.getName());
			Bukkit.broadcastMessage("§bThe town of " + town.getName() + " fell into ruin due to inactivity!");
			town.getAccount().setBalance(0, "Town deleted");
			TownyAPI.getInstance().getDataSource().removeTown(town);
		}
		deletableTowns.clear();
	}
	
	public void sendError(Player p, String msg) {
		String error = "§4[§c§lMLMC§4] §7Town not inactive: " + msg;
		p.sendMessage(error);
	}
	
	
	public boolean checkPlayerInactive(Resident res) {
		if (res == null) return true;
		long threshold = res.getLastOnline() + 2592000000L;
		return System.currentTimeMillis() > threshold;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		BukkitRunnable msg = new BukkitRunnable() {
			public void run() {
				if (p.hasPermission("tdeleter.admin")) {
					if (deletableTowns.size() > 0) {
						String msg = "§4[§c§lMLMC§4] §7The following towns can be deleted: §e";
						for (Town town : deletableTowns) {
							msg += town.getName() + " ";
						}
						p.sendMessage(msg);
						p.sendMessage("§4[§c§lMLMC§4] §7Type §e/tdelete confirm §7to delete all listed towns.");
					}
				}
			}
		};
		msg.runTaskLater(this, 60L);
	}
	
	@EventHandler
	public void onBonusBlockCostCalculation(BonusBlockPurchaseCostCalculationEvent e) {
		int blocks = e.getAlreadyPurchasedBlocksAmount();
		int numPurchased = e.getAmountOfPurchasingBlocksRequest();
		double cost = 0;
		for (int i = 0; i < numPurchased; i++) {
			cost += getBonusBlockPrice(blocks + i);
		}
		e.setPrice(cost);
	}
	
	private double getBonusBlockPrice(int previousBlocks) {
		return 1000 + (2 * previousBlocks) + (Math.pow(previousBlocks, 2) / 200);
	}

	@EventHandler
	public void onTownCreate(NewTownEvent e) {
		e.getTown().getAccount().deposit(500, "Starter money");
	}
}
