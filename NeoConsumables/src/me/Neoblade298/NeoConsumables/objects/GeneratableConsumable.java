package me.Neoblade298.NeoConsumables.objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface GeneratableConsumable {
	public ItemStack getItem(Player p, int amount);
	public String getDisplay();
}
