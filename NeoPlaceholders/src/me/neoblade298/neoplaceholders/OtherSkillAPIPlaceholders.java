package me.neoblade298.neoplaceholders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class OtherSkillAPIPlaceholders extends PlaceholderExpansion {

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
		return "nsapi";
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
		if (p == null) return "Loading...";
		
		String args[] = identifier.split("_");
		
		if (args[0].equalsIgnoreCase("account")) {
			if (args.length == 1) {
				return "" + SkillAPI.getPlayerAccountData(p).getActiveId();
			}
			// %nsapi_account_#_class%
			// %nsapi_account_#_profession%
			// %nsapi_account_#_level%
			else if (args.length == 3) {
				int acc = Integer.parseInt(args[1]);
				PlayerData data = SkillAPI.getPlayerAccountData(p).getData(acc);
				if (data != null) {
					PlayerClass pClass = data.getClass("class");
					PlayerClass pProf = data.getClass("profession");
					if (pClass != null) {
						if (args[2].equalsIgnoreCase("level")) return "�e" + pClass.getLevel();
						else if (args[2].equalsIgnoreCase("class")) return "�e" + pClass.getData().getName();
					}
					if (pProf != null) {
						if (args[2].equalsIgnoreCase("profession")) return "�e" + pProf.getData().getName();
					}
				}
				return "�cN/A";
			}
		}
		else if (args[0].equalsIgnoreCase("profession")) {
			PlayerClass prof = SkillAPI.getPlayerData(p).getClass("profession");
			if (prof != null) {
				return prof.getData().getPrefix();
			}
			return "N/A";
		}
		else if (args[0].equalsIgnoreCase("mana")) {
			PlayerData data = SkillAPI.getPlayerData(p);
			if (data != null) {
				return "" + (int) data.getMana();
			}
			return "0";
		}
		else if (args[0].equalsIgnoreCase("profession")) {
			PlayerData data = SkillAPI.getPlayerData(p);
			if (data != null) {
				return "" + (int) data.getMaxMana();
			}
			return "0";
		}
	 	return "Invalid placeholder";
	}
}
