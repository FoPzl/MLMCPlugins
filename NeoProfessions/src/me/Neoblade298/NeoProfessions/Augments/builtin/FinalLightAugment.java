package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.player.PlayerData;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModManaGainAugment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;

public class FinalLightAugment extends Augment implements ModManaGainAugment {
	private double manaMult = AugmentManager.getValue("finallight.mana-multiplier-base");
	private double manaMultLvl = AugmentManager.getValue("finallight.mana-multiplier-per-lvl");
	private int maxHealth = (int) AugmentManager.getValue("finallight.max-health");
	
	public FinalLightAugment() {
		super();
		this.name = "Final Light";
		this.etypes = Arrays.asList(new EventType[] {EventType.MANA_GAIN});
	}

	public FinalLightAugment(int level) {
		super(level);
		this.name = "Final Light";
		this.etypes = Arrays.asList(new EventType[] {EventType.MANA_GAIN});
	}

	@Override
	public double getManaGainMult(Player user) {
		return manaMult + (manaMultLvl * ((level / 5) - 1));
	}

	@Override
	public Augment createNew(int level) {
		return new FinalLightAugment(level);
	}

	@Override
	public boolean canUse(PlayerData user, ManaSource src) {
		double percentage = (user.getPlayer().getHealth() / user.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		return percentage < maxHealth;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases resource regen by §f" + formatPercentage(getManaGainMult(user)) + "% §7when");
		lore.add("§7below " + maxHealth + "% health.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
