package me.Neoblade298.NeoProfessions.Inventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoProfessions.Gardens.Garden;
import me.Neoblade298.NeoProfessions.Gardens.GardenSlot;
import me.Neoblade298.NeoProfessions.Managers.GardenManager;
import me.Neoblade298.NeoProfessions.PlayerProfessions.ProfessionType;
import me.Neoblade298.NeoProfessions.Utilities.SkullCreator;

public class GardenInventory extends ProfessionInventory {
	
	private Garden garden;
	private static final int HOME_SLOT = 49;
	public static final int EMPTY = 0, IMMATURE = 1, MATURE = 2;
	private Player p;
	private ProfessionType type;

	public GardenInventory(Player p, ProfessionType type) {
		this.p = p;
		this.type = type;
		String display;
		switch (type) {
		case HARVESTER: display = "§9Your Garden";
		break;
		case LOGGER: display = "§9Your Arbor";
		break;
		case STONECUTTER: display = "§9Your Quarry";
		default: display = "§9Your Area";
		break;
		}
		garden = GardenManager.getGarden(p, type);
		
		inv = Bukkit.createInventory(p, 54, display);

		inv.setContents(setupInventory(inv.getContents()));
		
		new BukkitRunnable() {
			public void run() {
				if (p.getOpenInventory().getTopInventory() != inv) {
					this.cancel();
				}
				
				ItemStack[] contents = inv.getContents();
				for (int i = 0; i < garden.getSize(); i++) {
					contents[i] = updateIcon(i, contents[i]);
				}
				inv.setContents(contents);
			}
		}.runTaskTimer(GardenManager.main, 40L, 40L);

		setupInventory(p, inv, this);
	}
	
	private ItemStack updateIcon(int slot, ItemStack item) {
		NBTItem nbti = new NBTItem(item);
		if (nbti.getInteger("type") == IMMATURE) {
			GardenSlot gslot = garden.getSlots().get(slot);
			if (gslot.getEndTime() <= System.currentTimeMillis()) {
				nbti.setInteger("type", MATURE);
				return gslot.getIcon();
			}
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			lore.set(0, gslot.getTimerLine());
			lore.add("§cRight click to uproot!");
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	private ItemStack[] setupInventory(ItemStack[] contents) {
		HashMap<Integer, GardenSlot> slots = garden.getSlots();
		for (int i = 0; i < garden.getSize(); i++) {
			if (slots.containsKey(i)) {
				contents[i] = slots.get(i).getIcon();
			}
			else {
				contents[i] = createEmptySlot();
			}
		}
		
		contents[HOME_SLOT] = createHomeButton();
		return contents;
	}
	
	private ItemStack createEmptySlot() {
		ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§aEmpty Plot");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Left click to plant something!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setInteger("type", EMPTY);
		return nbti.getItem();
	}
	
	private ItemStack createHomeButton() {
		ItemStack item = SkullCreator.itemFromBase64(RecipeView.HOUSE_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Back to Gardens");
		item.setItemMeta(meta);
		return item;
	}

	// Check for clicks on items
	public void handleInventoryClick(final InventoryClickEvent e) {
		if (e.getInventory() != inv)
			return;

		e.setCancelled(true);
		final ItemStack item = e.getCurrentItem();
		// verify current item is not null
		if (item == null || item.getType().isAir()) {
			return;
		}
		
		int slot = e.getRawSlot();
		if (slot == HOME_SLOT) {
			new GardenSelectInventory(p);
			return;
		}
		
		if (slot < 45) {
			NBTItem nbti = new NBTItem(item);
			if (nbti.getInteger("type") == EMPTY) {
				new GardenChooseSeedView(p, type, -1, -1, slot);
				return;
			}
			else if (nbti.getInteger("type") == MATURE) {
				GardenManager.addPlayerGardening(p, type);
				GardenManager.getGarden(p, type).harvestSeed(p, slot);
				return;
			}
			else if (nbti.getInteger("type") == IMMATURE && e.getClick() == ClickType.RIGHT){
				GardenManager.getGarden(p, type).uprootSeed(p, slot);
				return;
			}
		}
	}

	// Cancel dragging in this inventory
	public void handleInventoryDrag(final InventoryDragEvent e) {
		if (e.getInventory().equals(inv)) {
			e.setCancelled(true);
		}
	}
	
	public void handleInventoryClose(final InventoryCloseEvent e) {
	}
}
