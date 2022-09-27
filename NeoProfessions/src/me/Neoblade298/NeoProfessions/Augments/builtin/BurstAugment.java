package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;

public class BurstAugment extends Augment implements ModDamageDealtAugment {
	
	public BurstAugment() {
		super();
		this.name = "Burst";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public BurstAugment(int level) {
		super(level);
		this.name = "Burst";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	@Override
	public double getDamageDealtMult(LivingEntity user) {
		return 0.01 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new BurstAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		PlayerData pdata = SkillAPI.getPlayerData(user);
		return (pdata.getMana() / pdata.getMaxMana()) > 0.8 && pdata.getClass("class").getData().getManaName().endsWith("MP");
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases damage by §f" + formatPercentage(getDamageDealtMult(user)) + "% §7when dealing");
		lore.add("§7damage above 80% mana.");
		lore.add("§cOnly works with mana.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
