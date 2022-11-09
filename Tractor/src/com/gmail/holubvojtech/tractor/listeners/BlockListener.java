package com.gmail.holubvojtech.tractor.listeners;

import com.gmail.holubvojtech.tractor.BlockBox;
import com.gmail.holubvojtech.tractor.BlockIterator;
import com.gmail.holubvojtech.tractor.InventoryUtils;
import com.gmail.holubvojtech.tractor.PlayerData;
import com.gmail.holubvojtech.tractor.Tractor;
import com.gmail.holubvojtech.tractor.Utils;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

public class BlockListener implements Listener {
	private Tractor main;
	
	public BlockListener(Tractor main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		final Material seedBlockMat = block.getType();
		Material seedItemMat = (Material) Tractor.getCfg().getSeedTypes().get(seedBlockMat);
		if (seedItemMat == null) {
			return;
		}
		final Material soilType = (Material) Utils.getOrDefault(main.SOIL_OVERRIDE, seedBlockMat, Material.FARMLAND);

		Player player = event.getPlayer();
		if (!Tractor.hasPlayerData(player)) {
			return;
		}
		PlayerData data = Tractor.getPlayerData(player);
		if (!data.tractorEnabled()) {
			return;
		}
		if (event.getHand() == EquipmentSlot.OFF_HAND) {
			player.sendMessage("§cTractors do not work with offhand!");
			return;
		}
		GameMode gameMode = player.getGameMode();
		PlayerInventory inventory = player.getInventory();

		int xSize = data.getXSize();
		int zSize = data.getZSize();
		final int available = gameMode == GameMode.CREATIVE ? Integer.MAX_VALUE
				: InventoryUtils.countItem(inventory, seedItemMat) - 1;
		if (available == 0) {
			return;
		}
		final int[] count = new int[1];

		final Location loc = block.getLocation();
		loc.setY(loc.getY() + 1.0D);
		BlockBox box = new BlockBox(loc, xSize, zSize, Tractor.getCfg().getHeight());
		box.iterate(loc.getWorld(), new BlockIterator() {
			public boolean accept(Block block) {
				boolean checked = false;
				if (count[0] >= available) {
					if (!checked) {
						count[0] += 1;
						checked = true;
					}
					return false;
				}
				if (block.getLocation().equals(loc)) {
					checked = true;
					count[0] += 1;
					return true;
				}
				if (block.getType() != Material.AIR) {
					return true;
				}
				if (block.getRelative(BlockFace.DOWN).getType() != soilType) {
					return true;
				}
				block.setType(seedBlockMat);
				count[0] += 1;
				return true;
			}
		});
		if (gameMode != GameMode.CREATIVE) {
			InventoryUtils.subtract(inventory, seedItemMat, count[0], player);
			player.updateInventory();
		}
	}
}
