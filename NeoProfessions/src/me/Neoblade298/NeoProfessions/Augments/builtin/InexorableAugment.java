package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sucy.skill.api.event.PlayerCalculateDamageEvent;
import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageTakenAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class InexorableAugment extends Augment implements ModDamageTakenAugment {
	private static int secondsActive = (int) AugmentManager.getValue("inexorable.seconds-active");
	
	public InexorableAugment() {
		super();
		this.name = "Inexorable";
		this.etypes = Arrays.asList(new EventType[] {EventType.INTERACT});
	}

	public InexorableAugment(int level) {
		super(level);
		this.name = "Inexorable";
		this.etypes = Arrays.asList(new EventType[] {EventType.INTERACT});
	}

	@Override
	public Augment createNew(int level) {
		return new InexorableAugment(level);
	}
	
	@Override
	public double getDamageTakenMult(LivingEntity user) {
		return -1;
	}
	
	@Override
	public void applyDamageTakenEffects(Player user, LivingEntity target, PlayerCalculateDamageEvent e) {
		user.playSound(user.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target, PlayerCalculateDamageEvent e) {
		// Not on CD
		if (!FlagManager.hasFlag(user, "aug_cd_inexorable") && user.isBlocking()) {
			FlagManager.addFlag(user, user, "aug_cd_inexorable", 400);
			FlagManager.addFlag(user, user, "aug_inexorable", secondsActive * 20);
			user.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, secondsActive * 20, 0));
			return true;
		}
		
		// If on CD, only works if it's active and user blocking
		return FlagManager.hasFlag(user, "aug_inexorable") && user.isBlocking();
	}
	
	@Override
	public boolean isPermanent() {
		return true;
	}

	@Override
	public String getLine() {
		return "§7[§9§o" + name + " Lv " + level + "§7]";
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Blocking damage with the shield grants");
		lore.add("§7invulnerability for " + secondsActive + " seconds while");
		lore.add("§7the shield remains raised. 20s cd on use.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
