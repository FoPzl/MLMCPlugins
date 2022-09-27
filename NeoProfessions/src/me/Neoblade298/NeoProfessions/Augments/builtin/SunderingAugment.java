package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCriticalDamageEvent;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModCritDamageAugment;

public class SunderingAugment extends Augment implements ModCritDamageAugment {
	
	public SunderingAugment() {
		super();
		this.name = "Sundering";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_DAMAGE});
	}

	public SunderingAugment(int level) {
		super(level);
		this.name = "Sundering";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_DAMAGE});
	}

	@Override
	public double getCritDamageMult(Player user) {
		return 0.01 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new SunderingAugment(level);
	}

	@Override
	public boolean canUse(Player user, PlayerCriticalDamageEvent e) {
		return true;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases critical hit damage by §f" + formatPercentage(getCritDamageMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
