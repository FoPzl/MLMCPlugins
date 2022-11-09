package com.gmail.holubvojtech.tractor;

import org.bukkit.block.Block;

public abstract interface BlockIterator {
	public abstract boolean accept(Block paramBlock);
}
