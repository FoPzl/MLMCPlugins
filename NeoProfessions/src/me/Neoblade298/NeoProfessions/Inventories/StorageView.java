package me.Neoblade298.NeoProfessions.Inventories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoConsumables.SkullCreator;
import me.Neoblade298.NeoProfessions.Managers.StorageManager;
import me.Neoblade298.NeoProfessions.Storage.Sorter;
import me.Neoblade298.NeoProfessions.Storage.StoredItem;
import me.Neoblade298.NeoProfessions.Storage.StoredItemInstance;

public class StorageView extends ProfessionInventory {
	private InvSorter invsorter;
	private Player p;
	private ArrayList<StoredItemInstance> items;
	private int page = 1;
	private Sorter[] sorters;
	
	private int min, max;
	private int mode = 0;
	private static final int INFO_MODE = 0;
	private static final int DISPLAY_MODE = 1;
	
	public static final int INFO_BUTTON = 49;
	public static final int NEXT_BUTTON = 53;
	public static final int PREVIOUS_BUTTON = 45;
	public static ArrayList<String> info = new ArrayList<String>();
	
	public static final String INFO_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2RiYWYyMDNjZTlkNDliNzJjYWRlNDA1Yjg2MWFjZmU0YjY1M2RjOGM4YTQzZTgwYjY3MGZhOTdlNTYwZWZlYiJ9fX0=";
	public static final String PREV_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFjOTZhNWMzZDEzYzMxOTkxODNlMWJjN2YwODZmNTRjYTJhNjUyNzEyNjMwM2FjOGUyNWQ2M2UxNmI2NGNjZiJ9fX0=";
	public static final String NEXT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzMzYWU4ZGU3ZWQwNzllMzhkMmM4MmRkNDJiNzRjZmNiZDk0YjM0ODAzNDhkYmI1ZWNkOTNkYThiODEwMTVlMyJ9fX0=";
	
	static {
		info.add("§9§oLeft/Shift click §7§oto make 1x/10x voucher");
		info.add("§9§oRight click §7§oto show relevant recipes");
		info.add("§9§oPress 1 §7§ofor info mode (Current)");
		info.add("§9§oPress 2 §7§ofor display mode");
	}
	
	public StorageView(Player p, int min, int max) {
		this.p = p;
		this.inv = Bukkit.createInventory(p, 54, "§9Storage View");
		this.min = min;
		this.max = max;
		this.sorters = new Sorter[5];
		invsorter = new InvSorter();
		setupSorters();
		
		
		// Setup itemstacks to be used for sorting
		items = new ArrayList<StoredItemInstance>();
		if (min == -1) {
			setupItems();
		}
		else {
			setupItems(min, max);
		}
		
		sortItems();
		inv.setContents(setupAll());

		setupInventory(p, inv, this);
	}
	
	private void setupSorters() {
		// Setup sorters
		UUID uuid = p.getUniqueId();
		int namePriority = (int) StorageManager.settings.getValue(uuid, "name-priority");
		int levelPriority = (int) StorageManager.settings.getValue(uuid, "level-priority");
		int rarityPriority = (int) StorageManager.settings.getValue(uuid, "rarity-priority");
		int amountPriority = (int) StorageManager.settings.getValue(uuid, "amount-priority");
		boolean nameOrder = (boolean) StorageManager.settings.getValue(uuid, "name-order");
		boolean levelOrder = (boolean) StorageManager.settings.getValue(uuid, "level-order");
		boolean rarityOrder = (boolean) StorageManager.settings.getValue(uuid, "rarity-order");
		boolean amountOrder = (boolean) StorageManager.settings.getValue(uuid, "amount-order");
		sorters[namePriority] = new Sorter(Sorter.NAME_SORT, namePriority, nameOrder);
		sorters[levelPriority] = new Sorter(Sorter.LEVEL_SORT, levelPriority, levelOrder);
		sorters[rarityPriority] = new Sorter(Sorter.RARITY_SORT, rarityPriority, rarityOrder);
		sorters[amountPriority] = new Sorter(Sorter.AMOUNT_SORT, amountPriority, amountOrder);
		
		// Double check to make sure all sorters exist, if not, fail and default
		boolean validated = true;
		for (int i = 1; i < 5; i++) {
			if (sorters[i] == null) {
				validated = false;
				break;
			}
		}
		
		if (!validated) {
			// If failed, use default sort order
			namePriority = (int) StorageManager.settings.getDefault("name-priority");
			levelPriority = (int) StorageManager.settings.getDefault("level-priority");
			rarityPriority = (int) StorageManager.settings.getDefault("rarity-priority");
			amountPriority = (int) StorageManager.settings.getDefault("amount-priority");
			sorters[namePriority] = new Sorter(Sorter.NAME_SORT, namePriority, nameOrder);
			sorters[levelPriority] = new Sorter(Sorter.LEVEL_SORT, levelPriority, levelOrder);
			sorters[rarityPriority] = new Sorter(Sorter.RARITY_SORT, rarityPriority, rarityOrder);
			sorters[amountPriority] = new Sorter(Sorter.AMOUNT_SORT, amountPriority, amountOrder);
			
			// Reset settings to default
			Bukkit.getLogger().log(Level.WARNING, "[NeoProfessions] Not all sorters loaded, resetting " + p.getName() + " to default.");
			StorageManager.settings.resetField("name-priority", uuid);
			StorageManager.settings.resetField("level-priority", uuid);
			StorageManager.settings.resetField("rarity-priority", uuid);
			StorageManager.settings.resetField("amount-priority", uuid);
		}
	}
	
	private void setupItems() {
		ArrayList<Integer> removedItems = new ArrayList<Integer>();
		
		for (int id : StorageManager.getStorage(p).keySet()) {
			StoredItem item = StorageManager.getItem(id);
			if (item == null) {
				Bukkit.getLogger().log(Level.WARNING, "[NeoProfessions] Player " + p.getName() + " tried to load nonexistent id: " + id +". Deleting from inventory.");
				removedItems.add(id);
				continue;
			}
			
			int amount = StorageManager.getAmount(p, id);
			if (amount > 0) {
				items.add(new StoredItemInstance(item, amount));
			}
		}
		
		for (int toRemove : removedItems) {
			StorageManager.setPlayer(p, toRemove, 0);
		}
	}
	
	private void setupItems(int min, int max) {
		ArrayList<Integer> removedItems = new ArrayList<Integer>();
		
		for (int i = min; i <= StorageManager.getLimit(min); i++) {
			if (StorageManager.getItem(i) == null) {
				continue;
			}
			StoredItem item = StorageManager.getItem(i);
			if (item == null) {
				Bukkit.getLogger().log(Level.WARNING, "[NeoProfessions] Player " + p.getName() + " tried to load nonexistent id: " + i +". Deleting from inventory.");
				removedItems.add(i);
				continue;
			}
			
			int amount = StorageManager.getAmount(p, i);
			if (amount > 0) {
				items.add(new StoredItemInstance(item, amount));
			}
			
			for (int toRemove : removedItems) {
				StorageManager.setPlayer(p, toRemove, 0);
			}
		}
	}
	
	private ItemStack[] setupAll() {
		ItemStack[] contents = new ItemStack[54];
		return setupUtilityButtons(setupInventory(contents));
	}
	
	private ItemStack[] updateSlot(ItemStack[] contents, int slot) {
		// If item doesn't exist, re-setup inventory and skip the rest
		int top = (page - 1) * 45;
		if (items.get(top + slot).getAmount() <= 0) {
			items.remove(top + slot);
			return setupInventory(contents);
		}
		
		if (mode == INFO_MODE) {
			ItemStack item = items.get(top + slot).getStorageView(false);
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			for (String line : info) {
				lore.add(line);
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
			contents[slot] = item;
		}
		else if (mode == DISPLAY_MODE) {
			contents[slot] = items.get(top + slot).getStorageView(true);
		}
		return contents;
	}
	
	// Sort by rarity
	// Sort alphabetically
	// Sort by level
	private ItemStack[] setupInventory(ItemStack[] contents) {
		// Sort items on construction and on change sort type
		int count = 0;
		for (int i = (page - 1) * 45; i < 45 * page; i++) {
			if (items.size() <= i) {
				break;
			}
			
			if (mode == INFO_MODE) {
				ItemStack item = items.get(i).getStorageView(false);
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.getLore();
				for (String line : info) {
					lore.add(line);
				}
				meta.setLore(lore);
				item.setItemMeta(meta);
				contents[count++] = item;
			}
			else if (mode == DISPLAY_MODE) {
				contents[count++] = items.get(i).getStorageView(true);
			}
		}
		
		return contents;
	}
	
	private ItemStack[] setupUtilityButtons(ItemStack[] contents) {
		contents[INFO_BUTTON] = createInfoItem();
		contents[RecipeView.HOME_BUTTON] = createHomeItem();
		if (page > 1) {
			contents[PREVIOUS_BUTTON] = createPreviousButton();
		}
		if (items.size() > page * 45) {
			contents[NEXT_BUTTON] = createNextButton();
		}
		
		int sortNum = 1;
		for (int i = 47; i <= 51; i++) {
			if (i == INFO_BUTTON) {
				continue;
			}
			contents[i] = sorters[sortNum].createSortButton();
			sortNum++;
		}
		
		return contents;
	}
	
	private ItemStack createInfoItem() {
		ItemStack item = SkullCreator.itemFromBase64(INFO_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Info");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§9§oLeft/Shift click §7§oto make 1x/10x voucher");
		lore.add("§9§oRight click §7§oto show relevant recipes");
		lore.add("§9§oPress 1 §7§ofor info mode");
		lore.add("§9§oPress 2 §7§ofor display mode");
		meta.setLore(lore);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("type", "info");
		return nbti.getItem();
	}
	
	private ItemStack createHomeItem() {
		ItemStack item = SkullCreator.itemFromBase64(RecipeView.HOUSE_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Back");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7§oReturn to storage selection");
		meta.setLore(lore);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("type", "home");
		return nbti.getItem();
	}
	
	private ItemStack createPreviousButton() {
		ItemStack item = SkullCreator.itemFromBase64(PREV_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Previous Page");
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("type", "previous");
		return nbti.getItem();
	}
	
	private ItemStack createNextButton() {
		ItemStack item = SkullCreator.itemFromBase64(NEXT_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Next Page");
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("type", "next");
		return nbti.getItem();
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent e) {
		e.setCancelled(true);
		int slot = e.getRawSlot();
		
		// Hotbar actions can be done anywhere in the inv
		if (e.getClick().equals(ClickType.NUMBER_KEY)) {
			int hotbar = e.getHotbarButton() + 1;
			if (slot < 45 && (hotbar == 1 || hotbar == 2)) {
				changeMode(hotbar);
				return;
			}
		}
		
		ItemStack item = e.getCurrentItem();
		if (item == null || item.getType().isAir()) {
			return;
		}
		NBTItem nbti = new NBTItem(item);
		String type = nbti.getString("type");
		
		if (type.equals("next")) {
			page++;
			inv.setContents(setupAll());
			return;
		}
		else if (type.equals("previous")) {
			page--;
			inv.setContents(setupAll());
			return;
		}
		else if (type.equals("home")) {
			new StorageSelectInventory(p);
			return;
		}
		
		if (e.getClick().equals(ClickType.LEFT)) {
			if (slot < 45) {
				createVoucher(p, 1, slot);
			}
			else if (type.equals("sort")) {
				changeSortOrder(nbti.getInteger("priority"));
			}
		}
		else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
			if (slot < 45) {
				createVoucher(p, 10, slot);
			}
		}
		else if (e.getClick().equals(ClickType.RIGHT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) {
			if (slot < 45) {
				viewRecipes(p, slot);
			}
		}
		else if (e.getClick().equals(ClickType.NUMBER_KEY)) {
			int hotbar = e.getHotbarButton() + 1;
			if (type.equals("sort")) {
				changeSortPriority(nbti.getInteger("priority"), hotbar);
			}
			else if (hotbar == 1 || hotbar == 2) {
				changeMode(hotbar);
			}
		}
	}

	@Override
	public void handleInventoryDrag(InventoryDragEvent e) {
		e.setCancelled(true);
	}
	
	private void createVoucher(Player p, int amount, int slot) {
		int siSlot = ((page - 1) * 45) + slot;
		if (this.items.size() <= siSlot) return;
		StoredItemInstance si = this.items.get(siSlot);
		if (si.giveVoucher(p, amount)) {
			p.sendMessage("§4[§c§lMLMC§4] §7Successfully created voucher for " + si.getItem().getDisplay() + " §fx" + amount + "§7!");
		}
		inv.setContents(updateSlot(inv.getContents(), slot));
	}
	
	private void viewRecipes(Player p, int slot) {
		StoredItemInstance si = this.items.get(((page - 1) * 45) + slot);
		if (si.getItem().getRelevantRecipes().size() > 0) {
			new RecipeView(p, si.getItem(), min, max, this.getClass().getName());
		}
	}
	
	private void changeMode(int hotbar) {
		mode = hotbar - 1;
		inv.setContents(setupInventory(inv.getContents()));
	}
	
	private void changeSortOrder(int priority) {
		Sorter sorter = sorters[priority];
		boolean newOrder = sorter.flipOrder();
		StorageManager.settings.changeField(sorter.getSettingString() + "-order", Boolean.toString(newOrder), p.getUniqueId());
		
		sortItems();
		inv.setContents(setupAll());
	}
	
	private void changeSortPriority(int oldPriority, int hotbar) {
		if (hotbar > 4 || hotbar < 1) {
			return;
		}
		if (oldPriority == hotbar) {
			return;
		}
		
		Sorter toChange = sorters[oldPriority];
		Sorter changingWith = sorters[hotbar];
		StorageManager.settings.changeField(toChange.getSettingString() + "-priority", Integer.toString(hotbar), p.getUniqueId());
		StorageManager.settings.changeField(changingWith.getSettingString() + "-priority", Integer.toString(oldPriority), p.getUniqueId());
		
		changingWith.setPriority(oldPriority);
		toChange.setPriority(hotbar);
		sorters[oldPriority] = changingWith;
		sorters[hotbar] = toChange;
		p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
		sortItems();
		inv.setContents(setupAll());
	}
	
	private void sortItems() {
		Collections.sort(items, invsorter);
	}
	 
	private class InvSorter implements Comparator<StoredItemInstance> {
	    public int compare(StoredItemInstance a, StoredItemInstance b)
	    {
	    	int comp = 0;
	    	for (int i = 1; i <= 4; i++) {
	    		comp = sorters[i].compare(a, b);
	    		if (comp != 0) {
	    			break;
	    		}
	    	}
	    	return comp;
	    }
	}


	@Override
	public void handleInventoryClose(InventoryCloseEvent e) {}
}
