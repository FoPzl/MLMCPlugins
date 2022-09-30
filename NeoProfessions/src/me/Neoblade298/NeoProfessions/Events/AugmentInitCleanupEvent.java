package me.Neoblade298.NeoProfessions.Events;

import java.util.ArrayList;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.Neoblade298.NeoProfessions.Augments.Augment;
import me.Neoblade298.NeoProfessions.Augments.PlayerAugments;

public class AugmentInitCleanupEvent extends Event {
	private PlayerAugments paugs;
	private ArrayList<Augment> newInits, newCleanups;
    private static final HandlerList handlers = new HandlerList();
	public AugmentInitCleanupEvent(PlayerAugments paugs, ArrayList<Augment> newInits, ArrayList<Augment> newCleanups) {
		this.paugs = paugs;
		this.newInits = newInits;
		this.newCleanups = newCleanups;
	}
	public PlayerAugments getPlayerAugments() {
		return paugs;
	}
	
	public ArrayList<Augment> getInit() {
		return newInits;
	}
	
	public ArrayList<Augment> getCleanup() {
		return newCleanups;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }
	
}
