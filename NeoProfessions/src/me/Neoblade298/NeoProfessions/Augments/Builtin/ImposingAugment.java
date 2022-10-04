package me.Neoblade298.NeoProfessions.Augments.Builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModTauntAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class ImposingAugment extends Augment implements ModTauntAugment {
	private static double tauntMult = AugmentManager.getValue("imposing.taunt-multiplier-base");
	private static double tauntMultLvl = AugmentManager.getValue("imposing.taunt-multiplier-per-lvl");
	private static double minHealth = AugmentManager.getValue("imposing.health-percent-min");
	
	public ImposingAugment() {
		super();
		this.name = "Imposing";
		this.etypes = Arrays.asList(new EventType[] {EventType.TAUNT});
	}

	public ImposingAugment(int level) {
		super(level);
		this.name = "Imposing";
		this.etypes = Arrays.asList(new EventType[] {EventType.TAUNT});
	}

	@Override
	public double getTauntGainMult(Player user) {
		return tauntMult + (tauntMultLvl * ((level / 5) - 1));
	}

	@Override
	public Augment createNew(int level) {
		return new ImposingAugment(level);
	}

	@Override
	public boolean canUse(Player user) {
		double percentage = user.getHealth() / user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		return percentage > minHealth;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases taunts by §f" + formatPercentage(getTauntGainMult(user)) + "%");
		lore.add("§7when above " + formatPercentage(minHealth) + "% health.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
