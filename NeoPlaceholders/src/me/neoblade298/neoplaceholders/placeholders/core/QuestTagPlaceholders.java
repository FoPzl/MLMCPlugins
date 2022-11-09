package me.neoblade298.neoplaceholders.placeholders.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sucy.skill.SkillAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neoblade298.neocore.NeoCore;

public class QuestTagPlaceholders extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return Bukkit.getPluginManager().getPlugin("NeoQuests") != null;
    }
    
    @Override
    public boolean register(){
    	if (!canRegister()) return false;
    	Plugin plugin = Bukkit.getPluginManager().getPlugin("NeoQuests");
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
		return "questtags";
	}

    @Override
    public String getRequiredPlugin(){
        return "NeoQuests";
    }
    
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) return "Loading...";
		if (SkillAPI.getPlayerAccountData(p) == null) return "Loading...";
		
		int acct = SkillAPI.getPlayerAccountData(p).getActiveId();
		String args[] = identifier.split("_");
		String subkey = args[0];
		
		return "" + NeoCore.getPlayerTags("questaccount_" + acct).exists(subkey, p.getUniqueId());
	}
}
