package me.Neoblade298.NeoConsumables.objects;

import org.bukkit.Material;

public enum Rarity {
	LEGENDARY("§4", Material.RED_DYE, "§4Legendary", 4, 15),
	EPIC("§6", Material.ORANGE_DYE, "§6Epic", 3, 10),
	RARE("§9", Material.LIGHT_BLUE_DYE, "§9Rare", 2, 4),
	UNCOMMON("§a", Material.LIME_DYE, "§aUncommon", 1, 1),
	COMMON("§7", Material.LIGHT_GRAY_DYE, "§7Common", 0, 0.25);
	
	private final String colorcode;
	private final Material mat;
	private final String display;
	private final int priority;
	private final double priceMod;
	private Rarity(final String colorcode, final Material mat, final String display, final int priority, final double priceMod) {
		this.colorcode = colorcode;
		this.mat = mat;
		this.display = display;
		this.priority = priority;
		this.priceMod = priceMod;
	}
	
	public String getCode() {
		return colorcode;
	}
	
	public Material getMaterial() {
		return mat;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public double getPriceModifier() {
		return priceMod;
	}
}
