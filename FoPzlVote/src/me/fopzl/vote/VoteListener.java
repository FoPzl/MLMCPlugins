package me.fopzl.vote;

import com.vexsoftware.votifier.model.VotifierEvent;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VoteListener implements Listener {
	private Vote main;
	private HashMap<UUID, Integer> queuedRewards = new HashMap<UUID, Integer>();
	
	public VoteListener(Vote main) {
		this.main = main;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onVote(final VotifierEvent e) {
		if(main.isValidSite(e.getVote().getServiceName())) {
			OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(e.getVote().getUsername()); // TODO: verify case sensitivity
			if(p.isOnline()) {
				main.rewardVote((Player)p);
			} else {
				UUID uuid = p.getUniqueId();
				queuedRewards.put(uuid, queuedRewards.getOrDefault(uuid, 0) + 1);
			}
			
			Util.broadcastFormatted("&4[&c&lMLMC&4] &e" + p.getName() + " &7just voted on &c" + e.getVote().getServiceName() + "&7!");
			// TODO: voteparty stuff
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		if(queuedRewards.containsKey(uuid)) {
			int numRewards = queuedRewards.remove(uuid);
			for(int i = 0; i < numRewards; i++) {
				main.rewardVote(e.getPlayer());
			}
		}
	}
}
