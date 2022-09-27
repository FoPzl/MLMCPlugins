package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;
import me.Neoblade298.NeoProfessions.Augments.ModPotionAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class PhantomAugment extends Augment implements ModPotionAugment, ModDamageDealtAugment {
	
	public PhantomAugment() {
		super();
		this.name = "Phantom";
		this.etypes = Arrays.asList(new EventType[] {EventType.POTION, EventType.DAMAGE_DEALT});
	}

	public PhantomAugment(int level) {
		super(level);
		this.name = "Phantom";
		this.etypes = Arrays.asList(new EventType[] {EventType.POTION, EventType.DAMAGE_DEALT});
	}

	@Override
	public Augment createNew(int level) {
		return new PhantomAugment(level);
	}
	
	@Override
	public void applyPotionEffects(Player user, EntityPotionEffectEvent e) {
		PotionEffect applied = e.getNewEffect();
		PotionEffect pe = null;
		if (e.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
			pe = new PotionEffect(PotionEffectType.SPEED, 0, applied.getDuration());
		}
		else if (e.getNewEffect().getType().equals(PotionEffectType.SPEED)) {
			pe = new PotionEffect(PotionEffectType.INVISIBILITY, 0, applied.getDuration());
		}
		
		if (pe != null) {
			FlagManager.addFlag(user, user, "aug_phantomPotion", 20);
			user.addPotionEffect(pe);
		}
	}
	
	@Override
	public double getDamageDealtMult(LivingEntity user) {
		return 0.8;
	}
	
	@Override
	public boolean canUse(Player user, LivingEntity target) {
		boolean canUse = !FlagManager.hasFlag(user, "aug_phantom") && AugmentManager.getPlayerAugments(user.getPlayer()).getCount(this) >= 4 &&
				(user.hasPotionEffect(PotionEffectType.INVISIBILITY) || user.hasPotionEffect(PotionEffectType.SPEED));
		if (canUse) FlagManager.addFlag(user, user, "aug_phantom", 20);
		return canUse;
	}
	
	@Override
	public boolean canUse(Player user, EntityPotionEffectEvent e) {
		return !FlagManager.hasFlag(user, "aug_phantomPotion") && e.getAction().equals(Action.ADDED);
	}

	@Override
	public String getLine() {
		return "§7[§8§o" + name + " Lv " + level + "§7]";
	}
	
	@Override
	public boolean isPermanent() {
		return true;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Set effect with 4:");
		lore.add("§7Anytime you gain invisibility, receive");
		lore.add("§7speed and vice versa. 1s cooldown.");
		lore.add("§7Having either speed or invisibility");
		lore.add("§7Grants §f" + formatPercentage(getDamageDealtMult(user)) + "% §7bonus damage.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
