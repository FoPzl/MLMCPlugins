package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.sucy.skill.api.event.PlayerSkillCastSuccessEvent;
import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModSkillCastAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;
import me.Neoblade298.NeoProfessions.Objects.StoredAttributes;

public class SpectreAugment extends Augment implements ModSkillCastAugment {
	
	public SpectreAugment() {
		super();
		this.name = "Spectre";
		this.etypes = Arrays.asList(new EventType[] {EventType.SKILL_CAST});
	}

	public SpectreAugment(int level) {
		super(level);
		this.name = "Spectre";
		this.etypes = Arrays.asList(new EventType[] {EventType.SKILL_CAST});
	}

	@Override
	public Augment createNew(int level) {
		return new SpectreAugment(level);
	}
	
	@Override
	public void applySkillCastEffects(Player user, PlayerSkillCastSuccessEvent e) {
		FlagManager.addFlag(user, user, "aug_spectre", 400);
		StoredAttributes attr = new StoredAttributes();
		attr.setAttribute("healthregen", 50);
		attr.setAttribute("resourceregen", 50);
		Augment aug = this;
		new BukkitRunnable() {
			public void run() {
				PlayerAugments paugs = AugmentManager.getPlayerAugments(user);
				if (paugs != null) {
					paugs.removeAttributes(aug);
				}
			}
		}.runTaskLater(AugmentManager.getMain(), 60L);
	}

	@Override
	public boolean canUse(Player user, PlayerSkillCastSuccessEvent e) {
		return !FlagManager.hasFlag(user, "aug_spectre");
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
		lore.add("§7Casting any non-shift skill grants");
		lore.add("§7invisibility for 3s. During that");
		lore.add("§7time, gain §f+50% §7mana regen and");
		lore.add("§f+5% §7health regen.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
