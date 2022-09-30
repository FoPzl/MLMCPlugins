package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class InitiatorAugment extends Augment implements ModDamageDealtAugment {
	private double damageMult = AugmentManager.getValue("initiator.damage-multiplier-base");
	private double damageMultLvl = AugmentManager.getValue("initiator.damage-multiplier-per-lvl");
	private double minHealth = AugmentManager.getValue("initiator.min-health");
	
	public InitiatorAugment() {
		super();
		this.name = "Initiator";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public InitiatorAugment(int level) {
		super(level);
		this.name = "Initiator";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	@Override
	public double getDamageDealtMult(LivingEntity user) {
		return damageMult + (damageMultLvl * ((level / 5) - 1));
	}

	@Override
	public Augment createNew(int level) {
		return new InitiatorAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		AttributeInstance ai = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (ai != null) {
			double percentage = target.getHealth() / target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			return percentage > minHealth;
		}
		return false;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases damage by §f" + formatPercentage(getDamageDealtMult(user)) + "% §7when dealing");
		lore.add("§7damage to an enemy above " + formatPercentage(minHealth) + "% health.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
