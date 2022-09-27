package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModResearchPointsAugment;
import me.neoblade298.neomythicextension.events.MythicResearchPointsChanceEvent;

public class ResearchAugment extends Augment implements ModResearchPointsAugment {
	
	public ResearchAugment() {
		super();
		this.name = "Research";
		this.etypes = Arrays.asList(new EventType[] {EventType.RESEARCH_POINTS});
	}

	public ResearchAugment(int level) {
		super(level);
		this.name = "Research";
		this.etypes = Arrays.asList(new EventType[] {EventType.RESEARCH_POINTS});
	}

	@Override
	public Augment createNew(int level) {
		return new ResearchAugment(level);
	}
	
	@Override
	public double getRPChanceMult(Player user) {
		return 0.01 * (level / 5);
	}

	@Override
	public boolean canUse(Player user, MythicResearchPointsChanceEvent e) {
		return true;
	}

	public ItemStack getItem(Player user) {
		ItemStack item = super.getItem(user);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add("§7Increases rate of research");
		lore.add("§7breakthroughs by §f" + formatPercentage(getRPChanceMult(user)) + "%§7.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
