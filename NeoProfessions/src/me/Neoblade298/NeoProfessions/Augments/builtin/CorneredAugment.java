package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCriticalCheckEvent;
import com.sucy.skill.api.player.PlayerData;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModCritCheckAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class CorneredAugment extends Augment implements ModCritCheckAugment {
	private static double critMult = AugmentManager.getValue("cornered.crit-multiplier-base");
	private static double critMultLvl = AugmentManager.getValue("cornered.crit-multiplier-per-lvl");
	private static double minHealth = AugmentManager.getValue("cornered.health-percent-min");
	
	public CorneredAugment() {
		super();
		this.name = "Cornered";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_CHECK});
	}

	public CorneredAugment(int level) {
		super(level);
		this.name = "Cornered";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_CHECK});
	}

	@Override
	public double getCritChanceMult(Player user) {
		return critMult + (critMultLvl * ((level / 5) - 1));
	}

	@Override
	public Augment createNew(int level) {
		return new CorneredAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, PlayerCriticalCheckEvent e) {
		Player p = user.getPlayer();
		double percentage = p.getHealth() / p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		return percentage < minHealth;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases critical hit chance by §f" + formatPercentage(getCritChanceMult(user)) + "%");
		lore.add("§7when below 30% health.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
