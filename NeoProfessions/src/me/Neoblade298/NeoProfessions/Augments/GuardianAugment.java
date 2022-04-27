package me.Neoblade298.NeoProfessions.Augments;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.SkillBuffEvent;
import com.sucy.skill.api.util.BuffType;

public class GuardianAugment extends Augment implements ModBuffAugment {
	
	public GuardianAugment() {
		super();
		this.name = "Guardian";
		this.etypes = Arrays.asList(new EventType[] {EventType.BUFF});
	}

	public GuardianAugment(int level) {
		super(level);
		this.name = "Guardian";
		this.etypes = Arrays.asList(new EventType[] {EventType.BUFF});
	}

	@Override
	public double getBuffMult(LivingEntity user) {
		return 0.02 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new GuardianAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target, SkillBuffEvent e) {
		return e.getType().equals(BuffType.SKILL_DEFENSE) || e.getType().equals(BuffType.DEFENSE);
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("�7Increases effectiveness of defense");
		lore.add("�7buffs by �f" + formatPercentage(getBuffMult(user)) + "%�7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}