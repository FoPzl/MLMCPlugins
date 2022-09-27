package me.Neoblade298.NeoProfessions.Augments.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.event.PlayerSkillCastSuccessEvent;
import com.sucy.skill.api.event.SkillBuffEvent;
import com.sucy.skill.api.util.Buff;
import com.sucy.skill.api.util.BuffManager;
import com.sucy.skill.api.util.BuffType;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.EventType;
import me.Neoblade298.NeoProfessions.Augments.ModSkillCastAugment;

public class BarrierAugment extends Augment implements ModSkillCastAugment {
	
	public BarrierAugment() {
		super();
		this.name = "Barrier";
		this.etypes = Arrays.asList(new EventType[] {EventType.SKILL_CAST});
	}

	public BarrierAugment(int level) {
		super(level);
		this.name = "Barrier";
		this.etypes = Arrays.asList(new EventType[] {EventType.SKILL_CAST});
	}

	@Override
	public Augment createNew(int level) {
		return new BarrierAugment(level);
	}
	
	@Override
	public void applySkillCastEffects(Player user, PlayerSkillCastSuccessEvent e) {
		double amount = -50;
		int ticks = 60;
        SkillBuffEvent event = new SkillBuffEvent(user, user, amount, ticks, BuffType.DEFENSE, false);
        Bukkit.getPluginManager().callEvent(event);
        
        if (!event.isCancelled()) {
            BuffManager.getBuffData(user).addBuff(
                    BuffType.DEFENSE,
                    new Buff("BarrierAugment-" + user, event.getAmount(), false),
                    event.getTicks());
        }
	}

	@Override
	public boolean canUse(Player user, PlayerSkillCastSuccessEvent e) {
		return true;
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
		lore.add("§7Reduces damage by §f50 §7for");
		lore.add("§73s after casting a skill.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
