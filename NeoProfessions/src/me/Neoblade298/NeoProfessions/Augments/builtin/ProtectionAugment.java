package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCalculateDamageEvent;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageTakenAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class ProtectionAugment extends Augment implements ModDamageTakenAugment {
	private static double damageTakenMult = AugmentManager.getValue("protection.damage-reduction-multiplier-base");
	private static double damageTakenMultLvl = AugmentManager.getValue("protection.damage-reduction-multiplier-per-lvl");
	
	public ProtectionAugment() {
		super();
		this.name = "Protection";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_TAKEN});
	}

	public ProtectionAugment(int level) {
		super(level);
		this.name = "Protection";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_TAKEN});
	}

	@Override
	public double getDamageTakenMult(LivingEntity user) {
		return damageTakenMult + (damageTakenMultLvl * ((level / 5) - 1));
	}

	@Override
	public Augment createNew(int level) {
		return new ProtectionAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target, PlayerCalculateDamageEvent e) {
		return true;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Decreases damage taken by §f" + formatPercentage(getDamageTakenMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
