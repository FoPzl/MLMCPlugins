package me.Neoblade298.NeoProfessions.Augments.Builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.event.PlayerCriticalSuccessEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModCritSuccessAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;
import me.Neoblade298.NeoProfessions.Objects.FlagSettings;

public class SpellweavingAugment extends Augment implements ModCritSuccessAugment {
	private static double manaGain = AugmentManager.getValue("spellweaving.mana-gain-base");
	private static double manaGainLvl = AugmentManager.getValue("spellweaving.mana-gain-per-lvl");
	private static int cooldownSeconds = (int) AugmentManager.getValue("spellweaving.cooldown-seconds");
	private static final FlagSettings flag = new FlagSettings("aug_spellweaving", cooldownSeconds * 20);
	private double manaGainFinal = manaGain + (manaGainLvl * ((level / 5) - 1));
	
	public SpellweavingAugment() {
		super();
		this.name = "Spellweaving";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_SUCCESS});
	}

	public SpellweavingAugment(int level) {
		super(level);
		this.name = "Spellweaving";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_SUCCESS});
	}
	
	@Override
	public void applyCritSuccessEffects(PlayerData user, double chance) {
		user.giveMana(manaGainFinal, ManaSource.SPECIAL);
	}

	@Override
	public Augment createNew(int level) {
		return new SpellweavingAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, PlayerCriticalSuccessEvent e) {
		return user.getMainClass().getData().getManaName().endsWith("MP") && !FlagManager.hasFlag(user.getPlayer(), "aug_spellweaving");
	}
	
	@Override
	public FlagSettings setFlag() {
		return flag;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Upon critical hit, gain §f" + formatDouble(manaGainFinal) + " §7mana.");
		lore.add("§7Only works with mana.");
		lore.add("§7" + cooldownSeconds + "s cooldown.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
