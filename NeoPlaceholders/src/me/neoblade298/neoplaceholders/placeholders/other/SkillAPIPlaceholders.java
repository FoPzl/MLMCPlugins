package me.neoblade298.neoplaceholders.placeholders.other;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class SkillAPIPlaceholders extends PlaceholderExpansion {
	private static int QUEST_X_BOUND_1 = -1578;
	private static int QUEST_X_BOUND_2 = -1168;
	private static int QUEST_Z_BOUND_1 = 1243;
	private static int QUEST_Z_BOUND_2 = 1805;
	private static int TOWNY_X_BOUND_1 = -1638;
	private static int TOWNY_X_BOUND_2 = -1468;
	private static int TOWNY_Z_BOUND_1 = 764;
	private static int TOWNY_Z_BOUND_2 = 1034;

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
		if (!SkillAPI.isLoaded(p)) return "Loading...";
		
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
						if (args[2].equalsIgnoreCase("level")) {
							return "§e" + pClass.getLevel();
						}
						else if (args[2].equalsIgnoreCase("class")) return "§e" + pClass.getData().getName();
					}
					if (pProf != null) {
						if (args[2].equalsIgnoreCase("profession")) return "§e" + pProf.getData().getName();
					}
				}
				return "§cN/A";
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
		else if (args[0].equalsIgnoreCase("level")) {
			PlayerData data = SkillAPI.getPlayerData(p);
			PlayerClass pClass = data.getClass("class");
			if (pClass == null) {
				return "";
			}
			String cName = pClass.getData().getName();
			if (pClass.getLevel() == 10 && cName.equalsIgnoreCase("Beginner")) {
				return "§c/warp advance";
			}
			else if (pClass.getLevel() == 30 && (cName.equalsIgnoreCase("Swordsman") ||
					cName.equalsIgnoreCase("Archer") || cName.equalsIgnoreCase("Mage") ||
					cName.equalsIgnoreCase("Thief"))) {
				return "§c/warp advance";
			}
			return "" + pClass.getLevel();
		}
		else if (args[0].equalsIgnoreCase("warnings")) {
			PlayerData data = SkillAPI.getPlayerData(p);
			if (data != null) {
				if (data.getAttributePoints() > 0) {
					return "§cYou have " + data.getAttributePoints() + " unspent /attr!";
				}
				if (data.getClass("class") == null && !isInTutorial(p)) {
					return "§cYou have no class! /warp advance!";
				}
			}
			return "§8§m-|-------------|-";
		}
	 	return "Invalid placeholder";
	}
	
	private boolean isInTutorial(Player p) {
		if (p.getWorld().getName().equalsIgnoreCase("Argyll")) {
			Location loc = p.getLocation();
			double x = loc.getX();
			double z = loc.getZ();
			if (QUEST_X_BOUND_1 < x && x < QUEST_X_BOUND_2) {
				if (QUEST_Z_BOUND_1 < z && z < QUEST_Z_BOUND_2) {
					return true;
				}
			}
			if (TOWNY_X_BOUND_1 < x && x < TOWNY_X_BOUND_2) {
				if (TOWNY_Z_BOUND_1 < z && z < TOWNY_Z_BOUND_2) {
					return true;
				}
			}
		}
		return false;
	}
}
