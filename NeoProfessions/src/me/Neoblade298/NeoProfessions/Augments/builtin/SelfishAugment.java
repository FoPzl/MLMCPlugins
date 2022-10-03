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
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class SelfishAugment extends Augment implements ModHealAugment {
	private static double healMult = AugmentManager.getValue("selfish.heal-multiplier-base");
	private static double healMultLvl = AugmentManager.getValue("selfish.heal-multiplier-per-lvl");
	
	public SelfishAugment() {
		super();
		this.name = "Selfish";
		this.etypes = Arrays.asList(new EventType[] {EventType.HEAL});
	}

	public SelfishAugment(int level) {
		super(level);
		this.name = "Selfish";
		this.etypes = Arrays.asList(new EventType[] {EventType.HEAL});
	}

	@Override
	public double getHealMult(Player user) {
		return healMult + (healMultLvl * ((level / 5) - 1));
	}

	@Override
	public Augment createNew(int level) {
		return new SelfishAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, LivingEntity target) {
		return user.getPlayer().equals(target);
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases self-healing by §f" + formatPercentage(getHealMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
