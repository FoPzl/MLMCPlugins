package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;

public class CrimsonPactAugment extends Augment implements ModDamageDealtAugment {
	
	public CrimsonPactAugment() {
		super();
		this.name = "Crimson Pact";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public CrimsonPactAugment(int level) {
		super(level);
		this.name = "Crimson Pact";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}
	
	@Override
	public void applyDamageDealtEffects(Player user, LivingEntity target, double damage) {
		if (!FlagManager.hasFlag(user, "aug_crimsonPact")) {
			FlagManager.addFlag(user, user, "aug_crimsonPact", 40);
			double max = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			user.damage(max * 0.01);
		}
	}

	@Override
	public double getDamageDealtMult(LivingEntity user) {
		return 0.3;
	}

	@Override
	public Augment createNew(int level) {
		return new CrimsonPactAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		return true;
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
		lore.add("§7Increases damage by §f" + formatPercentage(getDamageDealtMult(user)) + "%§7.");
		lore.add("§7Deals 1% of your max health to you");
		lore.add("§7any time you deal damage. 2s cooldown.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
