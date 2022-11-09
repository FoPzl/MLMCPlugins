package me.neoblade298.neoplaceholders.placeholders;

import java.util.ArrayList;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neoblade298.neorelics.NeoRelics;

public class NeoRelicsPlaceholders extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return true;
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
		return "neorelics";
	}

    @Override
    public String getRequiredPlugin(){
        return "NeoRelics";
    }
    
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) return "Loading...";
		
		String args[] = identifier.split("_");
		if (args[0].equalsIgnoreCase("lore")) {
			ArrayList<String> lore = NeoRelics.getRelics().get(args[1]).getLore();
			String line = lore.get(0);
			for (int i = 1; i < lore.size(); i++) {
				line += "\n" + lore.get(i);
			}
			return line;
		}
    	return "§cInvalid placeholder!";
	}
}
