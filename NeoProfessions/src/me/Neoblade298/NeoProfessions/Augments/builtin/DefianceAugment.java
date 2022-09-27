package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.player.PlayerData;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModManaGainAugment;

public class DefianceAugment extends Augment implements ModManaGainAugment {
	
	public DefianceAugment() {
		super();
		this.name = "Defiance";
		this.etypes = Arrays.asList(new EventType[] {EventType.MANA_GAIN});
	}

	public DefianceAugment(int level) {
		super(level);
		this.name = "Defiance";
		this.etypes = Arrays.asList(new EventType[] {EventType.MANA_GAIN});
	}

	@Override
	public double getManaGainMult(Player user) {
		return 0.04 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new DefianceAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, ManaSource src) {
		return (user.getMana() / user.getMaxMana()) < 0.3;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases mana regen by §f" + formatPercentage(getManaGainMult(user)) + "% §7when");
		lore.add("§7below 30% mana.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
