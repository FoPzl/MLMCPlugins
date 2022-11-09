package me.Neoblade298.NeoProfessions.Augments.Builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCalculateDamageEvent;

import me.Neoblade298.NeoProfessions.Professions;
import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageTakenAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class InsidiousAugment extends Augment implements ModDamageTakenAugment {
	private static double damageCap = AugmentManager.getValue("insidious.damage-cap");
	private static double chance = AugmentManager.getValue("insidious.dodge-chance");
	
	public InsidiousAugment() {
		super();
		this.name = "Insidious";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_TAKEN});
	}

	public InsidiousAugment(int level) {
		super(level);
		this.name = "Insidious";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_TAKEN});
	}

	@Override
	public double getDamageTakenMult(LivingEntity user) {
		return 1;
	}

	@Override
	public Augment createNew(int level) {
		return new InsidiousAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target, PlayerCalculateDamageEvent e) {
		return e.getDamage() < damageCap && Professions.gen.nextDouble() <= chance;
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
		lore.add("§7Upon taking <" + damageCap + " damage pre-defense,");
		lore.add("§f" + formatPercentage(chance) + "%§7 chance to take 0 damage.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
