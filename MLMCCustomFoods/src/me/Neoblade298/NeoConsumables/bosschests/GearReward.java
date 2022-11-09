package me.Neoblade298.NeoConsumables.bosschests;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import me.Neoblade298.NeoConsumables.Util;

public class GearReward extends ChestReward {
	private int level;
	private String type;
	private String rarity;
	private static HashMap<String, String> colorcodes = new HashMap<String, String>();
	
	static {
		colorcodes.put("rare", "&9");
		colorcodes.put("epic", "&6");
		colorcodes.put("legendary", "&4");
	}
	
	public GearReward(int level, String rarity) {
		this.level = level;
		this.type = "auto";
		this.rarity = rarity.toLowerCase();
	}

	@Override
	public void giveReward(Player p, int level) {
		Util.serverCommand("gear give " + p.getName() + " " + rarity + " " + type + " " + this.level);
	}

	@Override
	public void sendMessage(Player p) {
		String color = colorcodes.get(rarity);
		Util.sendMessage(p, "&7- a(n) " + color + rarity.substring(0,1).toUpperCase() + rarity.substring(1) + " &7item!");
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity.toLowerCase();
	}
	
	public static GearReward parse(String args[], int level) {
		String type = "auto";
		String rarity = "common";
		int weight = 1;
		for (String arg : args) {
			if (arg.startsWith("rarity")) {
				rarity = arg.substring(arg.indexOf(":") + 1);
			}
			else if (arg.startsWith("type")) {
				type = arg.substring(arg.indexOf(":") + 1);
			}
			else if (arg.startsWith("weight")) {
				weight = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
			}
		}
		GearReward r = new GearReward(level, rarity);
		r.setType(type);
		r.setWeight(weight);
		return r;
	}
	
	@Override
	public String toString() {
		return "[Lv " + level + "] " + StringUtils.capitalize(rarity) + " Equipment";
	}
	
}
