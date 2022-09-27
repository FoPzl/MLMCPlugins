package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.player.PlayerData;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModHealAugment;

public class RallyAugment extends Augment implements ModHealAugment {
	
	public RallyAugment() {
		super();
		this.name = "Rally";
		this.etypes = Arrays.asList(new EventType[] {EventType.HEAL});
	}

	public RallyAugment(int level) {
		super(level);
		this.name = "Rally";
		this.etypes = Arrays.asList(new EventType[] {EventType.HEAL});
	}

	@Override
	public double getHealMult(Player user) {
		return 0.01 * (level / 5);
	}

	@Override
	public Augment createNew(int level) {
		return new RallyAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, LivingEntity target) {
		Player p = user.getPlayer();
		double percentage = p.getHealth() / p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		return percentage > 0.7;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases healing by §f" + formatPercentage(getHealMult(user)) + "% §7while");
		lore.add("§7above 70% health.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
