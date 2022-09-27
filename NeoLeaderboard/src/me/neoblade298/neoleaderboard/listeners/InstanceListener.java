package me.neoblade298.neoleaderboard.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.neoblade298.neocore.bungee.BungeeAPI;

public class InstanceListener implements Listener {
	private HashMap<Player, Long> playtime = new HashMap<Player, Long>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		playtime.put(e.getPlayer(), System.currentTimeMillis());
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		handleLeave(e.getPlayer());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e) {
		handleLeave(e.getPlayer());
	}
	
	private void handleLeave(Player p) {
		if (playtime.containsKey(p)) {
			long millisPlayed = System.currentTimeMillis() - playtime.get(p);
			long minutes = millisPlayed / (1000 * 60);
			BungeeAPI.sendPluginMessage("leaderboard_playtime", new String[] {p.getUniqueId().toString(), "" + minutes});
		}
	}
}
