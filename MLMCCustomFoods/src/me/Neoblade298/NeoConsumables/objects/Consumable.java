package me.Neoblade298.NeoConsumables.objects;

import java.util.ArrayList;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Neoblade298.NeoConsumables.Consumables;

public abstract class Consumable {
	Consumables main;
	String key;
	ArrayList<Sound> sounds;

	public Consumable(Consumables main, String key) {
		this.main = main;
		this.key = key;
		this.sounds = new ArrayList<Sound>();
	}
	
	public Consumables getMain() {
		return main;
	}
	
	public String getKey() {
		return key;
	}

	public ArrayList<Sound> getSounds() {
		return sounds;
	}

	public abstract boolean canUse(Player p, ItemStack item);
	public abstract void use(Player p, ItemStack item);
}
