package me.Neoblade298.NeoConsumables.bosschests;

import org.bukkit.entity.Player;

import me.Neoblade298.NeoConsumables.Util;

public class RelicReward extends ChestReward {
	private String mob;
	private String display;
	
	public RelicReward(String mob, String display) {
		this.mob = mob;
		this.display = display;
	}

	@Override
	public void giveReward(Player p, int level) {
		Util.serverCommand("mythicmobs items give -s " + p.getName() + " Relic" + mob);
	}

	@Override
	public void sendMessage(Player p) {
		Util.sendMessage(p, "&7- a(n) &4&l" + this.display + " Relic&7!");
	}

	public String getMob() {
		return mob;
	}

	public void setMob(String mob) {
		this.mob = mob;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
	
	public static RelicReward parse(String args[], String mob, String display) {
		int weight = 1;
		for (String arg : args) {
			if (arg.startsWith("mob")) {
				mob = arg.substring(arg.indexOf(":") + 1);
			}
			else if (arg.startsWith("display")) {
				display = arg.substring(arg.indexOf(":") + 1);
			}
			else if (arg.startsWith("weight")) {
				weight = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
			}
		}
		RelicReward r = new RelicReward(mob, display);
		r.setWeight(weight);
		return r;
	}
	
	@Override
	public String toString() {
		return this.display + "'s Relic";
	}
}
