package me.Neoblade298.NeoConsumables.bosschests;

import org.bukkit.entity.Player;

import me.Neoblade298.NeoConsumables.Util;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class AugmentReward extends ChestReward {
	private int level;
	private String droptable;
	
	public AugmentReward(int level, String droptable) {
		this.level = level;
		this.droptable = droptable;
	}

	@Override
	public void giveReward(Player p, int level) {
		p.getInventory().addItem(AugmentManager.getViaDroptable(droptable, this.level).getItem(p));
	}

	@Override
	public void sendMessage(Player p) {
		Util.sendMessage(p, "&7- a &6Lv " + level + " Augment&7!");
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getDroptable() {
		return droptable;
	}

	public void setDroptable(String droptable) {
		this.droptable = droptable;
	}

	public static AugmentReward parse(String args[], int level) {
		String droptable = "default";
		int weight = 1;
		for (String arg : args) {
			if (arg.startsWith("droptable")) {
				droptable = arg.substring(arg.indexOf(":") + 1);
			}
			else if (arg.startsWith("weight")) {
				weight = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
			}
		}
		AugmentReward r = new AugmentReward(level, droptable);
		r.setWeight(weight);
		return r;
	}
	
	@Override
	public String toString() {
		return "[Lv " + level + "] Augments";
	}
}
