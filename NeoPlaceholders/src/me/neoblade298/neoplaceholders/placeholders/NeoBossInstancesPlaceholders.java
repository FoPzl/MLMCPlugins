package me.neoblade298.neoplaceholders.placeholders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.util.FlagManager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neoblade298.neobossinstances.Boss;
import me.neoblade298.neobossinstances.BossInstances;
import me.neoblade298.neobossinstances.BossType;
import me.neoblade298.neocore.info.BossInfo;
import me.neoblade298.neocore.info.InfoAPI;

public class NeoBossInstancesPlaceholders extends PlaceholderExpansion {
	private BossInstances plugin;
	private DateFormat formatter = new SimpleDateFormat("m:ss");

    @Override
    public boolean canRegister(){
        return Bukkit.getPluginManager().getPlugin("NeoBossInstances") != null;
    }
    
    @Override
    public boolean register(){
    	if (!canRegister()) return false;
    	Plugin plugin = Bukkit.getPluginManager().getPlugin("NeoBossInstances");
    	if (plugin == null) return false;
    	this.plugin = (BossInstances) plugin;
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
		return "bosses";
	}

    @Override
    public String getRequiredPlugin(){
        return "NeoBossInstances";
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
		
		try {
			if (args[0].equalsIgnoreCase("cd")) {
				String boss = args[1];
				BossInfo bi = InfoAPI.getBossInfo(boss);
				String display;
				if (bi != null) {
					if (!bi.hasFought(p)) return "§c???";
					display = bi.getDisplayWithLevel(false);
				}
				else {
					display = plugin.getBossName(boss, p);
				}
				int time = plugin.getBossCooldown(boss, p);
				if (display == null) return "§c???";
				else if (time <= 0) return display + "§7: " + "§aReady!";
				else if (time > 0) {
					int minutes = time / 60;
					int seconds = time % 60;
					return display + "§7: " + String.format("§c%d:%02d", minutes, seconds);
				}
			}
			else if (args[0].equalsIgnoreCase("display")) {
				String boss = args[1];
				BossInfo bi = InfoAPI.getBossInfo(boss);
				if (bi == null) return "Invalid Placeholder";
				return bi.getDisplayWithLevel(false);
			}
			else if (args[0].equalsIgnoreCase("health")) {
				String boss = args[1];
				BossInfo bi = InfoAPI.getBossInfo(boss);
				if (bi == null) return "Invalid Placeholder";
				return "" + bi.getTotalHealth();
			}
			// %bosses_partyhealth_1-5%
			else if (args[0].equalsIgnoreCase("partyhealth")) {
				ArrayList<String> partyMembers = plugin.getHealthBars(p);
				if (partyMembers == null) {
					return "";
				}
				int num = Integer.parseInt(args[1]);
				if (num >= partyMembers.size()) {
					return "";
				}
				Player partyMember = Bukkit.getPlayer(partyMembers.get(num));
				if (partyMember == null) {
					return "";
				}
				
				double percenthp = partyMember.getHealth() / partyMember.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
				percenthp *= 100;
				int php = (int) percenthp;
				String color = null;

				if (FlagManager.hasFlag(partyMember, "curse")) {
					color = "§8";
				}
				else if (FlagManager.hasFlag(partyMember, "stun") || FlagManager.hasFlag(partyMember, "root") || FlagManager.hasFlag(partyMember, "silence")) {
					color = "§b";
				}
				
				if (color == null) {
					color = "§a";
					if (php < 50 && php >= 25) {
						color = "§e";
					}
					else if (php < 25) {
						color = "§c";
					}
				}
				
				String bar = "" + color;
				// Add 5 so 25% is still 3/10 on the health bar
				int phpmod = (php + 5) / 10;
				for (int i = 0; i < phpmod; i++) {
					bar += "|";
				}
				bar += "§7";
				for (int i = 0; i < (10 - phpmod); i++) {
					bar += "|";
				}
				
				return color + partyMember.getName() + " " + bar;
			}
			else if (args[0].equalsIgnoreCase("timers") && p != null) {
				// Boss timer only, no raid timer
				if (!SkillAPI.isLoaded(p)) return "";
				long bossTimer = plugin.getBossTimer(p);
				long raidTimer = plugin.getRaidTimer(p);
				Boss b = BossInstances.getBoss(p);
				if (b != null && b.getBossType().equals(BossType.DUNGEON)) {
					if ((System.currentTimeMillis() & 8191) > 4096) {
						return "§8§l> §c§lSpawners Killed: §f" + b.getSpawnersKilled() + " / " + b.getTotalSpawners();
					}
					else {
						return "§8§l> §c§lDungeon Timer: §c" + formatter.format(raidTimer - System.currentTimeMillis());
					}
				}
				if (bossTimer != -1 && raidTimer == -1) {
					return "§8§l> §c§lBoss Timer: " + formatter.format(System.currentTimeMillis() - bossTimer);
				}
				// Raid timer only, no boss timer
				else if (bossTimer == -1 && raidTimer != -1) {
					return "§8§l> §c§lBoss Timer: " + formatter.format(System.currentTimeMillis() - bossTimer);
				}
				// Both
				else if (bossTimer != -1 && raidTimer != -1) {
					if ((System.currentTimeMillis() & 8191) > 4096) {
						return "§8§l> §c§lBoss Timer: §c" + formatter.format(raidTimer - System.currentTimeMillis());
					}
					else {
						return "§8§l> §c§lRaid Timer: §c" + formatter.format(System.currentTimeMillis() - bossTimer);
					}
				}
				
				// Neither
				return "§8§l> §c§lTimer: §cN/A";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "Invalid Placeholder";
	}
}
