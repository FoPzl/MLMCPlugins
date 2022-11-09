package me.neoblade298.neoplaceholders.placeholders.other;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class ChatColorPlaceholders extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return true;
    }
    
    @Override
    public boolean register(){
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
		return "chatcolor";
	}
    
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	// Note identifier is currently unused
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) return "Loading...";
		
		if (p.hasPermission("mycommand.staff")) {
			return "#FFFFFF";
		}
		else if (p.hasPermission("mycommand.helper")) {
			return "#FFFFFF";
		}
		else if (p.hasPermission("mycommand.senior")) {
			return "#FF0000";
		}
    	return "&f";
	}
}
