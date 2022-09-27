package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.util.FlagManager;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModDamageDealtAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class TorrentAugment extends Augment implements ModDamageDealtAugment {
	double healthGain = 30;
	
	public TorrentAugment() {
		super();
		this.name = "Torrent";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	public TorrentAugment(int level) {
		super(level);
		this.name = "Torrent";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_DEALT});
	}

	@Override
	public Augment createNew(int level) {
		return new TorrentAugment(level);
	}

	@Override
	public boolean canUse(Player user, LivingEntity target) {
		if (!FlagManager.hasFlag(user, "aug_cd_torrent")) {
			AugmentManager.getPlayerAugments(user).incrementHitCount(this);
		}
		return AugmentManager.getPlayerAugments(user).getHitCount(this) >= 5;
	}
	
	@Override
	public boolean isPermanent() {
		return true;
	}

	@Override
	public String getLine() {
		return "§7[§9§o" + name + " Lv " + level + "§7]";
	}
	
	@Override
	public void applyDamageDealtEffects(Player user, LivingEntity target, double damage) {
		Player p = user.getPlayer();
		p.setHealth(Math.min(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), p.getHealth() + this.healthGain));
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Heal for §f" + this.healthGain + " §7every");
		lore.add("§75x you deal damage. 1s");
		lore.add("§7cooldown per damage.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
