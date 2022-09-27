package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModTauntAugment;

public class ImposingAugment extends Augment implements ModTauntAugment {
	
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
		return 0.05 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new ImposingAugment(level);
	}

	@Override
	public boolean canUse(Player user) {
		double percentage = user.getHealth() / user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		return percentage > 0.8;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases taunts by §f" + formatPercentage(getTauntGainMult(user)) + "%");
		lore.add("§7when above 80% health.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
