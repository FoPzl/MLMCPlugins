package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCalculateDamageEvent;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageTakenAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class DaredevilAugment extends Augment implements ModDamageTakenAugment {
	private static double damageMult = AugmentManager.getValue("daredevil.damage-reduction-multiplier-base");
	private static double damageMultLvl = AugmentManager.getValue("daredevil.damage-reduction-multiplier-per-lvl");
	private static double minHealth = AugmentManager.getValue("daredevil.health-percent-min");
	
	public DaredevilAugment() {
		super();
		this.name = "Daredevil";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_TAKEN});
	}

	public DaredevilAugment(int level) {
		super(level);
		this.name = "Daredevil";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_TAKEN});
	}

	@Override
	public double getDamageTakenMult(LivingEntity user) {
		return damageMult + (damageMultLvl * ((level / 5) - 1));
	}

	@Override
	public Augment createNew(int level) {
		return new DaredevilAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target, PlayerCalculateDamageEvent e) {
		double percentage = (user.getPlayer().getHealth() / user.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		return percentage < minHealth;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Decreases damage taken by §f" + formatPercentage(getDamageTakenMult(user)) + "%");
		lore.add("§7when below 30% health.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
