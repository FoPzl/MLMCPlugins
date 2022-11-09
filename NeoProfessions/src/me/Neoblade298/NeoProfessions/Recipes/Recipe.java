package me.Neoblade298.NeoProfessions.Recipes;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Events.ProfessionCraftSuccessEvent;
import me.Neoblade298.NeoProfessions.Managers.ProfessionManager;
import me.Neoblade298.NeoProfessions.Managers.StorageManager;
import me.Neoblade298.NeoProfessions.PlayerProfessions.ProfessionType;
import me.Neoblade298.NeoProfessions.Storage.StoredItemInstance;
import net.md_5.bungee.api.ChatColor;

public class Recipe {
	String key;
	String display;
	int level;
	int exp;
	ArrayList<RecipeRequirement> reqs;
	ArrayList<StoredItemInstance> components;
	RecipeResult result;
	boolean canMulticraft;
	private static float ERROR = 0.594604F;
	
	
	public Recipe(String key, String display, int exp, int level, ArrayList<RecipeRequirement> reqs,
			ArrayList<StoredItemInstance> components, RecipeResult result, boolean canMulticraft) {
		this.key = key;
		this.display = display;
		this.level = level;
		this.reqs = reqs;
		this.components = components;
		this.result = result;
		this.canMulticraft = canMulticraft;
		
		if (exp == -1) {
			exp = 0;
			for (RecipeRequirement req : reqs) {
				if (req instanceof EssenceRequirement) {
					EssenceRequirement ereq = (EssenceRequirement) req;
					exp += ereq.level * ereq.amount;
				}
			}
			
			for (StoredItemInstance component : components) {
				exp += component.getItem().getDefaultExp();
			}
		}
		this.exp = exp;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public ArrayList<RecipeRequirement> getReqs() {
		return reqs;
	}
	public void setReqs(ArrayList<RecipeRequirement> reqs) {
		this.reqs = reqs;
	}
	public ArrayList<StoredItemInstance> getComponents() {
		return components;
	}
	public void setComponents(ArrayList<StoredItemInstance> components) {
		this.components = components;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public boolean craftRecipe(Player p, int amount) {
		// First make sure everything required is available
		for (RecipeRequirement req : reqs) {
			if (!req.passesReq(p, amount)) {
				p.sendMessage(req.failMessage(p));
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, ERROR);
				return false;
			}
		}
		for (StoredItemInstance component : components) {
			if (!StorageManager.playerHas(p, component.getItem().getId(), component.getAmount() * amount)) {
				p.sendMessage("§4[§c§lMLMC§4] §cYou lack the component: " + component.getItem().getDisplay());
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, ERROR);
				return false;
			}
		}
		
		// Take all things required
		for (RecipeRequirement req : reqs) {
			req.useReq(p, amount);
		}
		for (StoredItemInstance component : components) {
			StorageManager.takePlayer(p, component.getItem().getId(), component.getAmount() * amount);
		}
		
		result.giveResult(p, amount);
		ProfessionManager.getAccount(p.getUniqueId()).get(ProfessionType.CRAFTER).addExp(p, exp * amount);
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
		p.sendMessage("§4[§c§lMLMC§4] §7You successfully crafted: " + result.getDisplay(p));
		Bukkit.getPluginManager().callEvent(new ProfessionCraftSuccessEvent(p, this));
		return true;
	}
	
	public ItemStack getReqsIcon(Player p, boolean canCraft) {
		try {
			ItemStack item = result.getResultItem(p, canCraft);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(result.getResultItem(p, canCraft).getItemMeta().getDisplayName());
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("§6Requirements§7:");
			for (RecipeRequirement req : reqs) {
				lore.add(req.getLoreString(p));
			}
			lore.add("§6Components§7:");
			for (StoredItemInstance component : components) {
				int playerHas = StorageManager.getAmount(p, component.getItem().getId());
				String line = playerHas >= component.getAmount() ? "§a" : "§c";
				line += "- " + playerHas + " / " + component.getAmount() + " " + ChatColor.stripColor(component.getItem().getDisplay());
				lore.add(line);
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}
		catch (Exception e) {
			Bukkit.getLogger().warning("[NeoProfessions] Failed to load requirement icon for recipe: " + this.key);
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean canCraft(Player p) {
		for (RecipeRequirement req : reqs) {
			if (!req.passesReq(p, 1)) {
				return false;
			}
		}
		for (StoredItemInstance component : components) {
			if (!StorageManager.playerHas(p, component.getItem().getId(), component.getAmount())) {
				return false;
			}
		}
		return true;
	}
	
	public RecipeResult getResult() {
		return result;
	}
	
	public boolean canMulticraft() {
		return canMulticraft;
	}
}
