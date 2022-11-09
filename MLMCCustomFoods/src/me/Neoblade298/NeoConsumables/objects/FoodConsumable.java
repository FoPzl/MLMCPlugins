package me.Neoblade298.NeoConsumables.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoConsumables.ConsumableManager;
import me.Neoblade298.NeoConsumables.Consumables;
import me.Neoblade298.NeoConsumables.SkullCreator;
import me.Neoblade298.NeoConsumables.runnables.AttrRemoveRunnable;
import me.Neoblade298.NeoConsumables.runnables.HealthRunnable;
import me.Neoblade298.NeoConsumables.runnables.ManaRunnable;

public class FoodConsumable extends Consumable implements GeneratableConsumable {
	private static int defaultCooldown = 45000;
	Material mat;
	ArrayList<PotionEffect> potions;
	ArrayList<FlagAction> flags;
	ArrayList<BuffAction> buffs;
	StoredAttributes attributes;
	int attributeTime;
	ArrayList<String> commands, worlds, desc, lore;
	double saturation;
	int hunger;
	double speed;
	int speedTime;
	int health, healthReps, healthPeriod;
	int mana, manaReps, manaPeriod;
	int cooldown;
	String display, base64;
	boolean isDuration;
	int totalDuration;
	Rarity rarity;
	
	public FoodConsumable(Consumables main, String key) {
		super(main, key);
		
		potions = new ArrayList<PotionEffect>();
		attributes = new StoredAttributes();
		commands = new ArrayList<String>();
		worlds = new ArrayList<String>();
		desc = new ArrayList<String>();
		lore = new ArrayList<String>();
		flags = new ArrayList<FlagAction>();
		buffs = new ArrayList<BuffAction>();
		display = null;
		base64 = null;
		
		cooldown = defaultCooldown;
		isDuration = false; // Default instant
		
		totalDuration = 0;
	}
	
	public static void setDefaultCooldown(int cooldown) {
		defaultCooldown = cooldown;
	}

	public boolean canUse(Player p, ItemStack item) {
		// Check world compatible
		if (!worlds.contains(p.getWorld().getName())) {
			String message = "§4[§c§lMLMC§4] §cYou cannot use this consumable in this world";
			p.sendMessage(message);
			return false;
		}

		// Check cds
		UUID uuid = p.getUniqueId();
		if (!ConsumableManager.cds.containsKey(uuid)) {
			ConsumableManager.cds.put(uuid, new PlayerCooldowns());
			return true;
		}
		
		if (!isDuration) {
			long remaining = ConsumableManager.cds.get(uuid).getInstantCooldown() - System.currentTimeMillis();
			if (remaining <= 0) {
				return true;
			}
			remaining /= 1000L;
			p.sendMessage("§4[§c§lMLMC§4] §cInstant consumables cooldown: §e" + remaining + "s§7.");
			return false;
		}
		else {
			long remaining = ConsumableManager.cds.get(uuid).getDurationCooldown() - System.currentTimeMillis();
			if (remaining <= 0) {
				return true;
			}
			remaining /= 1000L;
			p.sendMessage("§4[§c§lMLMC§4] §cDuration consumables cooldown: §e" + remaining + "s§7.");
			return false;
		}
	}

	public void use(final Player p, ItemStack item) {
		// First get rid of any existing effects
		UUID uuid = p.getUniqueId();
		ArrayList<BukkitTask> tasks = new ArrayList<BukkitTask>();
		if (ConsumableManager.effects.containsKey(uuid)) {
			ConsumableManager.effects.get(uuid).endEffects(true);
		}
		
		// Sounds, commands
		p.sendMessage("§4[§c§lMLMC§4] §7You ate " + item.getItemMeta().getDisplayName() + "§7!");
		for (Sound sound : getSounds()) {
			p.getWorld().playSound(p.getEyeLocation(), sound, 1.0F, 1.0F);
		}
		executeCommands(p);
		
		// Food event only happens if hunger is changing
		int foodLevel = Math.min(20, p.getFoodLevel() + hunger);
		if (hunger > 0) {
			FoodLevelChangeEvent event = new FoodLevelChangeEvent(p, foodLevel);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				p.setFoodLevel(foodLevel);
				p.setSaturation((float) Math.min(p.getFoodLevel(), this.saturation + p.getSaturation()));
			}
		}

		// Set cooldown
		long nextEat = System.currentTimeMillis() + (this.cooldown * 1000);
		if (!isDuration) {
			ConsumableManager.cds.get(uuid).setInstantCooldown(nextEat);
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
				public void run() {
					if (p != null) {
						String message = "§4[§c§lMLMC§4] Instant consumables can be eaten again.";
						p.sendMessage(message);
					}
				}
			}, this.cooldown * 20);
		}
		else {
			ConsumableManager.cds.get(uuid).setDurationCooldown(nextEat);
		}

		// Potion effects
		for (PotionEffect effect : this.getEffect()) {
			p.addPotionEffect(effect);
		}

		// SkillAPI Attributes
		if (!attributes.isEmpty()) {
			attributes.applyAttributes(p);
			tasks.add(new AttrRemoveRunnable(p, attributes).runTaskLater(main, attributeTime * 20));
		}
		
		// Flags
		for (FlagAction flag : flags) {
			if (flag.isAdd()) {
				FlagManager.addFlag(p, p, flag.getFlag(), flag.getDuration() * 20);
			}
			else {
				FlagManager.removeFlag(p, flag.getFlag());
			}
		}
		
		// Buffs
		for (BuffAction buff : buffs) {
	        BuffType buffType = buff.getType();
	        int seconds = buff.getDuration();
	        String category = buff.getCategory();
	        int buffticks = seconds * 20;
            SkillBuffEvent event = new SkillBuffEvent(p, p, buff.getValue(), buffticks, buffType, buff.isPercent());
            Bukkit.getPluginManager().callEvent(event);
            
            if (!event.isCancelled()) {
            	if (category == null) {
                    BuffManager.getBuffData(p).addBuff(
                            buffType,
                            new Buff(key + "-" + p, event.getAmount(), buff.isPercent()),
                            event.getTicks());
            	}
            	else {
                    BuffManager.getBuffData(p).addBuff(
                            buffType,
                            category,
                            new Buff(key + "-" + p, event.getAmount(), buff.isPercent()),
                            event.getTicks());
            	}
            }
		}
		
		// Speed
		if (speedTime > 0) {
            AttributeListener.refreshSpeed(p);
            FlagManager.addFlag(p, p, MechanicListener.SPEED_KEY, speedTime * 20);
            p.setWalkSpeed((float) (speed * p.getWalkSpeed()));
		}

		// Health and mana regen
		PlayerData data = SkillAPI.getPlayerData(p);
		if (healthReps == 0 && !p.isDead()) {
				p.setHealth(Math.min(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), p.getHealth() + health));
		}
		else if (healthReps > 0 && !p.isDead()) {
			tasks.add(new HealthRunnable(p, health, healthReps).runTaskTimer(main, 0, healthPeriod * 20));
		}
		
		if (data.getMainClass().getData().getManaName().contains("MP")) {
			if (manaReps == 0 && !p.isDead()) {
				data.setMana(Math.min(data.getMaxMana(), data.getMana() + mana));
			}
			else if (manaReps > 0 && !p.isDead()) {
				tasks.add(new ManaRunnable(p, mana, manaReps).runTaskTimer(main, 0, manaPeriod * 20));
			}
		}
		
		if (isDuration) {
			ConsumableManager.effects.put(uuid, new DurationEffects(main, this, System.currentTimeMillis(), uuid, tasks));
		}
		item.setAmount(item.getAmount() - 1);
	}

	public void executeCommands(Player player) {
		for (String cmd : this.commands) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%p", player.getName()));
		}
	}
	
	@Override
	public ItemStack getItem(int amount) {
		ItemStack item;
		if (base64 != null) {
			item = SkullCreator.itemFromBase64(base64);
		}
		else {
			item = new ItemStack(mat);
		}
		item.setAmount(amount);
		
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		meta.setDisplayName(display);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("consumable", key);
		return nbti.getItem();
	}
	
	private String correctColors(String line) {
		return line.replaceAll("\\\\&", "@").replaceAll("&", "§").replaceAll("@", "&");
	}
	
	public void generateLore() {
		lore.clear();
		String line = "";
		
		lore.add("§7Rarity: " + rarity.getDisplay());
		
		// Health & mana
		if (this.healthReps > 1) {
			line = "§aHP +" + health + "/" + (healthPeriod == 0 ? healthPeriod + "s" : "s");
			line += " [" + healthReps + "x]";
		}
		else if (this.health > 0) {
			line = "§aHP +" + health;
		}
		if (!line.isEmpty() && this.mana > 0) {
			line += "§7, ";
		}
		if (this.manaReps > 1) {
			line += "§9MP +" + mana + "/" + (manaPeriod == 0 ? manaPeriod + "s" : "s");
			line += " [" + manaReps + "x]";
		}
		else if (this.mana > 0) {
			line = "§aMP +" + mana;
		}
		if (!line.isEmpty()) {
			lore.add(line);
		}
		// Hunger
		if (hunger > 0) {
			lore.add("§6Hunger +" + hunger + ", Saturation +" + saturation);
		}
		
		// Potions
		if (!potions.isEmpty()) {
			lore.add("§9Potions:");
			for (PotionEffect pot : potions) {
				// Only potions are in ticks because PotionEffect is a bukkit object
				lore.add("§7- §9" + pot.getType().toString() + " " + pot.getAmplifier() + " [" + (pot.getDuration() / 20) + "s]");
			}
		}
		
		// Buffs
		if (!buffs.isEmpty()) {
			for (BuffAction buff : buffs) {
				lore.add("§7- §9" + BuffAction.display.get(buff.getType()) + " " + buff.getValue() + (buff.isPercent() ? "x [" : " [") + buff.getDuration() + "s]");
			}
		}
		
		// Attributes
		if (!attributes.isEmpty()) {
			lore.add("§cAttributes [" + attributeTime + "s]:");
			for (Entry<String, Integer> ent : attributes.getAttrs().entrySet()) {
				lore.add("§c" + StringUtils.capitalize(ent.getKey()) + " +" + ent.getValue());
			}
		}
		
		if (!desc.isEmpty()) {
			lore.add("§2Desc:");
			for (String loreline : desc) {
				lore.add(loreline.replaceAll("&", "§"));
			}
		}
	}
	
	public void setDisplay(String display) {
		this.display = correctColors(display);
	}
	
	public void setDescription(List<String> desc) {
		this.desc.clear();
		for (String line : desc) {
			this.desc.add(correctColors(line));
		}
	}
	
	public void setMaterial(String mat) {
		this.mat = Material.getMaterial(mat.toUpperCase());
	}
	
	public void setBase64(String base64) {
		this.base64 = base64;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public void setHealthReps(int healthReps) {
		this.healthReps = healthReps;
		if (this.healthReps * this.healthPeriod > totalDuration) {
			totalDuration = this.healthReps * this.healthPeriod;
		}
	}

	public void setManaReps(int manaReps) {
		this.manaReps = manaReps;
		if (this.manaReps * this.manaPeriod > totalDuration) {
			totalDuration = this.manaReps * this.manaPeriod;
		}
	}

	public void setHealthPeriod(int healthPeriod) {
		this.healthPeriod = healthPeriod > 0 ? healthPeriod : 1;
		if (this.healthReps * this.healthPeriod > totalDuration) {
			totalDuration = this.healthReps * this.healthPeriod;
		}
	}

	public void setManaPeriod(int manaPeriod) {
		this.manaPeriod = manaPeriod > 0 ? manaPeriod : 1;
		if (this.manaReps * this.manaPeriod > totalDuration) {
			totalDuration = this.manaReps * this.manaPeriod;
		}
	}
	
	public void setWorlds(List<String> worlds) {
		this.worlds = (ArrayList<String>) worlds;
	}
	
	public void setCommands(List<String> commands) {
		this.commands = (ArrayList<String>) commands;
	}
	
	public void addEffect(PotionEffect eff) {
		this.potions.add(eff);
		if (eff.getDuration() / 20 > totalDuration) { 
			totalDuration = eff.getDuration() / 20;
		}
	}

	public ArrayList<PotionEffect> getEffect() {
		return this.potions;
	}

	public StoredAttributes getAttributes() {
		return this.attributes;
	}

	public ArrayList<PotionEffect> getPotions() {
		return potions;
	}

	public int getAttributeTime() {
		return attributeTime;
	}

	public void setAttributeTime(int attributeTime) {
		this.attributeTime = attributeTime;
		if (this.attributeTime > totalDuration) {
			totalDuration = this.attributeTime;
		}
	}

	public ArrayList<String> getCommands() {
		return commands;
	}
	
	public void addFlag(FlagAction flag) {
		this.flags.add(flag);
		if (flag.getDuration() > totalDuration) {
			totalDuration = flag.getDuration();
		}
	}
	
	public ArrayList<FlagAction> getFlags() {
		return flags;
	}
	
	public void addBuff(BuffAction buff) {
		this.buffs.add(buff);
		if (buff.getDuration() > totalDuration) {
			totalDuration = buff.getDuration();
		}
	}
	
	public ArrayList<BuffAction> getBuffs() {
		return buffs;
	}

	public void setSaturation(double saturation) {
		this.saturation = saturation;
	}

	public void setHunger(int hunger) {
		this.hunger = hunger;
	}
	
	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}
	
	public void setIsDuration(boolean isDuration) {
		this.isDuration = isDuration;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}
	
	public void setSpeedTime(int time) {
		this.speedTime = time;
		if (this.speedTime > totalDuration) {
			this.totalDuration = this.speedTime;
		}
	}
	
	public int getSpeedTime() {
		return this.speedTime;
	}

	public int getHealth() {
		return health;
	}

	public int getHealthReps() {
		return healthReps;
	}

	public int getHealthPeriod() {
		return healthPeriod;
	}

	public int getMana() {
		return mana;
	}

	public int getManaReps() {
		return manaReps;
	}

	public int getManaPeriod() {
		return manaPeriod;
	}
	
	public int getTotalDuration() {
		return totalDuration;
	}
	
	public boolean isDuration() {
		return isDuration;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public double getSpeed() {
		return speed;
	}
}
