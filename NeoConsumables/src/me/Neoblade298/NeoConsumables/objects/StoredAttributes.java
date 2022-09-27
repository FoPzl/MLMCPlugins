package me.Neoblade298.NeoConsumables.objects;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;

public class StoredAttributes {
	private HashMap<String, Integer> attrs;
	
	public StoredAttributes() {
		this.attrs = new HashMap<String, Integer>();
	}

	public StoredAttributes(HashMap<String, Integer> attrs) {
		this.attrs = attrs;
	}

	public void applyAttributes(Player p) {
		PlayerData data = SkillAPI.getPlayerData(p);
		for (String attr : attrs.keySet()) {
			data.addBonusAttributes(attr, attrs.get(attr));
		}
	}

	public void removeAttributes(Player p) {
		PlayerData data = SkillAPI.getPlayerData(p);
		for (String attr : attrs.keySet()) {
			data.addBonusAttributes(attr, -attrs.get(attr));
		}
	}

	public HashMap<String, Integer> getAttrs() {
		return attrs;
	}
	
	public int getAttribute(String attr) {
		return attrs.getOrDefault(attr, 0);
	}
	
	public void setAttribute(String attr, int num) {
		attrs.put(attr, num);
	}

	public String toString() {
		return attrs.toString();
	}
	
	public boolean isEmpty() {
		return attrs.size() == 0;
	}

}
