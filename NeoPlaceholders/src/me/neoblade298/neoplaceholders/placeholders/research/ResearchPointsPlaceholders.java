package me.neoblade298.neoplaceholders.placeholders.research;

import java.util.HashMap;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neoblade298.neoresearch.Research;

public class ResearchPointsPlaceholders extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return Bukkit.getPluginManager().getPlugin("NeoResearch") != null;
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
		return "researchpoints";
	}

    @Override
    public String getRequiredPlugin(){
        return "NeoResearch";
    }
    
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) return "Loading...";
		
		String args[] = identifier.split("_");
		
		if (args.length != 1) return "Invalid placeholder";
		String mob = args[0];
		Optional<MythicMob> mm = MythicBukkit.inst().getMobManager().getMythicMob(mob);
		if (mm.isEmpty()) {
			return "Invalid placeholder";
		}
		String name = mm.get().getDisplayName().get();
		HashMap<String, Integer> researchPoints = Research.getPlayerStats(p.getUniqueId()).getResearchPoints();
		if (researchPoints.containsKey(mob)) {
			int points = researchPoints.get(mob);
			return name + "§7: §e" + points;
		}
    	return "§c???";
	}
}
