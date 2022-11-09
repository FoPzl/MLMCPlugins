package me.Neoblade298.NeoProfessions.Recipes;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.neoblade298.neogear.Gear;
import me.neoblade298.neogear.objects.GearConfig;
import net.md_5.bungee.api.ChatColor;

public class GearResult implements RecipeResult {
	String type;
	String rarity;
	int level;
	String display;

	public GearResult(String[] lineArgs) {
		this.type = "sword";
		this.rarity = "common";
		this.level = 5;
		
		for (String lineArg : lineArgs) {
			String[] args = lineArg.split(":");
			if (args[0].equalsIgnoreCase("type")) {
				this.type = args[1];
			}
			else if (args[0].equalsIgnoreCase("rarity")) {
				this.rarity = args[1];
			}
			else if (args[0].equalsIgnoreCase("level")) {
				this.level = Integer.parseInt(args[1]);
			}
		}
		
		display = Gear.getGearConfig(type, level).generateItem(rarity, level).getItemMeta().getDisplayName();
	}

	@Override
	public void giveResult(Player p, int amount) {
		HashMap<Integer, ItemStack> failed = p.getInventory().addItem(Gear.getGearConfig(type, level).generateItem(rarity, level));
		for (Integer i : failed.keySet()) {
			p.getWorld().dropItem(p.getLocation(), failed.get(i));
		}
	}
	
	@Override
	public ItemStack getResultItem(Player p, boolean canCraft) {
		ItemStack item = Gear.getGearConfig(type, level).generateItem(rarity, level);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName((canCraft ? "§a" : "§c") + ChatColor.stripColor(meta.getDisplayName()));
		item.setItemMeta(meta);
		return item;
	}
	
	@Override
	public String getDisplay(Player p) {
		return display;
	}
	
	public GearConfig getConfig() {
		return Gear.getGearConfig(type, level);
	}
}
