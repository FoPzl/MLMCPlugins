package me.Neoblade298.NeoProfessions.Recipes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface RecipeResult {
	public void giveResult(Player p, int amount);
	public ItemStack getResultItem(Player p, boolean canCraft);
	public String getDisplay(Player p);
}
