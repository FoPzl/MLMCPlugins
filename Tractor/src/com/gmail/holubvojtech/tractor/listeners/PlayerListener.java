package com.gmail.holubvojtech.tractor.listeners;

import com.gmail.holubvojtech.tractor.Tractor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Tractor.plugin.getPlayerData().remove(player.getUniqueId());
	}
}
