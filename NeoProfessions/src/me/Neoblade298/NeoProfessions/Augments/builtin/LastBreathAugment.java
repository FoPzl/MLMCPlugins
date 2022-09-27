package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModRegenAugment;

public class LastBreathAugment extends Augment implements ModRegenAugment {
	
	public LastBreathAugment() {
		super();
		this.name = "Last Breath";
		this.etypes = Arrays.asList(new EventType[] {EventType.REGEN});
	}

	public LastBreathAugment(int level) {
		super(level);
		this.name = "Last Breath";
		this.etypes = Arrays.asList(new EventType[] {EventType.REGEN});
	}

	@Override
	public double getRegenMult(Player user) {
		return 0.02 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new LastBreathAugment(level);
	}

	@Override
	public boolean canUse(Player user) {
		Player p = user.getPlayer();
		double percentage = p.getHealth() / p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		return percentage < 0.5;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases health regen by §f" + formatPercentage(getRegenMult(user)) + "%");
		lore.add("§7while below 50% health.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
