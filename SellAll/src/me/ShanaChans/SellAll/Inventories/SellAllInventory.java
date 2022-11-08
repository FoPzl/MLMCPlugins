package me.ShanaChans.SellAll.Inventories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ShanaChans.SellAll.SellAllManager;

public class SellAllInventory extends CustomInventory
{
	private final int SELL_ICON = 8;
	private final int MENU_MODEL = 5000;
	private Inventory inv;
	private boolean sold = false;
	
	public SellAllInventory(Player p) 
	{
		inv = Bukkit.createInventory(p, 54, "§cPlace items here to sell");
		ItemStack[] contents = inv.getContents();
		contents[SELL_ICON] = createGuiItem(Material.LIME_CONCRETE, "§aConfirm Sell", "§7You can check the value of", "§7item with §c/value");
		inv.setContents(contents);
		
		setupInventory(p, inv, this);
	}

	protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.setLore(Arrays.asList(lore));
		meta.setDisplayName(name);
		meta.setCustomModelData(MENU_MODEL);
		item.setItemMeta(meta);
		return item;
	}
	
	public void handleInventoryClick(final InventoryClickEvent e) {
		if (!sold && e.getClickedInventory() == inv && e.getRawSlot() == SELL_ICON) {
			e.setCancelled(true);
			Player player = (Player) e.getWhoClicked();
			SellAllManager.getPlayers().get(player.getUniqueId()).sellAll(inv, player, true);
		}
		
		else if (sold) {
			if (isUntouchable(e.getCurrentItem())) {
				e.setCancelled(true);
			}
		}
	}
	
	public void handleInventoryDrag(final InventoryDragEvent e) {
		if (sold) {
			e.setCancelled(true);
		}
	}
	
	public void handleInventoryClose(final InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (e.getInventory() == inv) {
			for (ItemStack item : inv.getContents()) {
				if (item == null || isUntouchable(item)) {
					continue;
				}
				
				HashMap<Integer, ItemStack> failed = p.getInventory().addItem(item);
				for (Entry<Integer, ItemStack> entry : failed.entrySet()) {
					p.getWorld().dropItem(p.getLocation(), entry.getValue());
				}
			}
		}
	}
	
	public boolean isUntouchable(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData() &&
				item.getItemMeta().getCustomModelData() == MENU_MODEL;
	}

}
