package me.Neoblade298.NeoProfessions.Augments.Builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCalculateDamageEvent;
import com.sucy.skill.api.event.PlayerCriticalSuccessEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModCritSuccessAugment;
import me.Neoblade298.NeoProfessions.Augments.ModDamageTakenAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class BrawlerAugment extends Augment implements ModCritSuccessAugment, ModDamageTakenAugment {
	private static double decreaseDamage = AugmentManager.getValue("brawler.damage-reduction-base");
	private static double decreaseDamageLvl = AugmentManager.getValue("brawler.damage-reduction-per-lvl");
	
	public BrawlerAugment() {
		super();
		this.name = "Brawler";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_SUCCESS, EventType.DAMAGE_TAKEN});
	}

	public BrawlerAugment(int level) {
		super(level);
		this.name = "Brawler";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_SUCCESS, EventType.DAMAGE_TAKEN});
	}
	
	@Override
	public void applyCritSuccessEffects(PlayerData user, double chance) {
		FlagManager.addFlag(user.getPlayer(), user.getPlayer(), "aug_brawler", 60);
	}

	@Override
	public Augment createNew(int level) {
		return new BrawlerAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, PlayerCriticalSuccessEvent e) {
		// crit
		return true;
	}

	@Override
	public boolean canUse(Player user, LivingEntity target, PlayerCalculateDamageEvent e) {
		// damage taken
		return FlagManager.hasFlag(user, "aug_brawler");
	}
	
	@Override
	public double getDamageTakenMult(LivingEntity user) {
		return decreaseDamage + (decreaseDamageLvl * ((level / 5) - 1));
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Upon critical hit, decrease damage taken");
		lore.add("§7by §f" + formatPercentage(getDamageTakenMult(user)) + "%§7 for §f3s§7.");
		lore.add("§73s cooldown.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
