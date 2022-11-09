package me.neoblade298.neorelics;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.util.FlagManager;

public class SetEffect {
	private HashMap<String, Integer> attributes;
	private String flag;
	public SetEffect(HashMap<String, Integer> attributes, String flag) {
		this.attributes = attributes;
		this.flag = flag;
	}
	
	public void applyEffects(Player p) {
		PlayerData data = SkillAPI.getPlayerData(p);
		if (this.flag != null) {
			FlagManager.addFlag(p, p, this.flag, -1);
		}
		for (Entry<String, Integer> entry : attributes.entrySet()) {
			data.addBonusAttributes(entry.getKey(), entry.getValue());
		}
	}
	
	public void removeEffects(Player p) {
		if (this.flag != null) {
			FlagManager.removeFlag(p, this.flag);
		}
		for (Entry<String, Integer> entry : attributes.entrySet()) {
			SkillAPI.getPlayerData(p).addBonusAttributes(entry.getKey(), -entry.getValue());
		}
	}
}
