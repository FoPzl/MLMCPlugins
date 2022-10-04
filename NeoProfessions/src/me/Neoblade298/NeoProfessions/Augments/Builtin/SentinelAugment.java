package me.Neoblade298.NeoProfessions.Augments.Builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCalculateDamageEvent;
import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;
import me.Neoblade298.NeoProfessions.Objects.FlagSettings;

public class SentinelAugment extends Augment implements ModDamageDealtAugment {
	private static double damageMult = AugmentManager.getValue("sentinel.health-damage-multiplier-base");
	private static double damageMultLvl = AugmentManager.getValue("sentinel.health-damage-multiplier-per-lvl");
	private static int cooldownSeconds = (int) AugmentManager.getValue("sentinel.cooldown-seconds");
	private static FlagSettings flag = new FlagSettings("aug_sentinel", cooldownSeconds * 20);
	
	public SentinelAugment() {
		super();
		this.name = "Sentinel";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public SentinelAugment(int level) {
		super(level);
		this.name = "Sentinel";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}
	
	private double getHealthMod() {
		return damageMult + (damageMultLvl * ((level / 5) - 1));
	}

	@Override
	public double getDamageDealtFlat(LivingEntity user, PlayerCalculateDamageEvent e) {
		return user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * getHealthMod();
	}

	@Override
	public Augment createNew(int level) {
		return new SentinelAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		return !FlagManager.hasFlag(user, "aug_sentinel");
	}
	
	@Override
	public FlagSettings setFlagAfter() {
		return flag;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases damage by §f" + formatDouble(getDamageDealtFlat(user, null)) + ", §7which");
		lore.add("§7is §f" + formatPercentage(getHealthMod()) + "% §7of your max health.");
		lore.add("§f" + cooldownSeconds + " §7second cooldown.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
