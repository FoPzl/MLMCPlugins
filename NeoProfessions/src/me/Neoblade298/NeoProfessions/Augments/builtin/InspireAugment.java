package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.SkillBuffEvent;
import com.sucy.skill.api.util.BuffType;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModBuffAugment;

public class InspireAugment extends Augment implements ModBuffAugment {
	
	public InspireAugment() {
		super();
		this.name = "Inspire";
		this.etypes = Arrays.asList(new EventType[] {EventType.BUFF});
	}

	public InspireAugment(int level) {
		super(level);
		this.name = "Inspire";
		this.etypes = Arrays.asList(new EventType[] {EventType.BUFF});
	}

	@Override
	public double getBuffTimeMult(LivingEntity user) {
		return 0.02 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new InspireAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target, SkillBuffEvent e) {
		return e.getType().equals(BuffType.SKILL_DAMAGE) || e.getType().equals(BuffType.DAMAGE);
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases duration of damage");
		lore.add("§7buffs by §f" + formatPercentage(getBuffTimeMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
