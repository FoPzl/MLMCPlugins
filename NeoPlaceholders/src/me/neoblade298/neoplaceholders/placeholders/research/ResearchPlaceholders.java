package me.neoblade298.neoplaceholders.placeholders.research;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neoblade298.neoresearch.PlayerStats;
import me.neoblade298.neoresearch.Research;

public class ResearchPlaceholders extends PlaceholderExpansion {

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
		return "research";
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
		
		Research nr = (Research) Bukkit.getPluginManager().getPlugin("NeoResearch");
		PlayerStats stats = Research.getPlayerStats(p.getUniqueId());
		if (args[0].equalsIgnoreCase("complete")) {
			if (Research.isCompleted(p, args[1])) {
				return "T";
			}
			else {
				return "F";
			}
		}
		else if (args[0].equalsIgnoreCase("level")) {
			return "" + stats.getLevel();
		}
		else if (args[0].equalsIgnoreCase("exp")) {
			return "" + stats.getExp();
		}
		else if (args[0].equalsIgnoreCase("completedresearch")) {
			return "" + stats.getCompletedResearchItems().size();
		}
		else if (args[0].equalsIgnoreCase("remainingresearch")) {
			return "" + (nr.getNumResearchItems() - stats.getCompletedResearchItems().size());
		}
		else if (args[0].equalsIgnoreCase("neededexp")) {
			return "" + Research.getNextLevel().get(stats.getLevel());
		}
    	return "§c???";
	}
}
