package me.Neoblade298.NeoConsumables.bosschests;

import org.bukkit.entity.Player;

import me.Neoblade298.NeoConsumables.Util;

public class ResearchBookReward extends ChestReward {
	private String mob;
	private String display;
	private String type;
	private String alias;
	
	public ResearchBookReward(String mob, String display) {
		this.mob = mob;
		this.type = "normal";
		this.display = display;
		alias = null;
	}

	@Override
	public void giveReward(Player p, int level) {
		int points = 5;
		if (!this.type.equalsIgnoreCase("normal")) {
			points = 25;
		}
		
		if (alias == null) {
			Util.serverCommand("nr spawnbook " + p.getName() + " " + mob + " " + level + " " + points);
		}
		else {
			Util.serverCommand("nr spawnbookalias " + p.getName() + " " + mob + " " + level + " " + points + " " + alias);
		}
	}

	@Override
	public void sendMessage(Player p) {
		if (this.type.equalsIgnoreCase("normal")) {
			Util.sendMessage(p, "&7- a(n) &4&l" + display + " &7research book!");
		}
		else {
			Util.sendMessage(p, "&7- an advanced &4&l" + display + " &7research book!");
			Util.serverBroadcast("&4[&c&lMLMC&4] &e" + p.getName() + " &7has found an advanced &4&l" + display + " &7research book!");
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public static ResearchBookReward parse(String args[], String mob, String display) {
		String type = "normal";
		String alias = null;
		int weight = 1;
		for (String arg : args) {
			if (arg.startsWith("mob")) {
				mob = arg.substring(arg.indexOf(":") + 1);
			}
			else if (arg.startsWith("display")) {
				display = arg.substring(arg.indexOf(":") + 1);
			}
			else if (arg.startsWith("type")) {
				type = arg.substring(arg.indexOf(":") + 1);
			}
			else if (arg.startsWith("alias")) {
				alias = arg.substring(arg.indexOf(":") + 1);
			}
			else if (arg.startsWith("weight")) {
				weight = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
			}
		}
		ResearchBookReward r = new ResearchBookReward(mob, display);
		r.setType(type);
		r.setAlias(alias);;
		r.setWeight(weight);
		return r;
	}
	
	@Override
	public String toString() {
		if (!this.type.equalsIgnoreCase("normal")) {
			return this.display + "'s Adv Research";
		}
		return this.display + "'s Research";
	}
}
