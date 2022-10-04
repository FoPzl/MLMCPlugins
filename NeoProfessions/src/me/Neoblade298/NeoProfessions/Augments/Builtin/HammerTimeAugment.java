package me.Neoblade298.NeoProfessions.Augments.Builtin;

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
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class HammerTimeAugment extends Augment implements ModDamageDealtAugment {
	private static double damageMult = AugmentManager.getValue("hammertime.damage-multiplier");
	private static int damageCap = (int) AugmentManager.getValue("hammertime.damage-cap");
	private static int distanceMax = (int) AugmentManager.getValue("hammertime.distance-max");
	private static int distanceMaxSq;
	
	public HammerTimeAugment() {
		super();
		this.name = "Hammer Time";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
		distanceMaxSq = (int) Math.pow(distanceMax, 2);
	}

	public HammerTimeAugment(int level) {
		super(level);
		this.name = "Hammer Time";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	@Override
	public double getDamageDealtFlat(LivingEntity user, PlayerCalculateDamageEvent e) {
		double newDamage = e.getDamage() * damageMult;
		return Math.min(damageCap, newDamage);
	}

	@Override
	public Augment createNew(int level) {
		return new HammerTimeAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		Location diff = user.getLocation().subtract(target.getLocation());
		double diffxy = (diff.getX() * diff.getX()) + (diff.getY() * diff.getY());
		return diffxy <= distanceMaxSq;
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
		lore.add("§7Increases damage by §f" + formatPercentage(damageMult) + "% §7when dealing");
		lore.add("§7damage closer than " + distanceMax + " blocks away.");
		lore.add("§7Capped to §f" + damageCap + " §7increase.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
