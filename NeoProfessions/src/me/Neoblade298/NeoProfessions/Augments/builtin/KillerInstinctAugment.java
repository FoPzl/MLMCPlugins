package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerCriticalCheckEvent;
import com.sucy.skill.api.player.PlayerData;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModCritCheckAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class KillerInstinctAugment extends Augment implements ModCritCheckAugment {
	
	public KillerInstinctAugment() {
		super();
		this.name = "Killer Instinct";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_CHECK});
	}

	public KillerInstinctAugment(int level) {
		super(level);
		this.name = "Killer Instinct";
		this.etypes = Arrays.asList(new EventType[] {EventType.CRIT_CHECK});
	}
	
	@Override
	public double getCritChanceMult(Player user) {
		return 0.25;
	}

	@Override
	public Augment createNew(int level) {
		return new KillerInstinctAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, PlayerCriticalCheckEvent e) {
		boolean isWithin = false;
		for (Entity ent : user.getPlayer().getNearbyEntities(4, 4, 4)) {
			if (!ent.hasMetadata("NPC")
	                && !ent.getType().equals(EntityType.ARMOR_STAND) && !(ent instanceof Player)) {
				isWithin = true;
				break;
			}
		}
		return AugmentManager.getPlayerAugments(user.getPlayer()).getCount(this) >= 4 && isWithin;
	}

	@Override
	public boolean isPermanent() {
		return true;
	}
	
	@Override
	public String getLine() {
		return "§7[§4§o" + name + " Lv " + level + "§7]";
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Set effect with 4:");
		lore.add("§7Increase crit chance");
		lore.add("§7by §f100%§7 when within");
		lore.add("§f4§7 blocks of an enemy.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
