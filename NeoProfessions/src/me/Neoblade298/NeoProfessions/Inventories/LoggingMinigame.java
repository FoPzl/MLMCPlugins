package me.Neoblade298.NeoProfessions.Inventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoProfessions.Professions;
import me.Neoblade298.NeoProfessions.Managers.GardenManager;
import me.Neoblade298.NeoProfessions.Managers.MinigameManager;
import me.Neoblade298.NeoProfessions.Managers.ProfessionManager;
import me.Neoblade298.NeoProfessions.Managers.StorageManager;
import me.Neoblade298.NeoProfessions.Minigames.MinigameDrop;
import me.Neoblade298.NeoProfessions.PlayerProfessions.ProfessionType;
import me.Neoblade298.NeoProfessions.Storage.StoredItemInstance;

public class LoggingMinigame extends ProfessionInventory {
	private static float ERROR = 0.594604F;
	private static int TOTAL_TICKS = 10;
	private static int END_TICKS = 3;
	int tickrate;
	Professions main;
	Player p;
	int stage = 0;
	ArrayList<MinigameDrop> drops;
	ArrayList<FallingItem> falling = new ArrayList<FallingItem>();
	double baseSpawnChance = 0.3;
	int difficulty;
	HashMap<Integer, MinigameDrop> items, itemsAfter; // Need 2 because 1 gets its items removed
	ArrayList<ItemStack> toSpawn = new ArrayList<ItemStack>();
	int dropsPast = 0;
	ArrayList<MinigameDrop> rewards = new ArrayList<MinigameDrop>();
	
	private int dropNum = 0;
	
	public LoggingMinigame(Professions main, Player p, ArrayList<MinigameDrop> drops, String name, int difficulty) {
		this.main = main;
		this.p = p;
		this.drops = drops;
		this.difficulty = difficulty;
		this.tickrate = 18 - (difficulty / 2);
		baseSpawnChance += difficulty * 0.15;
		inv = Bukkit.createInventory(p, 54, name.replaceAll("&", "§"));
		
		ItemStack[] contents = inv.getContents();
		for (int i = 11; i <= 15; i++) {
			for (int j = 0; j < 4; j++) {
				contents[i + (j*9)] = generateStartButton();
			}
		}
		inv.setContents(contents);

		setupInventory(p, inv, this);
	}
	
	private ItemStack generateStartButton() {
		ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§aClick to start!");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7§l§nInstructions");
		lore.add("§e" + drops.size() + " §7items will drop from the");
		lore.add("§7top. Click them to get them, but don't");
		lore.add("§7click any of the beehives!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack generateBeehive() {
		ItemStack item = new ItemStack(Material.BEE_NEST);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§cDon't click me!");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§cBzzzz");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack generateGameOver() {
		ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§7Game over!");
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack generateDrop(MinigameDrop drop, int tick) {
		ItemStack item = drop.getItem().getBaseView();
		NBTItem nbti = new NBTItem(item);
		nbti.setInteger("tick", tick);
		return nbti.getItem();
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent e) {
		e.setCancelled(true);
		ItemStack item = e.getCurrentItem();
		if (item == null || item.getType().isAir()) {
			return;
		}
		
		// Not yet started
		if (stage == 0) {
			// In stage 0, if item != null, it's the start button
			startGame();
		}
		
		else if (stage == 1) {
			if (item.getType().equals(Material.BEE_NEST)) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, ERROR);
				endGame();
			}
			else {
				p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
				NBTItem nbti = new NBTItem(item);
				rewards.add(itemsAfter.get(nbti.getInteger("tick")));
				dropsPast++;
				ItemStack[] contents = inv.getContents();
				contents[e.getRawSlot()] = null;
				inv.setContents(contents);
				if (dropsPast == drops.size()) {
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
					endGame();
				}
			}
		}
	}

	@Override
	public void handleInventoryDrag(InventoryDragEvent e) {
		e.setCancelled(true);
	}
	
	private void startGame() {
		stage = 1;
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, 1.0F);
		ItemStack[] contents = new ItemStack[54];
		inv.setContents(contents);
		items = new HashMap<Integer, MinigameDrop>();
		itemsAfter = new HashMap<Integer, MinigameDrop>();
		ThreadLocalRandom.current().ints(0, TOTAL_TICKS).distinct().limit(drops.size()).forEach(num -> {
			int key = num * tickrate;
			items.put(key, drops.get(dropNum));
			itemsAfter.put(key, drops.get(dropNum));
			dropNum++;
		});
		
		new BukkitRunnable() {
			int tick = 0;
			public void run() {
				if (stage == 2) {
					this.cancel();
					return;
				}
				moveRowsDown();
				spawnRow(tick);
				tick += tickrate;
				if (tick >= (tickrate * TOTAL_TICKS) + (tickrate * END_TICKS)) {
					this.cancel();
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, ERROR);
					endGame();
				}
			}
		}.runTaskTimer(MinigameManager.main, 0, tickrate);
	}
	
	private void endGame() {
		// Make sure endgame doesn't happen twice
		stage = 2;
		ItemStack[] contents = new ItemStack[54];
		for (int i = 0; i < 54; i++) {
			contents[i] = generateGameOver();
		}
		inv.setContents(contents);
		
		giveRewards();
		
		new BukkitRunnable() {
			public void run() {
				if (p.getOpenInventory().getTopInventory() == inv) {
					p.closeInventory();
				}
				GardenManager.tryReturnToGarden(p, ProfessionType.LOGGER);
			}
		}.runTaskLater(main, 40);
	}
	
	private void giveRewards() {
		// Give rewards
		int totalExp = 0;
		HashMap<Integer, Integer> items = new HashMap<Integer, Integer>(); 
		for (MinigameDrop drop : rewards) {
			StoredItemInstance si = drop.getItem();
			int id = si.getItem().getId();
			int amount = si.getAmount();
			items.put(id, items.getOrDefault(id, 0) + amount);
			totalExp += drop.getExp() * amount;
		}
		
		for (Entry<Integer, Integer> e : items.entrySet()) {
			StorageManager.givePlayer(p, e.getKey(), e.getValue());
		}
		ProfessionManager.getAccount(p.getUniqueId()).get(ProfessionType.LOGGER).addExp(p, totalExp);
	}

	@Override
	public void handleInventoryClose(InventoryCloseEvent e) {
		ProfessionInventory thisInv = this;
		if (stage < 2) {
			new BukkitRunnable() {
				public void run() {
					p.openInventory(inv);
					Professions.viewingInventory.put(p, thisInv);
				}
			}.runTask(MinigameManager.main);
		}
	}
	
	private void moveRowsDown() {
		ItemStack[] contents = inv.getContents();
		Iterator<FallingItem> iter = falling.iterator();
		while (iter.hasNext()) {
			FallingItem fi = iter.next();
			if (fi.pos >= 54 || contents[fi.pos] == null) {
				if (fi.pos >= 54 && new NBTItem(fi.item).hasKey("tick")) {
					dropsPast++;
				}
				iter.remove();
			}
			else {
				fi.moveDown(contents);
			}
		}
		if (dropsPast == drops.size()) {
			endGame();
			return;
		}
		inv.setContents(contents);
	}
	
	private void spawnRow(int tick) {
		if (stage == 2) {
			return;
		}
		double spawnChance = this.baseSpawnChance;
		spawnChance -= Professions.gen.nextDouble();
		int sizeLimit = items.containsKey(tick) ? 8 : 9;
		while (spawnChance >= 0 && toSpawn.size() < sizeLimit) {
			toSpawn.add(generateBeehive());
			spawnChance -= Professions.gen.nextDouble();
		}
		
		int numToSpawn = toSpawn.size() + (items.containsKey(tick) ? 1 : 0);
		ItemStack[] contents = inv.getContents();
		ThreadLocalRandom.current().ints(0, 18).distinct().limit(numToSpawn).forEach(num -> {
			ItemStack item;
			if (items.containsKey(tick)) {
				item = generateDrop(items.get(tick), tick);
				items.remove(tick);
			}
			else {
				item = toSpawn.remove(0);
			}
			falling.add(new FallingItem(num, item, contents));
		});
		inv.setContents(contents);
	}
	
	private class FallingItem {
		int pos;
		ItemStack item;
		public FallingItem(int startPos, ItemStack item, ItemStack[] contents) {
			this.pos = startPos;
			this.item = item;
			contents[this.pos] = item;
		}
		
		public void moveDown(ItemStack[] contents) {
			contents[pos] = null;
			pos = pos + 18;
			if (pos < 54) {
				contents[pos] = item;
			}
		}
	}
}
