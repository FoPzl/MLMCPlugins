package me.Neoblade298.NeoConsumables.objects;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoConsumables.Consumables;
import me.Neoblade298.NeoConsumables.bosschests.ChestReward;
import me.Neoblade298.NeoConsumables.bosschests.ChestStage;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.instancing.InstanceType;

public class ChestConsumable extends Consumable implements GeneratableConsumable {
	private static Random gen = new Random();
	private LinkedList<ChestStage> stages;
	private Sound sound;
	private ArrayList<String> lore = null;
	private String display;
	private int level;
	private static HashMap<Integer, ChatColor> stageToColor = new HashMap<Integer, ChatColor>();
	private static HashMap<Integer, Character> stageToLetter = new HashMap<Integer, Character>();
	private static DecimalFormat df = new DecimalFormat("#.#");
	
	static {
		stageToColor.put(1, ChatColor.GRAY);
		stageToColor.put(2, ChatColor.GREEN);
		stageToColor.put(3, ChatColor.BLUE);
		stageToColor.put(4, ChatColor.DARK_RED);
		stageToLetter.put(1, 'C');
		stageToLetter.put(2, 'B');
		stageToLetter.put(3, 'A');
		stageToLetter.put(4, 'S');
	}
	
	public ChestConsumable(Consumables main, String display, String key, int level, LinkedList<ChestStage> stages, Sound sound) {
		super(main, key);
		this.main = main;
		this.level = level;
		this.stages = stages;
		this.sound = sound;
		this.display = display;
	}
	
	@Override
	public String getDisplay() {
		return display;
	}
	
	public void useChest(Player p) {
		long delay = 0;
		p.playSound(p.getLocation(), sound, 1.0F, 1.0F);
		for (ChestStage stage : stages) {
			// Calculate if it will happen
			if (stage.getChance() < gen.nextDouble()) {
				continue;
			}

			delay += 20;
			BukkitRunnable runStage = new BukkitRunnable() {
				public void run() {
					p.playSound(p.getLocation(), stage.getSound(), 1.0F, stage.getPitch());
					ChestReward reward = stage.chooseReward();
					reward.sendMessage(p);
					reward.giveReward(p, level);
					if (stage.getEffect() != null) {
						ChestConsumable.runAnimation(p, stage.getEffect());
					}
				}
			};
			runStage.runTaskLater(main, delay);
		}
		
		// Extra 20 ticks after last item, then 10 ticks to close the animation
		final long fDelay = delay + 30;
		
		// Add chest animation
		BukkitRunnable chestAnimation = new BukkitRunnable() {
			int tick = 0;
			public void run() {
				if (tick >= fDelay - 10) {
					this.cancel();
				}
				tick += 2;
				Location loc = p.getLocation();
				loc.setY(loc.getY() + 1);
		        Vector dir = loc.getDirection().setY(0).normalize().multiply(4);
				p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc.add(dir), 5, 0.1, 0.1, 0.1, 0); 
			}
		};
		chestAnimation.runTaskTimer(main, 0, 2);
		
		BukkitRunnable endSound = new BukkitRunnable() {
			public void run() {
				p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0F, 1.0F);
			}
		};
		endSound.runTaskLater(main, fDelay - 10);
		
		BukkitRunnable endAnimation = new BukkitRunnable() {
			double distance = 4;
			public void run() {
				if (distance <= 0) {
					this.cancel();
				}
				else {
					distance -= 0.8;
					Location loc = p.getLocation();
					loc.setY(loc.getY() + 1);
			        Vector dir = loc.getDirection().setY(0).normalize().multiply(distance);
					p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc.add(dir), 5, 0.1, 0.1, 0.1, 0);
				}
			}
		};
		endAnimation.runTaskTimer(main, fDelay - 10, 2);
		
		BukkitRunnable endParticle = new BukkitRunnable() {
			public void run() {
				Location loc = p.getLocation();
				loc.setY(loc.getY() + 1);
		        Vector dir = loc.getDirection().setY(0).normalize().multiply(0.5);
				p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.add(dir), 100, 0.6, 0.6, 0.6, 0);
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
			}
		};
		endParticle.runTaskLater(main, fDelay);
	}
	
	private static void runAnimation(Player p, String type) {
		Particle part = Particle.CRIT_MAGIC;
		int amount = 50;
		if (type.equalsIgnoreCase("endrod")) {
			part = Particle.END_ROD;
			amount = 100;
		}
		else if (type.equalsIgnoreCase("fire")) {
			part = Particle.FLAME;
			amount = 200;
		}
		else if (type.equalsIgnoreCase("witch")) {
			part = Particle.SPELL_WITCH;
			amount = 500;
		}
		
		Location loc = p.getLocation();
		loc.setY(loc.getY() + 1);
        Vector dir = loc.getDirection().setY(0).normalize().multiply(4);
		p.getWorld().spawnParticle(part, loc.add(dir), amount, 0.4, 0.4, 0.4, 0.2);
	}

	@Override
	public boolean canUse(Player p, ItemStack item) {
		if (NeoCore.getInstanceType() == InstanceType.OTHER) {
			p.sendMessage("&cYou cannot open chests in a boss fight!".replaceAll("&", "§"));
			return false;
		}
		return true;
	}

	@Override
	public void use(Player p, ItemStack item) {
		p.sendMessage("§4[§c§lMLMC§4] §7You opened " + item.getItemMeta().getDisplayName() + "§7 to find...");
		for (Sound sound : getSounds()) {
			p.getWorld().playSound(p.getEyeLocation(), sound, 1.0F, 1.0F);
		}
		item.setAmount(item.getAmount() - 1);
		useChest(p);
		return;
	}
	
	private void generateLore() {
		lore = new ArrayList<String>();
		lore.add("§7§oUp to 1 item per letter tier");
		lore.add("§7[§ePotential Rewards§7]");
		Iterator<ChestStage> iter = stages.iterator();
		int stageNum = 0;
		while (iter.hasNext()) {
			stageNum++;
			ChestStage stage = iter.next();
			double chance = stage.getChance();
			double totalWeight = stage.getTotalWeight();
			ChatColor col = stageToColor.get(stageNum);
			char letter = stageToLetter.get(stageNum);
			for (ChestReward rew : stage.getRewards()) {
				double pct = (rew.getWeight() / totalWeight) * chance;
				lore.add(col + ChatColor.BOLD.toString() + "{" + letter + "} " + col + rew.toString() + " §7(" + df.format(pct * 100) + "%)");
			}
		}
	}
	
	@Override
	public ItemStack getItem(int amount) {
		if (lore == null) {
			generateLore();
		}
		ItemStack item = new ItemStack(Material.CHEST);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(display);
		meta.setLore(lore);
		item.setItemMeta(meta);
		item.setAmount(amount);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("consumable", key);
		return nbti.getItem();
	}
}
