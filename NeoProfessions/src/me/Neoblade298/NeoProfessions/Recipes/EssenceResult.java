package me.Neoblade298.NeoProfessions.Recipes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Managers.CurrencyManager;

public class EssenceResult implements RecipeResult {
	int level, amount;

	public EssenceResult(String[] lineArgs) {
		this.amount = 1;
		this.level = 5;
		
		for (String arg : lineArgs) {
			if (arg.startsWith("level")) {
				this.level = Integer.parseInt(arg.substring(arg.indexOf(':') + 1));
			}
			else if (arg.startsWith("amount")) {
				this.amount = Integer.parseInt(arg.substring(arg.indexOf(':') + 1));
			}
		}
	}

	@Override
	public void giveResult(Player p, int amount) {
		CurrencyManager.add(p, level, this.amount * amount);
	}
	
	@Override
	public ItemStack getResultItem(Player p, boolean canCraft) {
		ItemStack item = new ItemStack(Material.QUARTZ);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName((canCraft ? "§a" : "§c") + "[Lv " + level + "] Essence");
		item.setItemMeta(meta);
		return item;
	}
	
	@Override
	public String getDisplay(Player p) {
		return "[Lv " + level + "] Essence";
	}
}
