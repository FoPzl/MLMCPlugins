package me.Neoblade298.NeoConsumables;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;

public class Items {
	public static ItemStack getBossChestToken(Player p, long timestamp) {
		ItemStack item = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§cBoss Chest Token");
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Bound to §e" + p.getName());
		lore.add("§7Expires §c24h §7after it's received");
		lore.add("§6Right click to guarantee a boss");
		lore.add("§6chest next time you defeat a boss.");
		lore.add("§cDoes not stack its effect");
		
		meta.setLore(lore);
		meta.addEnchant(Enchantment.LUCK, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setCustomModelData(101);
		
		item.setItemMeta(meta);
		
		NBTItem nbti = new NBTItem(item);
		nbti.setLong("timestamp", timestamp);
		nbti.setString("player", p.getName());
		
		return nbti.getItem();
	}
}
