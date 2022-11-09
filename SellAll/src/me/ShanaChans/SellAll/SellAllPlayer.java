package me.ShanaChans.SellAll;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.util.PaginatedList;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class SellAllPlayer 
{
	private HashMap<Material, Integer> itemSellCap = new HashMap<Material, Integer>();
	private HashMap<Material, Integer> itemAmountSold = new HashMap<Material, Integer>();
	
	public SellAllPlayer(HashMap<Material, Integer> itemAmountSold)
	{
		this.itemAmountSold = itemAmountSold;
	}
	
	public int getItemCap(Material mat) 
	{
		return itemSellCap.get(mat);
	}

	public void setItemCap(Material mat, int newSellCap) 
	{
		itemSellCap.replace(mat, newSellCap);
	}
	
	public void sellAll(Inventory inv, Player player, boolean isSelling)
	{
		HashMap<Material, Integer> itemAmount = new HashMap<Material, Integer>();
		HashMap<Material, Double> itemTotal = new HashMap<Material, Double>();
		
		if(!isSelling)
		{
			SellAllManager.getPlayerConfirmInv().put(player.getUniqueId(), inv);
		}
		
		double totalCost = 0;
		
		HashMap<Material, Integer> tempItemAmount = new HashMap<Material, Integer>();
		
    	for(ItemStack items : inv.getContents())
    	{
    		if(items != null && !items.hasItemMeta())
    		{
    			Material material = items.getType();	
        		if(SellAllManager.getItemPrices().containsKey(material))
        		{
        			tempItemAmount.put(material, tempItemAmount.getOrDefault(material, 0) + items.getAmount());
        		}
    		}
    	}
    	
    	for(Entry<Material, Integer> mat : tempItemAmount.entrySet())
    	{
    		totalCost += getTotalPrice(mat.getKey(), mat.getValue(), player, itemAmount, itemTotal, inv, isSelling);
    	}
    	
    	
    	if(totalCost == 0)
    	{
    		player.sendMessage("§6No items to be sold.");
    	}
    	else
    	{
    		if(!isSelling)
    		{
    			getSellLog(false, player, itemAmount, itemTotal, totalCost);
    		}
    		else
    		{
    			getSellLog(true, player, itemAmount, itemTotal, totalCost);
    			NeoCore.getEconomy().depositPlayer(player, totalCost);
    		}
    	}
    }
	
	public void getSellLog(boolean sell, Player player, HashMap<Material, Integer> itemAmount, HashMap<Material, Double> itemTotal, double totalCost)
	{
		ComponentBuilder builder = new ComponentBuilder(sell ? "§6[Hover For Sell Log]" : "§6[Click to Confirm]");
		DecimalFormat df = new DecimalFormat("0.00");
		String text = "";
		text = text.concat("§c§oRed§7§o item amounts = reduced item value & over soft cap\n");
    	for(Material mat : itemAmount.keySet())
    	{
    		if(itemAmountSold.getOrDefault(mat, 0) > SellAllManager.getItemCaps().get(mat))
    		{
    			text = text.concat("§6" + mat.name() + " §c(" + itemAmount.get(mat) + "x)§7 - " + "§e" + df.format(itemTotal.get(mat)) + "g\n");
    		}
    		else
    		{
    			text = text.concat("§6" + mat.name() + " §7(" + itemAmount.get(mat) + "x) - " + "§e" + df.format(itemTotal.get(mat)) + "g\n");
    		}
    	}	
    	text = text.concat("§7TOTAL - §e" + df.format(totalCost) + "g");
    	if(sell)
    	{
    		builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(text)));
    	}
    	else
    	{
    		builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(text))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sellall confirm"));
    	}
    	player.spigot().sendMessage(builder.create());
	}
	
	public double getTotalPrice(Material mat, int amount, Player player, HashMap<Material, Integer> itemAmount, HashMap<Material, Double> itemTotal, Inventory inv, boolean isSelling)
	{	
		int cap = SellAllManager.getItemCaps().get(mat);
		int tierAmount = SellAllManager.getTierAmount();
		double tierMultiplier = SellAllManager.getTierMultiplier();
		double tierPriceMultiplier = SellAllManager.getTierPriceMultiplier();
		
		int currentSold;
		currentSold = itemAmountSold.getOrDefault(mat,0);
	
		HashMap<Integer, Integer> tierLimits = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> soldPerTier = new HashMap<Integer, Integer>();
		
		for(int i = 1; i <= tierAmount + 1; i++)
		{
			if(i == 1)
			{
				tierLimits.put(i, cap);
			}
			else
			{
				tierLimits.put(i, (int) Math.round((i - 1) * tierMultiplier * cap));
			}
		}
		
		int i = 1;
		
		while(i <= tierAmount && currentSold >= tierLimits.get(i))
		{
			i++;
		}
		
		int limit = tierLimits.get(i);
		
		if(currentSold > limit)
		{
			return 0;
		}
		
		if(amount <= 0)
		{
			return 0;
		}
		
		int remainingInTier = limit - currentSold;
		
		if(remainingInTier >= amount)
		{
			soldPerTier.put(i, amount);
			currentSold += amount;
		}
		else
		{
			soldPerTier.put(i, remainingInTier);
			amount -= remainingInTier;
			currentSold += remainingInTier;
		}
		
		double sellMultiplier = SellAllManager.getMultiplier(player);
		double sellBooster = SellAllManager.getBooster(player);
		double sellPriceModifier = (sellMultiplier - 1) + (sellBooster - 1) + 1;
		double price = 0;
		int itemSold = 0;
		
		for(Entry<Integer, Integer> e : soldPerTier.entrySet())
		{
			int amountSold = e.getValue();
			price += Math.pow(tierPriceMultiplier, e.getKey() - 1) * amountSold * sellPriceModifier * SellAllManager.getItemPrices().get(mat);
			itemSold += amountSold;
		}
		if(isSelling)
		{
			itemAmountSold.put(mat, itemAmountSold.getOrDefault(mat,0) + (int) Math.min(tierAmount * tierMultiplier * cap, itemSold));
			itemAmount.put(mat, itemAmount.getOrDefault(mat,0) + (int) Math.min(tierAmount * tierMultiplier * cap, itemSold));
			itemTotal.put(mat, itemTotal.getOrDefault(mat, 0.0) + price);
			inv.removeItem(new ItemStack(mat, itemSold));
		}
		else
		{
			itemAmount.put(mat, itemAmount.getOrDefault(mat, 0) + (int) Math.min(tierAmount * tierMultiplier * cap, itemSold));
			itemTotal.put(mat, itemTotal.getOrDefault(mat, 0.0) + price);
		}
		return price;
	}
	
	/**
	 * Lists out the players personal item caps
	 * @param player
	 */
	public void getSellCap(Player player, Player displayPlayer, int pageNumber, TreeMap<Material, Double> sort)
	{
		PaginatedList<String> list = new PaginatedList<String>();
		for(Material mat : sort.keySet())
		{
			list.add("§7" + mat.name() + ": " + itemAmountSold.getOrDefault(mat, 0) + " / " + SellAllManager.getItemCaps().get(mat));
		}
		if(-1 < pageNumber && pageNumber < list.pages())
		{
			player.sendMessage("§6O---={ " + displayPlayer.getName() + "'s Sell Soft Limits }=---O");
			player.sendMessage("§eWarning: Going over the soft cap will cause item values to be");
			player.sendMessage("§ediminished. At hard cap can no longer sell.");
			for(String output : list.get(pageNumber))
			{
				player.sendMessage(output);
			}
			String nextPage ="/sellall cap " + displayPlayer.getName() + " " + (pageNumber + 2); 
			String prevPage = "/sellall cap " + displayPlayer.getName() + " " + (pageNumber); 
			list.displayFooter(player, pageNumber, nextPage, prevPage);
			return;
		}
		player.sendMessage("§7Invalid page");
	}
	
	public void getValue(Player p)
	{
		ItemStack item = p.getInventory().getItemInMainHand();
        
        if (item == null || item.getType().isAir()) 
        {
            p.sendMessage("§6You're not holding anything!");
            return;
        }
        
        NBTItem nbti = new NBTItem(item);
        double value = 0;
        
        if (!nbti.getString("value").isBlank()) 
        {
            value = Double.parseDouble(nbti.getString("value"));
        }
        else 
        {
            value = nbti.getDouble("value");
        }
        
        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name();
        
        if(value == 0)
        {
        	if(SellAllManager.getItemPrices().containsKey(item.getType()))
        	{
        		name = item.getType().name();
        		value = SellAllManager.getItemPrices().get(item.getType());
        	}
        	else
        	{
        		p.sendMessage("§6This item does not have a price!");
        		return;
        	}
        }
        
        p.sendMessage("§6Value of §7" + name + "§7: §e" + value + "g");
        if(SellAllManager.getItemPrices().containsKey(item.getType()))
    	{
            p.sendMessage("§7Base Limit§7: §e" + (int)(SellAllManager.getItemCaps().get(item.getType())) + " §7Multiplier: §e1.0x");
        	for(int i=1; i <= SellAllManager.getTierAmount(); i++)
        	{
        		p.sendMessage("§7Tier " + i + " Limit§7: §e" + (int)(SellAllManager.getItemCaps().get(item.getType()) * SellAllManager.getTierMultiplier() * i) + " §7Multiplier: §e" + Math.pow(SellAllManager.getTierPriceMultiplier(), i) + "x");
        	}
 
            p.sendMessage("§6Current Amount Sold§7: §e" + itemAmountSold.getOrDefault(item.getType(), 0));
    	}
        
	}
	
	/**
	 * Sets a players sold amount for a material to a new value between -1 and the cap
	 * @param player
	 * @param mat
	 * @param newAmount
	 */
	public void setSold(Player player, Material mat, int newAmount)
	{
		if(itemAmountSold.containsKey(mat))
		{
			if(-1 < newAmount && newAmount < (itemSellCap.getOrDefault(mat, SellAllManager.getItemCaps().get(mat)) + 1))
			{
				itemAmountSold.put(mat, newAmount);
				player.sendMessage("§6Changed sold amount!");
			}
		}
	}

	public HashMap<Material, Integer> getItemAmountSold() {
		return itemAmountSold;
	}
	
	public void resetSold()
	{
		for(Material mat : itemAmountSold.keySet())
		{
			itemAmountSold.put(mat, 0);
		}
	}
}