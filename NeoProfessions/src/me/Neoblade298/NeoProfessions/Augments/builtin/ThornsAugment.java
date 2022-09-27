package me.Neoblade298.NeoProfessions.Augments.builtin;

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
import me.Neoblade298.NeoProfessions.Augments.ModDamageTakenAugment;
import me.Neoblade298.NeoProfessions.Objects.FlagSettings;

public class ThornsAugment extends Augment implements ModDamageTakenAugment {
	private double maxHealthMod;
	
	private static FlagSettings flag = new FlagSettings("aug_thorns", 20);
	
	public ThornsAugment() {
		super();
		this.name = "Thorns";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_TAKEN});
		this.maxHealthMod = (this.level / 5) * 0.002;
	}

	public ThornsAugment(int level) {
		super(level);
		this.name = "Thorns";
		this.etypes = Arrays.asList(new EventType[] {EventType.DAMAGE_TAKEN});
		this.maxHealthMod = (this.level / 5) * 0.002;
	}
	
	@Override
	public FlagSettings setFlagAfter() {
		return flag;
	}

	@Override
	public Augment createNew(int level) {
		return new ThornsAugment(level);
	}
	
	public double getThorns(Player user) {
		return getDamageReturned(user);
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Upon taking damage, return §f" + formatDouble(getDamageReturned(user)));
		lore.add("§7damage to the sender.");
		lore.add("§7Has a 1 second cooldown.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private double getDamageReturned(Player p) {
		return p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * this.maxHealthMod;
	}

	@Override
	public boolean canUse(Player user, LivingEntity target, PlayerCalculateDamageEvent e) {
		return !FlagManager.hasFlag(user, "aug_thorns");
	}

}
