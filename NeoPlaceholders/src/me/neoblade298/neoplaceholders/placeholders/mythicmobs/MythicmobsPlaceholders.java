package me.neoblade298.neoplaceholders.placeholders.mythicmobs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class MythicmobsPlaceholders extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return Bukkit.getPluginManager().getPlugin("MythicMobs") != null;
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
		return "neomythicmobs";
	}

    @Override
    public String getRequiredPlugin(){
        return "MythicMobs";
    }
    
	@Override
	public String getVersion() {
		return "1.0.1";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) return "Loading...";
		
		String args[] = identifier.split("_");
		if (args[0].equalsIgnoreCase("name")) {
			try {
				return MythicBukkit.inst().getMobManager().getMythicMob(args[1]).get().getDisplayName().get();
			}
			catch (Exception e) {
				return "§cInvalid name!";
			}
		}
    	return "§cInvalid placeholder!";
	}
}
