package me.neoblade298.neobossrelics;

import org.bukkit.entity.Player;

public class PlayerSet {
	private Set set;
	private int numRelics;
	private Player p;
	private NeoBossRelics main;
	
	public PlayerSet(NeoBossRelics main, Set set, int numRelics, Player p) {
		this.main = main;
		this.set = set;
		this.numRelics = numRelics;
		this.p = p;
	}
	
	public Set getSet() {
		return this.set;
	}
	
	public int getNumRelics() {
		return this.numRelics;
	}
	
	public void incrementNum() {
		int before = this.numRelics;
		this.set.applyEffects(p, before, ++this.numRelics);
	}
	
	public void decrementNum() {
		int before = this.numRelics;
		this.set.applyEffects(p, before, --this.numRelics);
		if (this.numRelics <= 0) main.playersets.remove(this.p.getUniqueId());
		
	}
	
	public void setNumRelics(int num) {
		int before = this.numRelics;
		this.numRelics = num;
		this.set.applyEffects(p, before, this.numRelics);
		if (this.numRelics <= 0) main.playersets.remove(this.p.getUniqueId());
	}
	
	public void remove() {
		this.set.applyEffects(p, this.numRelics, 0);
	}
}
