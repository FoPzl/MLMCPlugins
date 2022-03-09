package me.Neoblade298.NeoConsumables.objects;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;

public class StoredAttributes {
	private HashMap<String, Integer> stored;
	private HashMap<String, Integer> active;
	
	public StoredAttributes() {
		this.stored = new HashMap<String, Integer>();
		this.active = new HashMap<String, Integer>();
	}

	public StoredAttributes(HashMap<String, Integer> attrs) {
		this.stored = attrs;
		this.active = new HashMap<String, Integer>();
	}
	
	public int countAttributes() {
		int count = 0;
		for (String key : stored.keySet()) {
			count += stored.get(key);
		}
		return count;
	}

	public void applyAttributes(Player p) {
		PlayerData data = SkillAPI.getPlayerData(p);
		removeAttributes(p);
		for (String attr : stored.keySet()) {
			if (attr.equals("unused")) {
				continue;
			}
			// Must be deep copy
			data.addBonusAttributes(attr, -active.getOrDefault(attr, 0));
			active.put(attr, Integer.valueOf(stored.get(attr)));
			data.addBonusAttributes(attr, stored.get(attr));
		}
	}
	public void removeStoredAttributes() {
		stored.clear();
	}

	public void removeAttributes(Player p) {
		PlayerData data = SkillAPI.getPlayerData(p);
		for (String attr : active.keySet()) {
			data.addBonusAttributes(attr, -active.get(attr));
		}
		active.clear();
	}
	
	public void resetAttributes() {
		active.clear();
	}

	public HashMap<String, Integer> getStoredAttrs() {
		return stored;
	}

	public HashMap<String, Integer> getActiveAttrs() {
		return active;
	}
	
	public int getAttribute(String attr) {
		return stored.getOrDefault(attr, 0);
	}
	
	public void setAttribute(String attr, int num) {
		stored.put(attr, num);
	}

	public String toString() {
		return stored.toString();
	}
	
	public boolean isEmpty() {
		return stored.size() == 0;
	}

}
