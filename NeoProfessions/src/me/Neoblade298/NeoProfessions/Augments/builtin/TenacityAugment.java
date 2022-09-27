package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.FlagApplyEvent;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModFlagAugment;

public class TenacityAugment extends Augment implements ModFlagAugment {
	
	public TenacityAugment() {
		super();
		this.name = "Tenacity";
		this.etypes = Arrays.asList(new EventType[] {EventType.FLAG_RECEIVE});
	}

	public TenacityAugment(int level) {
		super(level);
		this.name = "Tenacity";
		this.etypes = Arrays.asList(new EventType[] {EventType.FLAG_RECEIVE});
	}

	@Override
	public double getFlagTimeMult(Player user) {
		return -0.015 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new TenacityAugment(level);
	}

	@Override
	public boolean canUse(FlagApplyEvent e) {
		return e.getFlag().equalsIgnoreCase("root") | e.getFlag().equalsIgnoreCase("stun");
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Reduces stun/root time by §f" + formatPercentage(getFlagTimeMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
