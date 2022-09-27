package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModProfessionHarvestAugment;
import me.Neoblade298.NeoProfessions.PlayerProfessions.ProfessionType;

public class MinerAugment extends Augment implements ModProfessionHarvestAugment {
	
	public MinerAugment() {
		super();
		this.name = "Miner";
		this.etypes = Arrays.asList(new EventType[] {EventType.PROFESSION_HARVEST});
	}

	public MinerAugment(int level) {
		super(level);
		this.name = "Miner";
		this.etypes = Arrays.asList(new EventType[] {EventType.PROFESSION_HARVEST});
	}

	@Override
	public double getChance() {
		return (this.level / 5) * 0.01;
	}

	@Override
	public double getAmountMult(Player user) {
		return 1;
	}

	@Override
	public Augment createNew(int level) {
		return new MinerAugment(level);
	}

	@Override
	public boolean canUse(Player user, ProfessionType type) {
		return type.equals(ProfessionType.STONECUTTER);
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7" + formatPercentage(getChance()) + "% chance to increase");
		lore.add("§7stonecutter base yield by " + formatPercentage(getAmountMult(user)) + "%.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
