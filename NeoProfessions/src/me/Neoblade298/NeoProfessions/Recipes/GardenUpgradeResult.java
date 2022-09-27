package me.Neoblade298.NeoProfessions.Recipes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Managers.GardenManager;
import me.Neoblade298.NeoProfessions.Objects.SkullCreator;
import me.Neoblade298.NeoProfessions.PlayerProfessions.ProfessionType;

public class GardenUpgradeResult implements RecipeResult {
	private static final String UPGRADE_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDlmNjEzZDIwYjQ2ZjBjYjg5ZWMzNzRmYWY0ZjQ3MzBmYTVkYzEwOTBhMGY1MmRmZmJjM2MwY2Q1OTQ2Mjg5NCJ9fX0=";
	ProfessionType type;
	int newSize;
	String display;

	public GardenUpgradeResult(String[] lineArgs) {
		this.type = ProfessionType.HARVESTER;
		this.newSize = 1;
		
		for (String arg : lineArgs) {
			if (arg.startsWith("size")) {
				newSize = Integer.parseInt(arg.substring(arg.indexOf(':') + 1));
			}
			else if (arg.startsWith("type")) {
				type = ProfessionType.valueOf(arg.substring(arg.indexOf(':') + 1).toUpperCase());
			}
		}
		
		// Add to source
		display = "Garden Upgrade";
	}

	@Override
	public void giveResult(Player p, int amount) {
		GardenManager.getGarden(p, type).setSize(newSize);
	}
	
	@Override
	public ItemStack getResultItem(Player p, boolean canCraft) {
		ItemStack item = SkullCreator.itemFromBase64(UPGRADE_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName((canCraft ? "§a" : "§c") + type.getDisplay() + " Garden Upgrade (" + newSize + ")");
		item.setItemMeta(meta);
		return item;
	}
	
	@Override
	public String getDisplay(Player p) {
		return display;
	}
}
