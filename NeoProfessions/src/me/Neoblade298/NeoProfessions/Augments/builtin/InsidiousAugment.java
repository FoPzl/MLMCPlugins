package me.Neoblade298.NeoProfessions.Augments.builtin;

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

public class InsidiousAugment extends Augment implements ModDamageTakenAugment {
	
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
		return e.getDamage() < 300 && Professions.gen.nextDouble() <= 0.3;
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
		lore.add("§7Upon taking <300 damage pre-defense,");
		lore.add("§f30%§7 chance to take 0 damage.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
