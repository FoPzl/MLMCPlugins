package me.Neoblade298.NeoProfessions.Augments;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoProfessions.Events.AugmentInitCleanupEvent;
import me.Neoblade298.NeoProfessions.Managers.AugmentManager;
import me.Neoblade298.NeoProfessions.Objects.StoredAttributes;

public class PlayerAugments {
	private HashMap<EventType, ArrayList<Augment>> augments;
	private HashMap<String, Integer> hitCount;
	private HashMap<String, Integer> counts;
	private HashMap<Augment, StoredAttributes> attrs;
	private HashSet<String> actives;
	private Player p;
	private boolean invChanged;
	private int prevSlot;
	
	public PlayerAugments(Player p) {
		this.p = p;
		this.augments = new HashMap<EventType, ArrayList<Augment>>();
		this.counts = new HashMap<String, Integer>();
		this.hitCount = new HashMap<String, Integer>();
		this.actives = new HashSet<String>();
		this.attrs = new HashMap<Augment, StoredAttributes>();
		this.invChanged = true;
		this.prevSlot = -1;

		checkAllAugments(p.getInventory());
	}
	
	public void inventoryChanged() {
		invChanged = true;
	}
	
	public void checkAugments(ItemStack item) {
		if (item != null && !item.getType().isAir()) {
			NBTItem nbti = new NBTItem(item);
			for (int i = 1; i <= nbti.getInteger("slotsCreated"); i++) {
				String augmentName = nbti.getString("slot" + i + "Augment");
				if (AugmentManager.hasAugment(augmentName)) {
					Augment aug = AugmentManager.getFromCache(augmentName, nbti.getInteger("slot" + i + "Level"));
					
					// Add augment to hashmap
					if (aug.getEventTypes() != null) {
						for (EventType etype : aug.getEventTypes()) {
							if (augments.containsKey(etype)) {
								augments.get(etype).add(aug);
							}
							else {
								ArrayList<Augment> list = new ArrayList<Augment>();
								list.add(aug);
								augments.put(etype, list);
							}
						}
					}
					
					// Add augment to counts
					counts.put(aug.getName(), counts.getOrDefault(aug.getName(), 0) + 1);
				}
			}
		}
	}
	
	public List<Augment> getAugments(EventType etype) {
		return augments.get(etype);
	}
	
	public int getCount(Augment aug) {
		return counts.getOrDefault(aug.getName(), 0);
	}
	
	public boolean containsAugments(EventType etype) {
		PlayerInventory inv = p.getInventory();
		
		// 2 cases for recalculation:
		// 1: Mainhand has changed slots
		// 2: Mainhand is same slot and inv has changed
		if (inv.getHeldItemSlot() != prevSlot || invChanged) {
			checkAllAugments(inv);
			prevSlot = inv.getHeldItemSlot();
			invChanged = false;
		}
		return augments.containsKey(etype);
	}
	
	public String toString() {
		String toReturn = "";
		for (EventType key : augments.keySet()) {
			toReturn += key + ": ";
			for (Augment aug : augments.get(key)) {
				toReturn += aug.getLine() + ",";
			}
			toReturn.substring(0, toReturn.length() - 1);
		}
		return toReturn;
	}
	
	public void incrementHitCount(Augment aug) {
		hitCount.put(aug.getName(), hitCount.getOrDefault(aug, 0) + 1);
	}
	
	public void resetHitCount(Augment aug) {
		hitCount.put(aug.getName(), 0);
	}
	
	public int getHitCount(Augment aug) {
		return hitCount.getOrDefault(aug.getName(), 0);
	}
	
	public void applyAttributes(Augment aug, StoredAttributes sattr) {
		sattr.applyAttributes(p);
		attrs.put(aug, sattr);
	}
	
	public void removeAttributes(Augment aug) {
		if (attrs.containsKey(aug)) {
			attrs.remove(aug).removeAttributes(p);
		}
	}
	
	// Only used for init and cleanup
	public HashSet<String> getActive() {
		return actives;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public void checkAllAugments(PlayerInventory inv) {
		augments.clear();
		counts.clear();
		checkAugments(inv.getHelmet());
		checkAugments(inv.getChestplate());
		checkAugments(inv.getLeggings());
		checkAugments(inv.getBoots());
		checkAugments(inv.getItemInMainHand());
		checkAugments(inv.getItemInOffHand());
		
		// Check for new inits or cleanups
		ArrayList<Augment> newInits = new ArrayList<Augment>();
		ArrayList<Augment> newCleanups = new ArrayList<Augment>();
		Iterator<String> iter = actives.iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			boolean exists = false;
			if (augments.containsKey(EventType.CLEANUP)) {
				for (Augment aug : augments.get(EventType.CLEANUP)) {
					if (aug.getName().equals(name)) {
						exists = true;
						break;
					}
				}
			}
			if (!exists) {
				newCleanups.add(AugmentManager.getFromCache(name, 5));
				iter.remove();
			}
		}
		if (augments.containsKey(EventType.INIT)) {
			for (Augment aug : augments.get(EventType.INIT)) {
				if (actives.add(aug.getName())) {
					newInits.add(aug);
				}
			}
		}
		Bukkit.getPluginManager().callEvent(new AugmentInitCleanupEvent(this, newInits, newCleanups));
	}
}
