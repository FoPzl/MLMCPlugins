package me.Neoblade298.NeoConsumables.bosschests;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.Neoblade298.NeoConsumables.Util;
import me.Neoblade298.NeoProfessions.Managers.StorageManager;

public class StoredItemReward extends ChestReward {
	private int id;
	private int amount;
	
	public StoredItemReward(int id, int amount) {
		this.id = id;
		this.amount = amount;
	}

	@Override
	public void giveReward(Player p, int level) {
		StorageManager.givePlayer(p, id, amount);
	}

	@Override
	public void sendMessage(Player p) {
		Util.sendMessage(p, "&7- &e" + amount + " " + StorageManager.getItem(id).getDisplay());
	}

	public static StoredItemReward parse(String args[], int level, String display) {
		int id = -1;
		int weight = 1;
		int amount = 1;
		for (String arg : args) {
			if (arg.startsWith("id")) {
				id = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
			}
			else if (arg.startsWith("amount")) {
				amount = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
			}
			else if (arg.startsWith("weight")) {
				weight = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
			}
		}
		StoredItemReward r = new StoredItemReward(id, amount);
		r.setWeight(weight);
		StorageManager.addSource(id, "§4§l" + display + " Chest", false);
		return r;
	}
	
	@Override
	public String toString() {
		return ChatColor.stripColor(StorageManager.getItem(id).getDisplay());
	}
}
