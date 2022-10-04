package me.Neoblade298.NeoProfessions.Augments.Builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModHealAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class RejuvenatingAugment extends Augment implements ModHealAugment {
	private static double manaGain = AugmentManager.getValue("rejuvenating.mana-gain-base");
	private static double manaGainLvl = AugmentManager.getValue("rejuvenating.mana-gain-per-lvl");
	private static int cooldownSeconds = (int) AugmentManager.getValue("rejuvenating.cooldown-seconds");
	private double manaGainFinal = manaGain + (manaGainLvl * ((level / 5) - 1));
	
	public RejuvenatingAugment() {
		super();
		this.name = "Rejuvenating";
		this.etypes = Arrays.asList(new EventType[] {EventType.HEAL});
	}

	public RejuvenatingAugment(int level) {
		super(level);
		this.name = "Rejuvenating";
		this.etypes = Arrays.asList(new EventType[] {EventType.HEAL});
	}

	@Override
	public Augment createNew(int level) {
		return new RejuvenatingAugment(level);
	}

	@Override
	public void applyHealEffects(PlayerData user, LivingEntity target, double healing) {
		SkillAPI.getPlayerData((Player) target).giveMana(manaGainFinal);
		FlagManager.addFlag(user.getPlayer(), user.getPlayer(), "aug_rejuvenating", cooldownSeconds * 20);
	}

	@Override
	public boolean canUse(PlayerData user, LivingEntity target) {
		if (target instanceof Player && !FlagManager.hasFlag(user.getPlayer(), "aug_rejuvenating")) {
			Player p = (Player) target;
			PlayerData data = SkillAPI.getPlayerData(p);
			return data.getClass("class").getData().getManaName().endsWith("MP");
		}
		return false;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Healing also grants healed player");
		lore.add("§f" + formatDouble(manaGainFinal) + " §7mana. Only works on mana users.");
		lore.add("§7Has a " + cooldownSeconds + " second cooldown.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
