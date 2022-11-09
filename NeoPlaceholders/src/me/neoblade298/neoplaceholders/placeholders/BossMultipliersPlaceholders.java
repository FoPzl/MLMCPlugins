package me.neoblade298.neoplaceholders.placeholders;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.info.BossInfo;
import me.neoblade298.neocore.info.InfoAPI;

public class BossMultipliersPlaceholders extends PlaceholderExpansion {
	private static DecimalFormat df = new DecimalFormat("##.##");


    @Override
    public boolean canRegister(){
        return true;
    }
    
    @Override
    public boolean register(){
    	if (!canRegister()) return false;
    	Plugin plugin = Bukkit.getPluginManager().getPlugin("NeoCore");
    	if (plugin == null) return false;
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
		return "bossmults";
	}
    
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) return "Loading...";
		
		String args[] = identifier.split("_");
		String boss = args[0];
		String id = args[1];
		BossInfo bi = InfoAPI.getBossInfo(boss);
		if (NeoCore.getPlayerFields("BossMultipliers").getValue(p.getUniqueId(), boss) == null) {
			return "Invalid";
		}
		int level = (int) NeoCore.getPlayerFields("BossMultipliers").getValue(p.getUniqueId(), boss);
		
		if (id.equalsIgnoreCase("gold")) {
			if (level < 1) {
				return "" + 0;
			}
			double scale = Math.min(2, 1 + (0.02 * (level - 1)));
			return df.format(scale);
		}
		else if (id.equalsIgnoreCase("chest")) {
			if (level < 1) {
				return "" + 0;
			}
			double scale = Math.min(50, 25 + (0.5 * (level - 1)));
			return df.format(scale);
		}
		else if (id.equalsIgnoreCase("damage")) {

			if (level <= 6 && level >= 1) {
				double scale = 1 + (0.1 * (level - 1));
				return df.format(scale);
			}
			else if (level > 6) {
				double scale = 1 + (0.3 * (level - 1));
				return df.format(scale);
			}
			else if (level < 1) {
				double scale = 1 + (0.01 * (level));
				return df.format(scale);
			}
		}
		else if (id.equalsIgnoreCase("health")) {
    		double oldHealth = bi.getTotalHealth();
    		double newHealth = oldHealth;
    		if (level >= 1) {
    			newHealth *= 0.5 + (Math.min(6, level) * 0.5);
    			if (level > 6) {
    				newHealth += (level - 6) * 0.2 * oldHealth;
    			}
    		}
    		else {
    			newHealth *= 1 + (level  * 0.01);
    		}
			return df.format(newHealth);
		}
		return "Invalid placeholder";
	}
}
