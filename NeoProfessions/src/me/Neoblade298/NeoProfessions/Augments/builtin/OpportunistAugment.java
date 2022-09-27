package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;

public class OpportunistAugment extends Augment implements ModDamageDealtAugment {
	
	public OpportunistAugment() {
		super();
		this.name = "Opportunist";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public OpportunistAugment(int level) {
		super(level);
		this.name = "Opportunist";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	@Override
	public double getDamageDealtMult(LivingEntity user) {
		return 0.01 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new OpportunistAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		return FlagManager.hasFlag(target, "stun") || FlagManager.hasFlag(target, "root") || target.hasPotionEffect(PotionEffectType.SLOW);
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases damage by §f" + formatPercentage(getDamageDealtMult(user)) + "% §7when dealing");
		lore.add("§7damage to a stunned/rooted/slowed enemy.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
