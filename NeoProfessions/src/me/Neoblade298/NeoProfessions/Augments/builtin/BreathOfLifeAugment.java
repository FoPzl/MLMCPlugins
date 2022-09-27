package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.player.PlayerData;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModHealAugment;

public class BreathOfLifeAugment extends Augment implements ModHealAugment {
	
	public BreathOfLifeAugment() {
		super();
		this.name = "Breath of Life";
		this.etypes = Arrays.asList(new EventType[] {EventType.HEAL});
	}

	public BreathOfLifeAugment(int level) {
		super(level);
		this.name = "Breath of Life";
		this.etypes = Arrays.asList(new EventType[] {EventType.HEAL});
	}

	@Override
	public double getHealMult(Player user) {
		return 0.5;
	}

	@Override
	public Augment createNew(int level) {
		return new BreathOfLifeAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, LivingEntity target) {
		return true;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases healing on allies by §f" + formatPercentage(getHealMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	@Override
	public boolean isPermanent() {
		return true;
	}

	@Override
	public String getLine() {
		return "§7[§a§o" + name + " Lv " + level + "§7]";
	}

}
