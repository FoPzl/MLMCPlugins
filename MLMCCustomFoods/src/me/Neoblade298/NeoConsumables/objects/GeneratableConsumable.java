package me.Neoblade298.NeoConsumables.objects;

import org.bukkit.inventory.ItemStack;

public interface GeneratableConsumable {
	public ItemStack getItem(int amount);
	public String getDisplay();
}
