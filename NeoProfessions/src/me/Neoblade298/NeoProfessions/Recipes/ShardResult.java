package me.Neoblade298.NeoProfessions.Recipes;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.neoblade298.neogear.Gear;
import me.neoblade298.neogear.objects.Rarity;
import me.neoblade298.neogear.objects.Shards;
import net.md_5.bungee.api.ChatColor;

public class ShardResult implements RecipeResult {
	String type;
	Rarity rarity;
	int level;
	String display;

	public ShardResult(String[] lineArgs) {
		this.type = "level";
		this.rarity = Gear.getRarities().get("common");
		this.level = 5;
		
		for (String lineArg : lineArgs) {
			String[] args = lineArg.split(":");
			if (args[0].equalsIgnoreCase("type")) {
				this.type = args[1];
			}
			else if (args[0].equalsIgnoreCase("rarity")) {
				this.rarity = Gear.getRarities().get(args[1].toLowerCase());
			}
			else if (args[0].equalsIgnoreCase("level")) {
				this.level = Integer.parseInt(args[1]);
			}
		}
		
		if (this.type.equalsIgnoreCase("level")) {
			display = Shards.getLevelShard(level).getItemMeta().getDisplayName();
		}
		else {
			Shards.getRarityShard(rarity, level).getItemMeta().getDisplayName();
		}
	}

	@Override
	public void giveResult(Player p, int amount) {
		ItemStack item = this.type.equalsIgnoreCase("level") ? Shards.getLevelShard(level) : Shards.getRarityShard(rarity, level);
		HashMap<Integer, ItemStack> failed = p.getInventory().addItem(item);
		for (Integer i : failed.keySet()) {
			p.getWorld().dropItem(p.getLocation(), failed.get(i));
		}
	}
	
	@Override
	public ItemStack getResultItem(Player p, boolean canCraft) {
		ItemStack item = this.type.equalsIgnoreCase("level") ? Shards.getLevelShard(level) : Shards.getRarityShard(rarity, level);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName((canCraft ? "§a" : "§c") + ChatColor.stripColor(meta.getDisplayName()));
		item.setItemMeta(meta);
		return item;
	}
	
	@Override
	public String getDisplay(Player p) {
		return display;
	}
}
