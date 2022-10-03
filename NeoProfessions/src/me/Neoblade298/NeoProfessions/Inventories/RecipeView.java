package me.Neoblade298.NeoProfessions.Inventories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoProfessions.Professions;
import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;
import me.Neoblade298.NeoProfessions.Managers.RecipeManager;
import me.Neoblade298.NeoProfessions.Recipes.GearResult;
import me.Neoblade298.NeoProfessions.Recipes.Recipe;
import me.Neoblade298.NeoProfessions.Storage.StoredItem;
import me.Neoblade298.NeoProfessions.Utilities.SkullCreator;

public class RecipeView extends ProfessionInventory {
	private Player p;
	private ArrayList<Recipe> recipes;
	StoredItem base;
	private int page = 1;
	private int mode = 0;
	private RecipeSorter rsorter;
	private String name;
	private List<String> recipeList;
	private String returnTo;
	
	private int min, max;
	
	private static final int CRAFTING_MODE = 0;
	private static final int RESULT_MODE = 1;
	private static final int AUGMENT_MODE = 2;
	public static ArrayList<String> info = new ArrayList<String>();
	
	public static final String HOUSE_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzVhMzViNWNhMTUyNjg2ODVjNDY2MDUzNWU1ODgzZDIxYTVlYzU3YzU1ZDM5NzIzNDI2OWFjYjVkYzI5NTRmIn19fQ==";
	
	public static final int INFO_BUTTON = 49;
	public static final int NEXT_BUTTON = 53;
	public static final int PREVIOUS_BUTTON = 45;
	public static final int HOME_BUTTON = 46;
	
	static {
		info.add("§9§oLeft/Shift click §7§oto craft 1x/10x");
		info.add("§9§oRight click §7§oto view components");
		info.add("§9§oPress 1 §7§ofor crafting mode (Current)");
		info.add("§9§oPress 2 §7§ofor result mode");
		info.add("§9§oPress 3 §7§ofor augment mode");
	}
	
	public RecipeView(Player p, String name, List<String> recipeList, String returnTo) {
		this.p = p;
		this.inv = Bukkit.createInventory(p, 54, "§9Recipe View: " + name + " Recipes");
		this.name = name;
		this.returnTo = returnTo;
		this.recipeList = recipeList;
		rsorter = new RecipeSorter();
		
		// Setup recipes relevant to the base
		recipes = new ArrayList<Recipe>();
		for (String key : recipeList) {
			Recipe recipe = RecipeManager.getRecipe(key);
			if (recipe == null) {
				Bukkit.getLogger().log(Level.WARNING, "[NeoProfessions] Failed to load recipe " + key + " in RecipeView. Skipping.");
				continue;
			}
			recipes.add(recipe);
		}
		Collections.sort(recipes, rsorter);
		inv.setContents(setupAll());

		setupInventory(p, inv, this);
	}
	
	public RecipeView(Player p, StoredItem base, int min, int max, String returnTo) {
		this.p = p;
		this.inv = Bukkit.createInventory(p, 54, "§9Recipe View: " + base.getDisplay());
		this.returnTo = returnTo;
		p.openInventory(inv);
		this.base = base;
		rsorter = new RecipeSorter();
		
		this.min = min;
		this.max = max;
		
		// Setup recipes relevant to the base
		recipes = new ArrayList<Recipe>();
		for (String key : base.getRelevantRecipes()) {
			Recipe recipe = RecipeManager.getRecipe(key);
			if (recipe == null) {
				Bukkit.getLogger().log(Level.WARNING, "[NeoProfessions] Failed to load recipe " + key + " in RecipeView. Skipping.");
				continue;
			}
			recipes.add(recipe);
		}
		Collections.sort(recipes, rsorter);
		
		inv.setContents(new ItemStack[54]);
		inv.setContents(setupAll());
		Professions.viewingInventory.put(p, this);
	}
	
	private ItemStack[] setupAll() {
		ItemStack[] contents = new ItemStack[54];
		return setupUtilityButtons(setupInventory(contents));
	}
	
	// Sort by rarity
	// Sort alphabetically
	// Sort by level
	private ItemStack[] setupInventory(ItemStack[] contents) {
		// Sort items on construction and on change sort type
		int count = 0;
		for (int i = (page - 1) * 45; i < 45 * page; i++) {
			if (recipes.size() <= i) {
				break;
			}
			
			Recipe recipe = recipes.get(i);
			if (mode == CRAFTING_MODE) {
				ItemStack item = recipe.getReqsIcon(p, recipe.canCraft(p));
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.getLore();
				for (String line : info) {
					lore.add(line);
				}
				meta.setLore(lore);
				item.setItemMeta(meta);
				contents[count++] = item;
			}
			else if (mode == RESULT_MODE) {
				contents[count++] = recipe.getResult().getResultItem(p, recipe.canCraft(p));
			}
			else if (mode == AUGMENT_MODE) {
				ItemStack item = recipe.getResult().getResultItem(p, recipe.canCraft(p));
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList<String>();
				if (recipe.getResult() instanceof GearResult) {
					NBTItem nbti = new NBTItem(recipe.getResult().getResultItem(p, recipe.canCraft(p)));
					for (int slot = 1; slot <= nbti.getInteger("slotsCreated"); slot++) {
						if (nbti.hasKey("slot" + slot + "Augment")) {
							Augment aug = AugmentManager.getFromCache(nbti.getString("slot" + slot + "Augment"), nbti.getInteger("slot" + slot + "Level"));
							lore.add(aug.getLine());
							for (String line : aug.getItem(p).getItemMeta().getLore()) {
								lore.add(line);
							}
						}
					}
				}
				if (lore.isEmpty()) {
					lore.add("§7This item has no augments.");
				}
				meta.setLore(lore);
				item.setItemMeta(meta);
				contents[count++] = item;
			}
		}	
		
		return contents;
	}
	
	private ItemStack[] setupUtilityButtons(ItemStack[] contents) {
		contents[INFO_BUTTON] = createInfoItem();
		contents[HOME_BUTTON] = createHomeItem();
		if (page > 1) {
			contents[PREVIOUS_BUTTON] = createPreviousButton();
		}
		if (recipes.size() > page * 45) {
			contents[NEXT_BUTTON] = createNextButton();
		}
		
		return contents;
	}
	
	private ItemStack createInfoItem() {
		ItemStack item = SkullCreator.itemFromBase64(StorageView.INFO_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Info");
		ArrayList<String> lore = new ArrayList<String>();
		if (base != null) {
			lore.add("§7Recipes with: " + base.getDisplay());
		}
		lore.add("§9§oLeft/Shift click §7§oto craft 1x/10x");
		lore.add("§9§oRight click §7§oto view components");
		lore.add("§9§oPress 1 §7§ofor crafting mode");
		lore.add("§9§oPress 2 §7§ofor result mode");
		lore.add("§9§oPress 3 §7§ofor augment mode");
		meta.setLore(lore);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("type", "info");
		return nbti.getItem();
	}
	
	private ItemStack createHomeItem() {
		ItemStack item = SkullCreator.itemFromBase64(HOUSE_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Back");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7§oPrevious Menu");
		meta.setLore(lore);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("type", "home");
		return nbti.getItem();
	}
	
	private ItemStack createPreviousButton() {
		ItemStack item = SkullCreator.itemFromBase64(StorageView.PREV_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9Previous Page");
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("type", "previous");
		return nbti.getItem();
	}
	
	private ItemStack createNextButton() {
		ItemStack item = SkullCreator.itemFromBase64(StorageView.NEXT_HEAD);
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
		
		// Hotbar actions can be done anywhere in the inv
		if (e.getClick().equals(ClickType.NUMBER_KEY)) {
			int hotbar = e.getHotbarButton();
			if (hotbar >= 1 && hotbar <= 3) {
				changeMode(hotbar);
			}
		}
		
		ItemStack item = e.getCurrentItem();
		if (item == null || item.getType().isAir()) {
			return;
		}
		NBTItem nbti = new NBTItem(item);
		int slot = e.getRawSlot();
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
			if (this.returnTo == null) {
				return;
			}
			else if (this.returnTo.equals(StorageView.class.getName())) {
				new StorageView(p, min, max);
			}
			else if (this.returnTo.equals(RecipeSelectInventory.class.getName())) {
				new RecipeSelectInventory(p);
			}
			return;
		}
		
		if (e.getClick().equals(ClickType.LEFT)) {
			if (slot < 45) {
				craftItem(slot, 1);
			}
		}
		else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
			if (slot < 45) {
				craftItem(slot, 10);
			}
		}
		else if (e.getClick().equals(ClickType.RIGHT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) {
			if (slot < 45) {
				viewComponents(p, slot);
			}
		}
		else if (e.getClick().equals(ClickType.NUMBER_KEY)) {
			int hotbar = e.getHotbarButton() + 1;
			if (hotbar >= 1 && hotbar <= 3) {
				changeMode(hotbar);
			}
		}
	}
	
	private void viewComponents(Player p, int slot) {
		Recipe recipe = this.recipes.get(((page - 1) * 45) + slot);
		if (base != null) {
			new ComponentView(p, recipe, base, min, max);
		}
		else {
			new ComponentView(p, recipe, name, recipeList, returnTo);
		}
	}
	
	private void craftItem(int slot, int amount) {
		Recipe recipe = this.recipes.get(((page - 1) * 45) + slot);
		if (recipe.canMulticraft() || amount == 1) {
			recipe.craftRecipe(p, amount);
		}
	}
	
	private void changeMode(int hotbar) {
		mode = hotbar - 1;
		inv.setContents(setupInventory(inv.getContents()));
	}

	@Override
	public void handleInventoryDrag(InventoryDragEvent e) {
		e.setCancelled(true);
	}


	@Override
	public void handleInventoryClose(InventoryCloseEvent e) {
		
	}
	 
	private class RecipeSorter implements Comparator<Recipe> {
	    public int compare(Recipe a, Recipe b)
	    {
	    	int comp = 0;
	    	int canA = a.canCraft(p) ? 1 : 0;
	    	int canB = b.canCraft(p) ? 1 : 0;
	    	comp = canB - canA;
	    	if (comp == 0) {
	    		comp = a.getLevel() - b.getLevel(); // Ascending level
	    		if (comp == 0) {
		    		comp = b.getKey().compareTo(a.getKey());
	    		}
	    	}
	    	return comp;
	    }
	}
}
