package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCalculateDamageEvent;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;

public class DeadeyeAugment extends Augment implements ModDamageDealtAugment {
	private static final double DAMAGE_MULTIPLIER = 0.5;
	
	public DeadeyeAugment() {
		super();
		this.name = "Deadeye";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public DeadeyeAugment(int level) {
		super(level);
		this.name = "Deadeye";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	@Override
	public double getDamageDealtFlat(LivingEntity user, PlayerCalculateDamageEvent e) {
		double newDamage = e.getDamage() * 0.5;
		return Math.min(100, newDamage);
	}

	@Override
	public Augment createNew(int level) {
		return new DeadeyeAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		Location diff = user.getLocation().subtract(target.getLocation());
		double diffxy = (diff.getX() * diff.getX()) + (diff.getY() * diff.getY());
		return diffxy >= 100;
	}
	
	@Override
	public boolean isPermanent() {
		return true;
	}

	@Override
	public String getLine() {
		return "§7[§4§o" + name + " Lv " + level + "§7]";
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases damage by §f" + formatPercentage(DAMAGE_MULTIPLIER) + "% §7when dealing");
		lore.add("§7damage further than 10 blocks away.");
		lore.add("§7Capped to §f100 §7increase.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
