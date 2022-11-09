package me.Neoblade298.NeoProfessions.Augments.Builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class CalmingAugment extends Augment implements ModDamageDealtAugment {
	private static double threatReduc = AugmentManager.getValue("calming.threat-reduction-base");
	private static double threatReducLvl = AugmentManager.getValue("calming.threat-reduction-per-lvl");
	private double finalThreatReduc = threatReduc + (threatReducLvl * (level / 5));
	
	public CalmingAugment() {
		super();
		this.name = "Calming";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public CalmingAugment(int level) {
		super(level);
		this.name = "Calming";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}
	
	@Override
	public void applyDamageDealtEffects(Player user, LivingEntity target, double damage) {
		MythicBukkit.inst().getAPIHelper().reduceThreat(target, user, damage * this.finalThreatReduc);
	}

	@Override
	public Augment createNew(int level) {
		return new CalmingAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		return true;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Reduces threat generated from dealing");
		lore.add("§7damage by §f" + formatPercentage(this.finalThreatReduc) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
