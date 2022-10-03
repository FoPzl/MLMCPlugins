package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class SniperAugment extends Augment implements ModDamageDealtAugment {
	private static int minDistance = (int) AugmentManager.getValue("sniper.distance-min");
	private static int minDistanceSq = (int) Math.pow(minDistance, 2);
	private static double damageMult = AugmentManager.getValue("sniper.damage-multiplier-base");
	private static double damageMultLvl = AugmentManager.getValue("sniper.damage-multiplier-per-lvl");
	
	public SniperAugment() {
		super();
		this.name = "Sniper";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public SniperAugment(int level) {
		super(level);
		this.name = "Sniper";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	@Override
	public double getDamageDealtMult(LivingEntity user) {
		return damageMult + (damageMultLvl * ((level / 5) - 1));
	}

	@Override
	public Augment createNew(int level) {
		return new SniperAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		Location diff = user.getLocation().subtract(target.getLocation());
		double diffxy = (diff.getX() * diff.getX()) + (diff.getY() * diff.getY());
		return diffxy >= minDistanceSq;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases damage by §f" + formatPercentage(getDamageDealtMult(user)) + "% §7when dealing");
		lore.add("§7damage further than " + minDistance + " blocks away.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
