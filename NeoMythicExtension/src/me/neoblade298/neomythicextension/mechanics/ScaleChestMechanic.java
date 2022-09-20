package me.neoblade298.neomythicextension.mechanics;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import me.Neoblade298.NeoConsumables.Consumables;
import me.Neoblade298.NeoConsumables.objects.GeneratableConsumable;
import me.neoblade298.neocore.bungee.BungeeAPI;
import me.neoblade298.neomythicextension.MythicExt;
import me.neoblade298.neomythicextension.events.ChestDropEvent;

public class ScaleChestMechanic implements ITargetedEntitySkill {

	protected final ItemStack item;
	protected final double basechance;
	protected final String msg;
	protected final String boss;
	protected final Random rand;

    @Override
    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

	public ScaleChestMechanic(MythicLineConfig config) {
        String key = config.getString("i", "mi_sewerzombie");
        this.item = ((GeneratableConsumable) Consumables.getConsumable(key)).getItem(null, 1);
        this.basechance = config.getDouble(new String[] {"basechance", "bc"}, 0.25);
        this.msg = new String("&4[&c&lMLMC&4] &7" + config.getString("msg", "&7You found a Boss Chest")).replaceAll("&", "§");
        this.boss = config.getString("boss", "Ratface");
        this.rand = new Random();
        
        if (this.item == null) {
        	Bukkit.getLogger().log(Level.WARNING, "[NeoMythicExtension] Item doesn't exist: " + key);
        }
	}
	
	@Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		try {
			if (target.getBukkitEntity() instanceof Player) {
				if (data.getCaster().getLevel() < 1) {
					return SkillResult.CONDITION_FAILED;
				}
				
				double rand = this.rand.nextDouble();
	
				// Increases chance by boss level
				double chance = Math.min(0.5, this.basechance + (0.005 * (data.getCaster().getLevel() - 1)));
				
				// Check if player is holding a drop charm
				Player p = (Player) target.getBukkitEntity();
				int dropType = 0;
	
				if (p.hasPermission("tokens.active.boss")) {
					dropType = 2;
					chance = 1;
				}
	
				final ItemStack fitem = this.item;
				if (p.hasPermission("tokens.active.boss")) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset tokens.active.boss");
				}
				
				ChestDropEvent e = new ChestDropEvent(p, chance, dropType);
				Bukkit.getPluginManager().callEvent(e);
				double moddedChance = e.getChance();
				dropType = e.getDropType();
				if (MythicExt.debug) {
					Bukkit.getLogger().info("[NeoMythicExt] Generated chest chance: " + chance + ", cutoff: " + e.getChance() + ", drop type: " + dropType);
				}
				
				// Check for successful drop
				if (rand <= moddedChance) {
					HashMap<Integer, ItemStack> failed = p.getInventory().addItem(fitem);
					if (!failed.isEmpty()) p.getWorld().dropItem(p.getLocation(), fitem);
					
					// Message
					String localMsg = msg;
					if (dropType == 1 && rand >= chance) localMsg += " via Chest Augment";
					if (dropType == 2) localMsg += " via Boss Chest Token";
					localMsg += "!";
					p.sendMessage(localMsg);
					String name = fitem.getItemMeta().getDisplayName().replaceAll("§", "&");
					Bukkit.getLogger().info("Player " + p.getName() + " found boss chest, sending broadcast");
					BungeeAPI.broadcast("&4[&c&lMLMC&4] &e" + p.getName() + " &7has found " + name + "&7!");
				}
				return SkillResult.SUCCESS;
			}
			return SkillResult.INVALID_TARGET;
		} catch (Exception e) {
			e.printStackTrace();
			return SkillResult.ERROR;
		}
    }
}
