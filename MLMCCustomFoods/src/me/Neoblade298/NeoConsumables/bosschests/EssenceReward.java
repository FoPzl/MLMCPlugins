package me.Neoblade298.NeoConsumables.bosschests;

import org.bukkit.entity.Player;

import me.Neoblade298.NeoConsumables.Util;
import me.Neoblade298.NeoProfessions.Managers.CurrencyManager;

public class EssenceReward extends ChestReward {
	private int amount;
	private int level;
	
	public EssenceReward(int amount, int level) {
		this.amount = amount;
		this.level = level;
	}

	@Override
	public void giveReward(Player p, int level) {
		CurrencyManager.add(p, this.level, amount);
	}

	@Override
	public void sendMessage(Player p) {
		Util.sendMessage(p, "&7- &e" + amount + " &cLv " + level + " essence&7!");
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public static EssenceReward parse(String args[], int level) {
		int amount = 1;
		int weight = 1;
		for (String arg : args) {
			if (arg.startsWith("amount")) {
				amount = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
			}
			else if (arg.startsWith("weight")) {
				weight = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
			}
		}
		EssenceReward r = new EssenceReward(amount, level);
		r.setWeight(weight);
		return r;
	}
	
	@Override
	public String toString() {
		return "[Lv " + level + "] Essence";
	}
}
