package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModChestDropAugment;
import me.neoblade298.neomythicextension.events.ChestDropEvent;

public class ChestChanceAugment extends Augment implements ModChestDropAugment {
	
	public ChestChanceAugment() {
		super();
		this.name = "Chest Chance";
		this.etypes = Arrays.asList(new EventType[] {EventType.CHEST_DROP});
	}

	public ChestChanceAugment(int level) {
		super(level);
		this.name = "Chest Chance";
		this.etypes = Arrays.asList(new EventType[] {EventType.CHEST_DROP});
	}

	@Override
	public Augment createNew(int level) {
		return new ChestChanceAugment(level);
	}
	
	@Override
	public double getChestChanceMult(Player user) {
		return 0.01 * (level / 5);
	}

	@Override
	public boolean canUse(Player user, ChestDropEvent e) {
		return true;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases drop rate of");
		lore.add("§7boss chests by §f" + formatPercentage(getChestChanceMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
