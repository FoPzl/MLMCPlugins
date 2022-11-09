package me.Neoblade298.NeoProfessions.Inventories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
import me.Neoblade298.NeoProfessions.Professions;
import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.ItemEditor;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;
import me.Neoblade298.NeoProfessions.Managers.CurrencyManager;
import me.Neoblade298.NeoProfessions.Utilities.Util;

public class InspectAugmentsInventory extends ProfessionInventory {
	private Player p;
	ItemStack item;
	ItemEditor editor;
	
	private static final int GOLD_COST = 2000;
	private static final int ESSENCE_COST = 3;

	public InspectAugmentsInventory(Professions main, Player p, ItemStack item) {
		this.item = item;
		this.p = p;
		this.editor = new ItemEditor(item);
		
		inv = Bukkit.createInventory(p, 9, "§cAugment Viewer");

		inv.addItem(item);
		NBTItem nbti = new NBTItem(item);
		ItemStack[] contents = inv.getContents();
		for (int i = 1; i < 9 - nbti.getInteger("slotsCreated"); i++) {
			contents[i] = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
		}
		
		int j = 1;
		int slot = nbti.getInteger("slotsCreated");
		for (int i = 9 - slot; i < 9; i++) {
			Augment oldAug = editor.getAugment(j);
			if (oldAug == null) {
				contents[i] = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, "§7Empty slot");
			}
			else {
				contents[i] = createAugment(oldAug, j);
			}
			j++;
		}
		inv.setContents(contents);

		setupInventory(p, inv, this);
	}

	protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	protected ItemStack createAugment(Augment aug, int slot) {
		ItemStack item = aug.getItem(p);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (aug.isPermanent()) {
			lore.add("§cThis augment cannot be unslotted.");
		}
		else {
			lore.add("§cShift left click to unslot.");
			lore.add("§cCosts §e" + GOLD_COST + "g §cand 3 Essence");
			lore.add("§cof the same level.");
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		if (!aug.isPermanent()) nbti.setInteger("slot", slot);
		return nbti.getItem();
	}

	protected ItemStack createGuiItem(final Material material, final String name, List<String> list, Augment aug) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(list);
		item.setItemMeta(meta);
		return item;
	}

	// Check for clicks on items
	public void handleInventoryClick(final InventoryClickEvent e) {
		if (e.getInventory() != inv)
			return;

		e.setCancelled(true);
		ItemStack item = e.getCurrentItem();
		if (item == null || item.getType().isAir()) {
			return;
		}
		
		if (!e.isShiftClick()) {
			return;
		}
		
		NBTItem nbti = new NBTItem(item);
		if (!nbti.hasKey("slot")) {
			return;
		}
		if (!Professions.econ.has(p, GOLD_COST)) {
			Util.sendMessage(p, "&cYou don't have enough gold to unslot!");
			return;
		}
		String aug = nbti.getString("augment");
		int level = nbti.getInteger("level");
		int slot = nbti.getInteger("slot");
		if (!CurrencyManager.hasEnough(p, level, ESSENCE_COST)) {
			Util.sendMessage(p, "&cYou don't have enough essence to unslot!");
			return;
		}

		String result = editor.unslotAugment(p, slot);
		if (result == null) {
			Util.sendMessage(p, "&7Successfully unslotted item!");
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
			Professions.econ.withdrawPlayer(p, GOLD_COST);
			CurrencyManager.subtract(p, level, ESSENCE_COST);
			Augment augment = AugmentManager.getFromCache(aug, level);
			HashMap<Integer, ItemStack> failed = p.getInventory().addItem(augment.getItem(p));
			if (!failed.isEmpty()) {
				for (Integer key : failed.keySet()) {
					p.getWorld().dropItem(p.getLocation(), failed.get(key));
				}
			}
			p.sendMessage("§4[§c§lMLMC§4] §7You successfully unslotted " + augment.getLine() + "§7!");
			p.closeInventory();
		}
		else {
			Util.sendMessage(p, "Could not unslot augment on slot " + slot + ", " + result);
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
