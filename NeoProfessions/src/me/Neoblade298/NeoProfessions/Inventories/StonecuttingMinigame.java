package me.Neoblade298.NeoProfessions.Inventories;

import java.util.ArrayList;
import java.util.HashMap;
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

import me.Neoblade298.NeoProfessions.Professions;
import me.Neoblade298.NeoProfessions.Managers.GardenManager;
import me.Neoblade298.NeoProfessions.Managers.MinigameManager;
import me.Neoblade298.NeoProfessions.Managers.ProfessionManager;
import me.Neoblade298.NeoProfessions.Managers.StorageManager;
import me.Neoblade298.NeoProfessions.Minigames.MinigameDrop;
import me.Neoblade298.NeoProfessions.PlayerProfessions.ProfessionType;
import me.Neoblade298.NeoProfessions.Storage.StoredItemInstance;

public class StonecuttingMinigame extends ProfessionInventory {
	private static float ERROR = 0.594604F;
	Professions main;
	Player p;
	int stage = 0;
	ArrayList<MinigameDrop> drops;
	int difficulty;
	HashMap<Integer, MinigameDrop> hiddenItems;
	ArrayList<MinigameDrop> rewards = new ArrayList<MinigameDrop>();
	
	private static int NUM_FLASHES = 3;
	private int flashTime = 20;
	private static int DELAY_AFTER_FLASH = 20;
	
	private int totalRewardsShown = 0;
	private int numSuccesses = 0;
	private int dropNum = 0;
	private int flashNum = 0;
	private int calculateFlashNum = 0;
	private int totalDelay = 20;
	private int numRewardsShown;
	
	public StonecuttingMinigame(Professions main, Player p, ArrayList<MinigameDrop> drops, String name, int difficulty) {
		this.main = main;
		this.p = p;
		this.drops = drops;
		this.difficulty = difficulty;
		this.flashTime = 20 - difficulty;
		inv = Bukkit.createInventory(p, 45, name.replaceAll("&", "§"));
		
		ItemStack[] contents = inv.getContents();
		for (int i = 11; i <= 15; i++) {
			for (int j = 0; j < 3; j++) {
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
		lore.add("§7The screen will flash " + NUM_FLASHES + " times.");
		lore.add("§7During this, up to §e" + drops.size() + " §7drops");
		lore.add("§7will show up. Click them! If you miss");
		lore.add("§7and click a glass pane, it's game over.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack generateSuccess() {
		ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§aSuccess!");
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack generateEmpty(Material mat) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack generateGameOver() {
		ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§7Game Over!");
		item.setItemMeta(meta);
		return item;
	}
	
	private int getQuadrant(int slot) {
		int x = slot % 9;
		int y = slot / 9;
		if (y <= 1) {
			if (x <= 3) {
				return 0;
			}
			else if (x >= 4) {
				return 1;
			}
		}
		else if (y >= 3) {
			if (x <= 3) {
				return 2;
			}
			else if (x >= 4) {
				return 3;
			}
		}
		return -1;
	}
	
	private void fillQuadrant(int quadrant, ItemStack[] contents, ItemStack fill) {
		int x = quadrant % 2;
		int y = quadrant / 2;
		fillQuadrant(x, y, contents, fill);
	}
	
	private void fillAllQuadrants(ItemStack[] contents, ItemStack fill) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				fillQuadrant(i, j, contents, fill);
			}
		}
	}
	
	private void fillQuadrant(int x, int y, ItemStack[] contents, ItemStack fill) {
		int startx = (x * 4) + x;
		int starty = (y * 2) + y;
		for (int i = startx; i < startx + 4; i++) {
			for (int j = starty ; j < starty + 2; j++) {
				contents[(j * 9) + i] = fill;
			}
		}
	}
	
	private void fillBorders(ItemStack[] contents, ItemStack fill) {
		for (int i = 0; i < 9; i++) {
			contents[18 + i] = fill;
		}
		for (int i = 0; i < 5; i++) {
			contents[(i * 9) + 4] = fill;
		}
	}
	
	private ItemStack generateDrop(MinigameDrop drop) {
		return drop.getItem().getBaseView();
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
			int slot = e.getRawSlot();
			if (item.getType().toString().endsWith("PANE")) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, ERROR);
				endGame();
			}
			else {
				// Success
				ItemStack[] contents = inv.getContents();
				int quadrant = getQuadrant(slot);
				fillQuadrant(quadrant, contents, generateSuccess());
				inv.setContents(contents);
				int key = flashNum * 4 + quadrant;
				MinigameDrop drop = hiddenItems.get(key);
				rewards.add(drop);
				p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
				numSuccesses++;
				if (numSuccesses >= drops.size()) {
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
		ItemStack[] contents = new ItemStack[45];
		hiddenItems = new HashMap<Integer, MinigameDrop>();
		// Generate which flash and which quadrant each drop appears
		ThreadLocalRandom.current().ints(0, NUM_FLASHES * 4).distinct().limit(drops.size()).forEach(num -> {
			// divide by NUM_FLASHES to get what flash it's on, mod by NUM_FLASHES for which quadrant
			hiddenItems.put(num, drops.get(dropNum));
			dropNum++;
		});
		fillBorders(contents, generateEmpty(Material.GRAY_STAINED_GLASS_PANE));
		fillAllQuadrants(contents, generateEmpty(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
		inv.setContents(contents);
		
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, 0.5F);
		
		// Each flash will be 20 + rand after the previous, they can be up to 60 ticks after the previous
		ThreadLocalRandom.current().ints(0, 40).distinct().limit(drops.size()).forEach(num -> {
			// Runnable 1, flash
			for (int i = 0; i < 4; i++) {
				if (hiddenItems.containsKey(4 * calculateFlashNum + i)) {
					numRewardsShown++;
				}
			}
			new BukkitRunnable() {
				public void run() {
					if (stage == 2) {
						return;
					}
					if (totalRewardsShown >= drops.size()) {
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, 2.0F);
						endGame();
						return;
					}
					for (int i = 0; i < 4; i++) {
						ItemStack[] contents = inv.getContents();
						if (hiddenItems.containsKey(4 * flashNum + i)) {
							fillQuadrant(i, contents, generateDrop(hiddenItems.get(4 * flashNum + i)));
							totalRewardsShown++;
						}
						else {
							fillQuadrant(i, contents, generateEmpty(Material.RED_STAINED_GLASS_PANE));
						}
						inv.setContents(contents);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, 2.0F);
					}
				}
			}.runTaskLater(MinigameManager.main, totalDelay + num);
			
			totalDelay += num;
			int calculatedFlashTime = (int) (numRewardsShown > 0 ?
					flashTime * (0.5 + (0.5 * numRewardsShown)) : flashTime);
			new BukkitRunnable() {
				public void run() {
					if (stage == 2) {
						return;
					}
					flashNum++;
					for (int i = 0; i < 4; i++) {
						ItemStack[] contents = inv.getContents();
						fillAllQuadrants(contents, generateEmpty(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
						inv.setContents(contents);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, 1.0F);
					}
				}
			}.runTaskLater(MinigameManager.main, totalDelay + calculatedFlashTime);
			totalDelay += calculatedFlashTime + DELAY_AFTER_FLASH;
			numRewardsShown = 0;
			calculateFlashNum++;
		});
		
		// Game over, all 5 flashes completed
		new BukkitRunnable() {
			public void run() {
				if (stage == 2) {
					return;
				}
				endGame();
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, 1.0F);
			}
		}.runTaskLater(MinigameManager.main, totalDelay + DELAY_AFTER_FLASH);
	}
	
	private void endGame() {
		stage = 2;
		ItemStack[] contents = new ItemStack[45];
		for (int i = 0; i < 45; i++) {
			contents[i] = generateGameOver();
		}
		inv.setContents(contents);
		
		giveRewards();
		
		new BukkitRunnable() {
			public void run() {
				if (p.getOpenInventory().getTopInventory() == inv) {
					p.closeInventory();
				}
				GardenManager.tryReturnToGarden(p, ProfessionType.STONECUTTER);
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
		ProfessionManager.getAccount(p.getUniqueId()).get(ProfessionType.STONECUTTER).addExp(p, totalExp);
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
}
