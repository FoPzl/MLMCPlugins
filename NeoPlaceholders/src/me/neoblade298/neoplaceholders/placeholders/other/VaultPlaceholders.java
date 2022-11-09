package me.neoblade298.neoplaceholders.placeholders.other;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.sucy.skill.SkillAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neoblade298.neoplaceholders.NeoPlaceholders;
import net.milkbowl.vault.economy.Economy;

public class VaultPlaceholders extends PlaceholderExpansion {
	NeoPlaceholders main;
	Economy econ;
	public VaultPlaceholders(NeoPlaceholders main) {
		this.main = main;
	}

    @Override
    public boolean canRegister(){
        return Bukkit.getPluginManager().getPlugin("Vault") != null;
    }
    
    @Override
    public boolean register(){
    	if (!canRegister()) return false;
        RegisteredServiceProvider<Economy> rsp = main.getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
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
		return "nvault";
	}

    @Override
    public String getRequiredPlugin(){
        return "Vault";
    }
    
	@Override
	public String getVersion() {
		return "1.0.1";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) return "Loading...";
		DecimalFormat df = new DecimalFormat("0");
		DecimalFormat df1k = new DecimalFormat("0.00k");
		DecimalFormat df1M = new DecimalFormat("0.00M");
		
		String args[] = identifier.split("_");
		if (!SkillAPI.isLoaded(p)) return "Loading...";
		
		if (args[0].equalsIgnoreCase("money")) {
			String placeholder = "Loading...";
			double money = econ.getBalance(p);
			// Check if cursed
			
			if (money >= 1000000) {
				placeholder = df1M.format(money / 1000000);
			}
			else if (money >= 1000) {
				placeholder = df1k.format(money / 1000);
			}
			else {
				placeholder = df.format(money);
			}
			return placeholder;
		}
	 	return "Invalid placeholder";
	}
}
