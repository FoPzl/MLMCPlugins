package me.Neoblade298.NeoProfessions.Augments.Builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModProfessionHarvestAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;
import me.Neoblade298.NeoProfessions.PlayerProfessions.ProfessionType;

public class HerbalistAugment extends Augment implements ModProfessionHarvestAugment {
	private static double chanceMult = AugmentManager.getValue("herbalist.chance-multiplier-base");
	private static double chanceMultLvl = AugmentManager.getValue("herbalist.chance-multiplier-per-lvl");
	
	public HerbalistAugment() {
		super();
		this.name = "Herbalist";
		this.etypes = Arrays.asList(new EventType[] {EventType.PROFESSION_HARVEST});
	}

	public HerbalistAugment(int level) {
		super(level);
		this.name = "Herbalist";
		this.etypes = Arrays.asList(new EventType[] {EventType.PROFESSION_HARVEST});
	}

	@Override
	public double getChance() {
		return chanceMult + (chanceMultLvl * ((level / 5) - 1));
	}

	@Override
	public double getAmountMult(Player user) {
		return 1;
	}

	@Override
	public Augment createNew(int level) {
		return new HerbalistAugment(level);
	}

	@Override
	public boolean canUse(Player user, ProfessionType type) {
		return type.equals(ProfessionType.HARVESTER);
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7" + formatPercentage(getChance()) + "% chance to increase");
		lore.add("§7harvester base yield by " + formatPercentage(getAmountMult(user)) + "%.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
