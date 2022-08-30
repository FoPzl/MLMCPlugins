package me.fopzl.vote;

import com.vexsoftware.votifier.model.VotifierEvent;

import java.util.HashMap;
import java.util.Map.Entry;
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
	
	public VoteListener(Vote main) {
		this.main = main;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onVote(final VotifierEvent e) {
		com.vexsoftware.votifier.model.Vote vote = e.getVote();
		String site = vote.getServiceName();
		if(main.isValidSite(site)) {
			OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(vote.getUsername()); // TODO: verify case sensitivity
			Util.broadcastFormatted("&4[&c&lMLMC&4] &e" + p.getName() + " &7just voted on &c" + site + "&7!");
			
			if(p.isOnline()) {
				main.rewardVote((Player)p, site);
			} else {
				UUID uuid = p.getUniqueId();
				HashMap<String, Integer> pq = null;
				HashMap<UUID, HashMap<String, Integer>> qr = main.getVoteInfo().queuedRewards;
				
				if(!qr.containsKey(uuid)) {
					pq = new HashMap<String, Integer>();
					qr.put(uuid, pq);
				} else {
					pq = qr.get(uuid);
				}
				pq.put(site, pq.getOrDefault(site, 0) + 1);
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
				HashMap<UUID, HashMap<String, Integer>> qr = main.getVoteInfo().queuedRewards;
				if(qr.containsKey(uuid)) {
					HashMap<String, Integer> sites = qr.remove(uuid);
					for(Entry<String, Integer> entry : sites.entrySet()) {
						for(int i = 0; i < entry.getValue(); i++) {
							main.rewardVote(e.getPlayer(), entry.getKey());
						}
					}
				}
			}
		}.runTaskLater(main, 100); // delay to let vote stats load in first
	}
}