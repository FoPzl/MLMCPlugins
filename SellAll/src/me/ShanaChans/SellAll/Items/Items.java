package me.ShanaChans.SellAll.Items;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;

public class Items 
{
	public static ItemStack getChestSellStick() 
	{
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Sell Stick");
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Right click on chest to sell");
		
		meta.setLore(lore);
		meta.addEnchant(Enchantment.LUCK, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setCustomModelData(101);
		
		item.setItemMeta(meta);
		
		NBTItem nbti = new NBTItem(item);
		nbti.setBoolean("sellStick", true);
		
		return nbti.getItem();
	}

}
