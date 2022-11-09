package com.gmail.holubvojtech.tractor;

import org.bukkit.Location;
import org.bukkit.World;

public class BlockBox {
	private int x1;
	private int y1;
	private int z1;
	private int x2;
	private int y2;
	private int z2;

	public BlockBox(Location center, int xSize, int zSize, int height) {
		int x = center.getBlockX();
		int y = center.getBlockY();
		int z = center.getBlockZ();

		this.x1 = x;
		this.y1 = (y - height / 2);
		this.z1 = z;

		this.x2 = (this.x1 + (xSize - 1));
		this.y2 = (this.y1 + (height - 1));
		this.z2 = (this.z1 + (zSize - 1));
	}

	public void iterate(World world, BlockIterator iterator) {
		for (int y = this.y1; y <= this.y2; y++) {
			for (int z = this.z1; z <= this.z2; z++) {
				for (int x = this.x1; x <= this.x2; x++) {
					if (!iterator.accept(world.getBlockAt(x, y, z))) {
						return;
					}
				}
			}
		}
	}
}
