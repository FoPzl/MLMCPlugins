package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModTauntAugment;

public class SteadfastAugment extends Augment implements ModTauntAugment {
	
	public SteadfastAugment() {
		super();
		this.name = "Steadfast";
		this.etypes = Arrays.asList(new EventType[] {EventType.TAUNT});
	}

	public SteadfastAugment(int level) {
		super(level);
		this.name = "Steadfast";
		this.etypes = Arrays.asList(new EventType[] {EventType.TAUNT});
	}

	@Override
	public double getTauntGainMult(Player user) {
		return 0.03 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new SteadfastAugment(level);
	}

	@Override
	public boolean canUse(Player user) {
		return true;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases taunts by §f" + formatPercentage(getTauntGainMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
