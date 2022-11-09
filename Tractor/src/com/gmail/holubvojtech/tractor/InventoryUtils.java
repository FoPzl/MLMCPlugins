package com.gmail.holubvojtech.tractor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {
	public static int countItem(Inventory inv, Material mat) {
		int found = 0;
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack is = inv.getItem(i);
			if ((is != null) && (is.getType() == mat)) {
				found += is.getAmount();
			}
		}
		return found;
	}

	public static void subtract(Inventory inv, Material mat, int left, Player player) {
		boolean checked = false;
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack is = inv.getItem(i);
			if ((is != null) && (is.getType() == mat)) {
				if ((player.getInventory().getHeldItemSlot() != i) && (40 != i) && (!checked)) {
					left--;
					checked = true;
				}
				int amount = is.getAmount();
				if (amount <= left) {
					inv.setItem(i, null);
					if (player.getInventory().getHeldItemSlot() == i) {
						checked = true;
					}
				} else {
					is.setAmount(amount - left);
					inv.setItem(i, is);
				}
				left -= amount;
				if (left <= 0) {
					return;
				}
			}
		}
	}
}
