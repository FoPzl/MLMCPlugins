package me.neoblade298.neoplaceholders.placeholders.other;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.util.FlagManager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class LordboardPlaceholders extends PlaceholderExpansion {
	
    @Override
    public boolean canRegister(){
        return Bukkit.getPluginManager().getPlugin("SkillAPI") != null;
    }
    
    @Override
    public boolean register(){
    	if (!canRegister()) return false;
    	return super.register();
    }

	@Override
	public String getAuthor() {
		return "Neoblade298";
	}
	
    @Override
    public boolean persist(){
        return true;
    }

	@Override
	public String getIdentifier() {
		return "lb";
	}

    @Override
    public String getRequiredPlugin(){
        return "SkillAPI";
    }
    
	@Override
	public String getVersion() {
		return "1.0.1";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) return "N/A";
		DecimalFormat df = new DecimalFormat("0");
		DecimalFormat df1k = new DecimalFormat("0.00k");
		DecimalFormat df10k = new DecimalFormat("00.0k");
		
		String args[] = identifier.split("_");
		if (!SkillAPI.isLoaded(p)) return "N/A";
		
		if (args[0].equalsIgnoreCase("health")) {
			String placeholder = "N/A";
			double health = p.getHealth();
			placeholder = "";
			// Check if cursed
			if (FlagManager.hasFlag(p, "curse")) {
				placeholder = "§8";
			}
			else if (FlagManager.hasFlag(p, "stun") || FlagManager.hasFlag(p, "root") || FlagManager.hasFlag(p, "silence")) {
				placeholder = "§b";
			}
			
			if (health >= 10000) {
				placeholder += df10k.format(health / 1000);
			}
			else if (health >= 1000) {
				placeholder += df1k.format(health / 1000);
			}
			else {
				placeholder += df.format(health);
			}
			return placeholder;
		}
		else if (args[0].equalsIgnoreCase("maxhealth")) {
			String placeholder = "N/A";
			double health = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			if (health >= 10000) {
				placeholder = df10k.format(health / 1000);
			}
			else if (health >= 1000) {
				placeholder = df1k.format(health / 1000);
			}
			else {
				placeholder = df.format(health);
			}
			return placeholder;
		}
		else if (args[0].equalsIgnoreCase("resourcename")) {
			PlayerClass pClass = SkillAPI.getPlayerData(p).getClass("class");
			if (pClass != null) {
				return pClass.getData().getManaName();
			}
			return "N/A";
		}
		else if (args[0].equalsIgnoreCase("resource")) {
			return df.format(SkillAPI.getPlayerData(p).getMana());
		}
		else if (args[0].equalsIgnoreCase("maxresource")) {
			return df.format(SkillAPI.getPlayerData(p).getMaxMana());
		}
		else if (args[0].equalsIgnoreCase("world")) {
			String world = p.getWorld().getName();
			if (world.startsWith("Aurum")) {
				return "Resource";
			}
			else if (world.equalsIgnoreCase("Aegis")) {
				return "Old Towny";
			}
			else if (world.equalsIgnoreCase("Eretras")) {
				return "New Towny";
			}
			return world;
		}
	 	return "Invalid placeholder";
	}
}
