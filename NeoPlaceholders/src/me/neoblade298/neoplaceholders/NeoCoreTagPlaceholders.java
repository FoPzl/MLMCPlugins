package me.neoblade298.neoplaceholders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sucy.skill.SkillAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neoblade298.neocore.NeoCore;

public class NeoCoreTagPlaceholders extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return Bukkit.getPluginManager().getPlugin("NeoCore") != null;
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
		return "playertags";
	}

    @Override
    public String getRequiredPlugin(){
        return "NeoCore";
    }
    
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) return "Loading...";
		if (!SkillAPI.isLoaded(p)) return "Loading...";
		
		String args[] = identifier.split("_");
		String key = args[0];
		String subkey = args[1];
		
		return "" + NeoCore.getPlayerTags(key).exists(subkey, p.getUniqueId());
	}
}