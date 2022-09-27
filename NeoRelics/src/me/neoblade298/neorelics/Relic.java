package me.neoblade298.neorelics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import me.neoblade298.neocore.info.BossInfo;
import me.neoblade298.neocore.info.InfoAPI;
import me.neoblade298.neocore.util.Util;

public class Relic {
	private HashMap<Integer, SetEffect> effects = new HashMap<Integer, SetEffect>();
	private BossInfo bi;
	private String key, display = "Incorrectly loaded";
	private ArrayList<String> lore;
	private int level = 1;
	
	public Relic(ConfigurationSection cfg) {
		this.key = cfg.getName();
		this.lore = parseLore(cfg.getStringList("lore"));
		
		String bossname = this.key.substring(5); // Remove Relic from key
		bi = InfoAPI.getBossInfo(bossname);
		if (bi == null) {
			Bukkit.getLogger().warning("[NeoRelics] Failed to load relic " + key + ", boss info for " + bossname + " was null");
		}
		else {
			this.level = bi.getLevel(true);
			this.display = "§4[Lv " + level + "] §cRelic of " + bi.getDisplay();
		}
		
		ConfigurationSection effs = cfg.getConfigurationSection("effects");
		for (String numString : effs.getKeys(false)) {
			ConfigurationSection currentSetNum = effs.getConfigurationSection(numString);
			int num = Integer.parseInt(numString);
			String flag = currentSetNum.getString("flag");
			ArrayList<String> attrs = (ArrayList<String>) currentSetNum.getStringList("attributes");
			HashMap<String, Integer> attrMap = new HashMap<String, Integer>();
			for (String attr : attrs) {
				String[] attrSplit = attr.split(":");
				attrMap.put(attrSplit[0], Integer.parseInt(attrSplit[1]));
			}
			SetEffect eff = new SetEffect(attrMap, flag);
			effects.put(num, eff);
		}
	}
	
	private ArrayList<String> parseLore(List<String> cfg) {
		ArrayList<String> lore = new ArrayList<String>();
		for (String line : cfg) {
			lore.add(Util.translateColors(line));
		}
		return lore;
	}
	
	public void applyEffects(Player p, int previous, int current) {
		// Add the effects that weren't previously there
		if (current - previous > 0) {
			for (int num : this.effects.keySet()) {
				if (num > previous && num <= current) {
					this.effects.get(num).applyEffects(p);
				}
			}
		}
		// Remove the effects that were previously there
		else {
			for (int num : this.effects.keySet()) {
				if (num > current && num <= previous) {
					this.effects.get(num).removeEffects(p);
				}
			}
		}
	}
	
	public ItemStack getItem() {
		ItemStack item = new ItemStack(Material.QUARTZ);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		meta.setDisplayName(display);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addEnchant(Enchantment.DURABILITY, 1, false);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("augment", key);
		nbti.setInteger("level", level);
		return nbti.getItem();
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getDisplay() {
		return this.display;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public ArrayList<String> getLore() {
		return this.lore;
	}
	
	public HashMap<Integer, SetEffect> getEffects() {
		return effects;
	}
	
	public BossInfo getBossInfo() {
		return bi;
	}
}
