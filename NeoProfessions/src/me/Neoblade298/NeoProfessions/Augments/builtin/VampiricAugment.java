package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCriticalSuccessEvent;
import com.sucy.skill.api.player.PlayerData;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModCritSuccessAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class VampiricAugment extends Augment implements ModCritSuccessAugment {
	private static double healthGain = AugmentManager.getValue("vampiric.health-gain-base");
	private static double healthGainLvl = AugmentManager.getValue("vampiric.health-gain-per-lvl");
	private int healthGainFinal = (int) (healthGain + (healthGainLvl * ((level / 5) - 1)));
	
	public VampiricAugment() {
		super();
		this.name = "Vampiric";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_SUCCESS});
	}

	public VampiricAugment(int level) {
		super(level);
		this.name = "Vampiric";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_SUCCESS});
	}
	
	@Override
	public void applyCritSuccessEffects(PlayerData user, double chance) {
		Player p = user.getPlayer();
		p.setHealth(Math.min(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), p.getHealth() + healthGainFinal));
	}

	@Override
	public Augment createNew(int level) {
		return new VampiricAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, PlayerCriticalSuccessEvent e) {
		return true;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Upon critical hit, gain §f" + formatDouble(healthGainFinal) + " §7health.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
