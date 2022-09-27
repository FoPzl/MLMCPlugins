package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.player.PlayerData;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;

public class OverloadAugment extends Augment implements ModDamageDealtAugment {
	double manaTaken;
	
	public OverloadAugment() {
		super();
		this.name = "Overload";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
		this.manaTaken = (this.level / 5) * 0.5;
	}

	public OverloadAugment(int level) {
		super(level);
		this.name = "Overload";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
		this.manaTaken = (this.level / 5) * 0.5;
	}
	
	@Override
	public void applyDamageDealtEffects(Player user, LivingEntity target, double damage) {
		SkillAPI.getPlayerData(user).useMana(this.manaTaken, ManaCost.SPECIAL);
	}

	@Override
	public double getDamageDealtMult(LivingEntity user) {
		return 0.015 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new OverloadAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		PlayerData pdata = SkillAPI.getPlayerData(user);
		return (pdata.getMana() / pdata.getMaxMana()) > 0.1 && pdata.getClass("class").getData().getManaName().endsWith("MP");
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases damage by §f" + formatPercentage(getDamageDealtMult(user)) + "% §7when dealing");
		lore.add("§7damage while above 10% mana. Costs");
		lore.add("§f" + this.manaTaken + " §7mana per damage instance.");
		lore.add("§cOnly works with mana.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
