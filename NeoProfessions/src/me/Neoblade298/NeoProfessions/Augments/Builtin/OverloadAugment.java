package me.Neoblade298.NeoProfessions.Augments.Builtin;

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
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class OverloadAugment extends Augment implements ModDamageDealtAugment {
	private static double damageMult = AugmentManager.getValue("overload.damage-multiplier-base");
	private static double damageMultLvl = AugmentManager.getValue("overload.damage-multiplier-per-lvl");
	private static double manaTaken = AugmentManager.getValue("overload.mana-taken-base");
	private static double manaTakenLvl = AugmentManager.getValue("overload.mana-taken-per-lvl");
	private static double minMana = AugmentManager.getValue("overload.mana-percent-min");
	private double manaTakenFinal = manaTaken + (manaTakenLvl * ((level / 5) - 1));
	
	public OverloadAugment() {
		super();
		this.name = "Overload";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public OverloadAugment(int level) {
		super(level);
		this.name = "Overload";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}
	
	@Override
	public void applyDamageDealtEffects(Player user, LivingEntity target, double damage) {
		SkillAPI.getPlayerData(user).useMana(manaTakenFinal, ManaCost.SPECIAL);
	}

	@Override
	public double getDamageDealtMult(LivingEntity user) {
		return damageMult + (damageMultLvl * ((level / 5) - 1));
	}

	@Override
	public Augment createNew(int level) {
		return new OverloadAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		PlayerData pdata = SkillAPI.getPlayerData(user);
		return (pdata.getMana() / pdata.getMaxMana()) > minMana && pdata.getClass("class").getData().getManaName().endsWith("MP");
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases damage by §f" + formatPercentage(getDamageDealtMult(user)) + "% §7when dealing");
		lore.add("§7damage while above " + formatPercentage(minMana) + "% mana. Costs");
		lore.add("§f" + formatDouble(manaTakenFinal) + " §7mana per damage instance.");
		lore.add("§cOnly works with mana.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
