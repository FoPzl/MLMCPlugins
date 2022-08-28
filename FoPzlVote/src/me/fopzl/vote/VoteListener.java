package me.fopzl.vote;

import com.vexsoftware.votifier.model.VotifierEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteListener implements Listener {
	private Vote main;
	private HashMap<UUID, List<String>> queuedRewards = new HashMap<UUID, List<String>>();
	
	public VoteListener(Vote main) {
		this.main = main;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onVote(final VotifierEvent e) {
		com.vexsoftware.votifier.model.Vote vote = e.getVote();
		if(main.isValidSite(vote.getServiceName())) {
			OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(vote.getUsername()); // TODO: verify case sensitivity
			Util.broadcastFormatted("&4[&c&lMLMC&4] &e" + p.getName() + " &7just voted on &c" + vote.getServiceName() + "&7!");
			
			if(p.isOnline()) {
				main.rewardVote((Player)p, vote.getServiceName());
			} else {
				UUID uuid = p.getUniqueId();
				if(!queuedRewards.containsKey(uuid)) {
					queuedRewards.put(uuid, new ArrayList<String>());
				}
				queuedRewards.get(uuid).add(vote.getServiceName());
			}
			
			main.incVoteParty();
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				UUID uuid = e.getPlayer().getUniqueId();
				if(queuedRewards.containsKey(uuid)) {
					List<String> sites = queuedRewards.remove(uuid);
					for(String s : sites) {
						main.rewardVote(e.getPlayer(), s);
					}
				}
			}
		}.runTaskLater(main, 100); // delay to let vote stats load in first
	}
}
