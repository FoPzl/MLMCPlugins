package me.neoblade298.mlmcgift;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiftRequestSet {
	private Set<GiftRequest> theSet;
	
	public GiftRequestSet() {
		theSet = new HashSet<GiftRequest>();
	}
	
	public GiftRequestSet(Set<GiftRequest> oldSet) {
		theSet = new HashSet<GiftRequest>();
		theSet.addAll(oldSet);
	}
	
	public GiftRequestSet(GiftRequestSet oldSet) {
		theSet = new HashSet<GiftRequest>();
		for(GiftRequest req : oldSet.getSet()) {
			theSet.add(req);
		}
	}
	
	public Set<GiftRequest> getSet(){
		return theSet;
	}
	
	public GiftRequest getRequestBySender(Player sender) {
		for(GiftRequest req : theSet) {
			if(req.getSender().equals(sender)) {
				return req;
			}
		}
		
		return null;
	}
	
	public GiftRequest getRequestByReceiver(Player receiver) {
		for(GiftRequest req : theSet) {
			if(req.getReceiver().equals(receiver)) {
				return req;
			}
		}
		
		return null;
	}
	
	public boolean add(GiftRequest request) {
		return theSet.add(request);
	}
	
	public boolean remove(GiftRequest request) {
		return theSet.remove(request);
	}
	
	public boolean containsSender(Player player) {
		for(GiftRequest req : theSet) {
			if(req.getSender().equals(player)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean containsReceiver(Player player) {
		for(GiftRequest req : theSet) {
			if(req.getReceiver().equals(player)) {
				return true;
			}
		}
		
		return false;
	}
	
	// TODO: implement generic set methods (extends HashSet)
}

class GiftRequest {
	private Player sender;
	private Player receiver;
	private ItemStack item;
	
	public GiftRequest(Player sender, Player receiver, ItemStack item) {
		this.sender = sender;
		this.receiver = receiver;
		this.item = item;
	}

	public Player getSender() {
		return sender;
	}

	public void setSender(Player sender) {
		this.sender = sender;
	}

	public Player getReceiver() {
		return receiver;
	}

	public void setReceiver(Player receiver) {
		this.receiver = receiver;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}
}
