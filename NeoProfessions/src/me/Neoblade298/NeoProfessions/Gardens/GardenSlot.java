package me.Neoblade298.NeoProfessions.Gardens;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoProfessions.Inventories.GardenInventory;
import me.Neoblade298.NeoProfessions.Managers.StorageManager;
import me.Neoblade298.NeoProfessions.Storage.StoredItem;

public class GardenSlot {
	int id;
	Fertilizer fertilizer;
	long endTime;
	
	public GardenSlot(int id, Fertilizer fertilizer, long endTime) {
		this.id = id;
		this.fertilizer = fertilizer;
		this.endTime = endTime;
	}

	public int getId() {
		return id;
	}

	public Fertilizer getFertilizer() {
		return fertilizer;
	}

	public long getEndTime() {
		return endTime;
	}
	
	public ItemStack getIcon() {
		boolean isComplete = endTime <= System.currentTimeMillis();
		StoredItem si = StorageManager.getItem(id);
		ItemStack item = isComplete ? si.getItem() : new ItemStack(Material.PUMPKIN_SEEDS);
		ItemMeta meta = item.getItemMeta();
		String display = isComplete ? "§a" : "§c";
		display += ChatColor.stripColor(si.getDisplay());
		meta.setDisplayName(display);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(getTimerLine());
		
		if (fertilizer != null) {
			lore.add("§6Fertilized:");
			for (String line : fertilizer.getEffects()) {
				lore.add(line);
			}
		}
		if (!isComplete) lore.add("§cRight click to uproot!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setInteger("type", isComplete ? GardenInventory.MATURE : GardenInventory.IMMATURE);
		return nbti.getItem();
	}
	
	public String getTimerLine() {
		if (endTime <= System.currentTimeMillis()) {
			return "§aCan be harvested";
		}
		int time = (int) ((endTime - System.currentTimeMillis()) / 1000); // Remaining time in seconds
		int minutes = time / 60;
		int seconds = time % 60;
		return "§cTime to harvest: " + String.format("§c%d:%02d", minutes, seconds);
	}
	
	public boolean canHarvest() {
		return endTime <= System.currentTimeMillis();
	}
	
	public long getTicksRemaining() {
		return (endTime - System.currentTimeMillis()) / 50; // Remaining time in ticks
	}
}
