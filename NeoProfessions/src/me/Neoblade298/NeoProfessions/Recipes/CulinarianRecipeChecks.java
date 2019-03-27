package me.Neoblade298.NeoProfessions.Recipes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import me.Neoblade298.NeoProfessions.Items.DrinksRecipeItems;
import me.Neoblade298.NeoProfessions.Items.IngredientRecipeItems;

public class CulinarianRecipeChecks {
	
	public static void checkVodka(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.Ingr22") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : IngredientRecipeItems.getVodkaRecipe()) {
				if(!inv.containsAtLeast(i, 1)) {
					inv.setResult(null);
					break;
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkRum(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.Ingr23") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : IngredientRecipeItems.getRumRecipe()) {
				if(!inv.containsAtLeast(i, 1)) {
					inv.setResult(null);
					break;
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkTequila(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.Ingr24") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : IngredientRecipeItems.getTequilaRecipe()) {
				if(!inv.containsAtLeast(i, 1)) {
					inv.setResult(null);
					break;
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkBlackWidow(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.1") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getBlackWidowRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkPinkPanther(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.2") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getPinkPantherRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkMidnightKiss(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.3") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getMidnightKissRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkMidnightBlue(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.4") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getMidnightBlueRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 24);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkGoodAndEvil(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.5") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getGoodAndEvilRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkThorHammer(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.6") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getThorHammerRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkJackFrost(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.7") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getJackFrostRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkWhiteRussian(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.8") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getWhiteRussianRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkSwampWater(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.9") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getSwampWaterRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkBlueMotorcycle(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.10") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getBlueMotorcycleRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkRedDeath(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.11") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getRedDeathRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 23);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkBombsicle(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.12") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getBombsicleRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkSweetTart(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.13") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getSweetTartRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkPinaColada(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.14") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getPinaColadaRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 23);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkMargaritaOnTheRocks(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.15") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getMargaritaOnTheRocksRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 24);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	public static void checkBloodyMary(Player p, CraftingInventory inv) {
		if(p.hasPermission("recipes.drinks.16") && p.hasPermission("recipes.drinks.unlock")) {
			for(ItemStack i : DrinksRecipeItems.getBloodyMaryRecipe()) {
				if(i.getType().equals(Material.POTION)) {
					checkAlcoholBase(inv, 22);
				}
				else {
					if(!inv.containsAtLeast(i, 1)) {
						inv.setResult(null);
						break;
					}
				}
			}
		}
		else { 
			inv.setResult(null);
		}
	}
	
	private static void checkAlcoholBase(CraftingInventory inv, int ingredient) {
		if(!inv.contains(Material.POTION, 1)) {
			inv.setResult(null);
		}
		else {
			for(int j = 1; j < inv.getContents().length; j++) {
				if(inv.getContents()[j] != null && inv.getContents()[j].getType().equals(Material.POTION)) {
					ItemStack pot = inv.getContents()[j];
					if(!pot.hasItemMeta() || !pot.getItemMeta().hasLore() || !pot.getItemMeta().getLore().get(0).contains("Ingredient " + ingredient)) {
						inv.setResult(null);
					}
				}
			}
		}
	}
}
