package me.Neoblade298.NeoConsumables.objects;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.SkillBuffEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.util.Buff;
import com.sucy.skill.api.util.BuffManager;
import com.sucy.skill.api.util.BuffType;
import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.listener.AttributeListener;
import com.sucy.skill.listener.MechanicListener;

import me.Neoblade298.NeoConsumables.ConsumableManager;
import me.Neoblade298.NeoConsumables.Consumables;
import me.Neoblade298.NeoConsumables.runnables.HealthRunnable;
import me.Neoblade298.NeoConsumables.runnables.ManaRunnable;

public class DurationEffects {
	private Consumables main;
	private long startTime;
	private FoodConsumable cons;
	private ArrayList<BukkitTask> tasks;
	private UUID uuid;
	
	public DurationEffects(Consumables main, FoodConsumable cons, long startTime, UUID uuid, ArrayList<BukkitTask> tasks) {
		this.main = main;
		this.cons = cons;
		this.startTime = startTime;
		this.tasks = tasks;
		this.uuid = uuid;
	}
	
	public FoodConsumable getCons() {
		return cons;
	}
	
	public ArrayList<BukkitTask> getTasks() {
		return tasks;
	}
	
	public long getStartTime() {
		return this.startTime;
	}
	
	public void startEffects() {
		if (!isRelevant()) {
			ConsumableManager.effects.remove(uuid);
			return;
		}
		
		int secondsElapsed = (int) (System.currentTimeMillis() - startTime) / 1000;
		int secondsLeft = cons.getTotalDuration() - secondsElapsed;
		Player p = Bukkit.getPlayer(uuid);
		p.sendMessage("§4[§c§lMLMC§4] §7Active consumable: §e" + cons.getDisplay() + " §7for §e" + secondsLeft + " §7more seconds.");
		
		// Attributes
		if (cons.getAttributeTime() - secondsElapsed > 0) {
			cons.getAttributes().applyAttributes(p);
		}
		
		// Flags
		for (FlagAction flag : cons.getFlags()) {
			if (flag.isAdd()) {
				int remaining = flag.getDuration() - secondsElapsed;
				if (remaining > 0) {
					FlagManager.addFlag(p, p, flag.getFlag(), remaining * 20);
				}
			}
		}
		
		// Buffs
		for (BuffAction buff : cons.getBuffs()) {
	        BuffType buffType = buff.getType();
	        int remaining = buff.getDuration() - secondsElapsed;
	        String category = buff.getCategory();
	        int remainingTicks = remaining * 20;
	        if (remainingTicks > 0) {
	            SkillBuffEvent event = new SkillBuffEvent(p, p, buff.getValue(), remainingTicks, buffType, buff.isPercent());
	            Bukkit.getPluginManager().callEvent(event);
	            
	            if (!event.isCancelled()) {
	            	if (category == null) {
	                    BuffManager.getBuffData(p).addBuff(
	                            buffType,
	                            new Buff(cons.getKey() + "-" + p, event.getAmount(), buff.isPercent()),
	                            event.getTicks());
	            	}
	            	else {
	                    BuffManager.getBuffData(p).addBuff(
	                            buffType,
	                            category,
	                            new Buff(cons.getKey() + "-" + p, event.getAmount(), buff.isPercent()),
	                            event.getTicks());
	            	}
	            }
	        }
		}
		
		// Speed
		int speedTime = cons.getSpeedTime();
		if (speedTime - secondsElapsed > 0) {
            AttributeListener.refreshSpeed(p);
            FlagManager.addFlag(p, p, MechanicListener.SPEED_KEY, speedTime * 20);
            p.setWalkSpeed((float) (cons.getSpeed() * p.getWalkSpeed()));
		}

		// Health and mana regen
		PlayerData data = SkillAPI.getPlayerData(p);
			int healthReps = cons.getHealthReps();
			int manaReps = cons.getManaReps();
			int remainingHealthReps = healthReps - (secondsElapsed / cons.getHealthPeriod());
			int remainingManaReps = manaReps - (secondsElapsed / cons.getManaPeriod());
			
			if (remainingHealthReps > 0) {
				tasks.add(new HealthRunnable(p, cons.getHealth(), remainingHealthReps).runTaskTimer(main, 0, cons.getHealthPeriod() * 20));
			}
			
			if (remainingManaReps > 0) {
				if (data.getMainClass().getData().getManaName().contains("MP")) {
					tasks.add(new ManaRunnable(p, cons.getMana(), remainingManaReps).runTaskTimer(main, 0, cons.getManaPeriod() * 20));
				}
			}
	}
	
	public void endEffects(boolean isNewEffect) {
		if (!isRelevant()) {
			ConsumableManager.effects.remove(uuid);
			return;
		}
		
		
		Player p = Bukkit.getPlayer(uuid);
		
		// only do this if a new effect is replacing this one
		if (isNewEffect) {
			cons.getAttributes().removeAttributes(p);
			
			for (FlagAction flag : cons.getFlags()) {
				FlagManager.removeFlag(p, flag.getFlag());
			}
			
			for (BuffAction buff : cons.getBuffs()) {
				double amount = buff.isPercent() ? 1 : 0;
            	if (buff.getCategory() == null) {
                    BuffManager.getBuffData(p).addBuff(
                            buff.getType(),
                            new Buff(cons.getKey() + "-" + p, amount, buff.isPercent()),
                            1);
            	}
            	else {
                    BuffManager.getBuffData(p).addBuff(
                            buff.getType(),
                            buff.getCategory(),
                            new Buff(cons.getKey() + "-" + p, amount, buff.isPercent()),
                            1);
            	}
			}
			
			if (cons.getSpeedTime() > 0) {
				FlagManager.removeFlag(p, "sapiSpeedKey");
			}
		}
		
		for (BukkitTask task : tasks) {
			if (!task.isCancelled()) {
				task.cancel();
			}
		}
		
		// Potion effects
		for (PotionEffect effect : cons.getPotions()) {
			if (p.hasPotionEffect(effect.getType())) {
				p.removePotionEffect(effect.getType());
			}
		}
	}
	
	// Use when saving
	public boolean isRelevant() {
		long elapsedMilliseconds = System.currentTimeMillis() - startTime;
		return (cons.getTotalDuration() * 1000) > elapsedMilliseconds;
	}
}
