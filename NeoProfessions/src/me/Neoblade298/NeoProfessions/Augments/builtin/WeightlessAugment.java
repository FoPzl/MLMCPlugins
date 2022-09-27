package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModCleanupAugment;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;
import me.Neoblade298.NeoProfessions.Augments.ModInitAugment;

public class WeightlessAugment extends Augment implements ModInitAugment, ModCleanupAugment, ModDamageDealtAugment {
	
	public WeightlessAugment() {
		super();
		this.name = "Weightless";
		this.etypes = Arrays.asList(new EventType[] {EventType.INIT, EventType.CLEANUP, EventType.DAMAGE_DEALT});
	}

	public WeightlessAugment(int level) {
		super(level);
		this.name = "Weightless";
		this.etypes = Arrays.asList(new EventType[] {EventType.INIT, EventType.CLEANUP, EventType.DAMAGE_DEALT});
	}

	@Override
	public Augment createNew(int level) {
		return new WeightlessAugment(level);
	}
	
	@Override
	public void applyInitEffects(Player user) {
		user.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));
	}
	
	@Override
	public void applyCleanupEffects(Player user) {
		user.removePotionEffect(PotionEffectType.JUMP);
	}
	
	@Override
	public boolean isPermanent() {
		return true;
	}
	
	@Override
	public double getDamageDealtMult(LivingEntity user) {
		return 0.4;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean canUse(Player user, LivingEntity target) {
		return !user.isOnGround();
	}

	@Override
	public String getLine() {
		return "§7[§a§o" + name + " Lv " + level + "§7]";
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Grants permanent jump boost 2.");
		lore.add("§7Dealing damage while in the air");
		lore.add("§7increases damage by §f" + formatPercentage(getDamageDealtMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
