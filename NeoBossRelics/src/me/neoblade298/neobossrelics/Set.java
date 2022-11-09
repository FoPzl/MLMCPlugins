package me.neoblade298.neobossrelics;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class Set {
	private HashMap<Integer, SetEffect> effects;
	private String name;
	
	public Set(String name, HashMap<Integer, SetEffect> effects) {
		this.effects = effects;
		this.name = name;
	}
	
	public void applyEffects(Player p, int previous, int current) {
		// Add the effects that weren't previously there
		if (current - previous > 0) {
			for (int num : this.effects.keySet()) {
				if (num > previous && num <= current) {
					this.effects.get(num).applyEffects(p);
				}
			}
		}
		// Remove the effects that were previously there
		else {
			for (int num : this.effects.keySet()) {
				if (num > current && num <= previous) {
					this.effects.get(num).removeEffects(p);
				}
			}
		}
	}
	
	public String getName() {
		return this.name;
	}
}
