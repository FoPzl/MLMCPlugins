package me.Neoblade298.NeoProfessions.Gardens;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.Neoblade298.NeoProfessions.Managers.GardenManager;
import me.Neoblade298.NeoProfessions.Managers.MinigameManager;
import me.Neoblade298.NeoProfessions.Minigames.MinigameParameters;

public class Garden {
	int size;
	HashMap<Integer, GardenSlot> slots;
	private static final long DEFAULT_GROW_TIME = 1800000; // 30 minutes
	private static final int BASE_SIZE = 1;
	
	public Garden() {
		this.size = BASE_SIZE;
		this.slots = new HashMap<Integer, GardenSlot>();
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public HashMap<Integer, GardenSlot> getSlots() {
		return slots;
	}
	
	public void plantSeed(UUID uuid, int slot, int plantId, Fertilizer fert) {
		long growTime = DEFAULT_GROW_TIME;
		if (fert != null) {
			growTime *= fert.getTimeMultiplier();
		}
		GardenSlot gs = new GardenSlot(plantId, fert, System.currentTimeMillis() + growTime);
		this.slots.put(slot, gs);
		GardenManager.addGardenMessage(uuid, gs);
	}
	
	public void harvestSeed(Player p, int slot) {
		GardenSlot gs = slots.get(slot);
		if (gs != null && gs.canHarvest()) {
			slots.remove(slot);
			MinigameManager.startMinigame(p, gs.getId(), gs.getFertilizer() != null ? gs.getFertilizer().getParams() : new MinigameParameters());
		}
	}
	
	public void uprootSeed(Player p, int slot) {
		GardenSlot gs = slots.get(slot);
		if (gs != null && !gs.canHarvest()) {
			slots.remove(slot);
		}
	}
}
