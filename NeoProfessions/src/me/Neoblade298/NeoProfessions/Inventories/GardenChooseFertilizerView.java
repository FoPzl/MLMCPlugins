package me.Neoblade298.NeoProfessions.Inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoProfessions.Events.ProfessionPlantSeedEvent;
import me.Neoblade298.NeoProfessions.Gardens.Fertilizer;
import me.Neoblade298.NeoProfessions.Managers.GardenManager;
import me.Neoblade298.NeoProfessions.Managers.StorageManager;
import me.Neoblade298.NeoProfessions.PlayerProfessions.ProfessionType;
import me.Neoblade298.NeoProfessions.Storage.StoredItem;
import me.Neoblade298.NeoProfessions.Storage.StoredItemInstance;
import me.Neoblade298.NeoProfessions.Utilities.SkullCreator;

public class GardenChooseFertilizerView extends ProfessionInventory {
	
	private Player p;
	private ProfessionType type;
	private int min, max, id, slot;
	private static final int FERTILIZER_BASE = 4000, FERTILIZER_MAX = 4099,
			BACK_BUTTON = 45, SEED_ICON = 49;

	public GardenChooseFertilizerView(Player p, int id, int min, int max, int slot, ProfessionType type) {
		this.type = type;
		this.min = min;
		this.max = max;
		this.id = id;
		this.slot = slot;
		inv = Bukkit.createInventory(p, 54, "§9Choose a Fertilizer");
		this.p = p;

		ItemStack[] contents = inv.getContents();
		contents[BACK_BUTTON] = createBackButton();
		contents[SEED_ICON] = StorageManager.getItem(id).getBaseView(1);
		contents[0] = createNoFertilizerButton();
		inv.setContents(setupItems(contents));

		setupInventory(p, inv, this);
	}
	
	private ItemStack[] setupItems(ItemStack[] contents) {
		int count = 1;
		for (int i = FERTILIZER_BASE; StorageManager.getItem(i) != null && i <= FERTILIZER_MAX; i++) {
			StoredItem si = StorageManager.getItem(i);
			int amount = StorageManager.getAmount(p, i);
			if (amount > 0) {
				ItemStack item = new StoredItemInstance(si, amount).getStorageView(true);
				ItemMeta meta = item.getItemMeta();
				meta.setLore(GardenManager.getFertilizer(i).getEffects());
				item.setItemMeta(meta);
				NBTItem nbti = new NBTItem(item);
				nbti.setInteger("id", i);
				contents[count++] = nbti.getItem();
			}
		}
		return contents;
	}

	protected ItemStack createNoFertilizerButton() {
		ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Plant without Fertilizer");
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setInteger("id", -1);
		return nbti.getItem();
	}

	protected ItemStack createBackButton() {
		ItemStack item = SkullCreator.itemFromBase64(StorageView.PREV_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Return");
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
		final Player p = (Player) e.getWhoClicked();
		final int slot = e.getRawSlot();
		
		if (slot == BACK_BUTTON) {
			new GardenChooseSeedView(p, type, min, max, this.slot);
			return;
		}
		else if (slot < 45) {
			chooseFertilizer(slot);
		}
	}
	
	private void chooseFertilizer(int slot) { 
		NBTItem nbti = new NBTItem(inv.getContents()[slot]);
		int fertilizerId = nbti.getInteger("id");
		int seedLevel = StorageManager.getItem(id).getLevel();
		
		// Make sure the level of the fertilizer is high enough
		if (fertilizerId != -1 && StorageManager.getItem(fertilizerId).getLevel() < seedLevel) {
			p.sendMessage("§cThis fertilizer is not high enough level!");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, HarvestingMinigame.ERROR);
			return;
		}
		// Make sure the player has enough
		else if (!StorageManager.playerHas(p, id, 1)) {
			p.sendMessage("§cYou don't have enough of this seed!");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, HarvestingMinigame.ERROR);
			return;
		}
		else if (!StorageManager.playerHas(p, fertilizerId, 1) && fertilizerId != -1) {
			p.sendMessage("§cYou don't have enough of this fertilizer!");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, HarvestingMinigame.ERROR);
			return;
		}
		StorageManager.takePlayer(p, id, 1);
		if (fertilizerId != -1) {
			StorageManager.takePlayer(p, fertilizerId, 1);
		}
		Fertilizer fert = null;
		if (fertilizerId != -1) {
			fert = GardenManager.getFertilizer(fertilizerId);
		}
		GardenManager.getGarden(p, type).plantSeed(p.getUniqueId(), this.slot, id, fert);
		p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
		Bukkit.getPluginManager().callEvent(new ProfessionPlantSeedEvent(p, StorageManager.getItem(id), fert, GardenManager.getGarden(p, type)));
		new GardenInventory(p, type);
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
