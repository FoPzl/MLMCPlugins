package me.fopzl.vote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.fopzl.vote.commands.MLVoteCommand;
import me.fopzl.vote.commands.VotePartyCommand;
import me.neoblade298.neocore.bungee.BungeeAPI;

public class Vote extends JavaPlugin {
	private VoteParty voteParty;
	private HashSet<String> validVoteSites = new HashSet<String>();
	
	public void onEnable() {
		super.onEnable();
		
		voteParty = new VoteParty(this);
		
		this.getCommand("mlvote").setExecutor(new MLVoteCommand(this));
		this.getCommand("vp").setExecutor(new VotePartyCommand(voteParty));
		
		getServer().getPluginManager().registerEvents(new VoteListener(this), this);
		
		loadConfig();
		
		Bukkit.getServer().getLogger().info("FoPzlVote Enabled");
	}
	
	public void onDisable() {
		Bukkit.getServer().getLogger().info("FoPzlVote Disabled");
		super.onDisable();
	}
	
	public void loadConfig() {
		// TODO
		
		voteParty.loadConfig();
	}
	
	public void incVoteParty() {
		voteParty.addPoints(1);
	}
	
	public void showLeaderboard(CommandSender sender) {
		// TODO: sqlize this, it will change (no hashmap)
		
		HashMap<UUID, Integer> topVoters = new HashMap<UUID, Integer>();
		int num = 1;
		String msg = "&4[&c&lMLMC&4] &eTop Monthly Voters:\n"; // TODO: better to send as one msg with newlines vs send multiple?
		for(Entry<UUID, Integer> entry : topVoters.entrySet()) {
			String username = Bukkit.getServer().getOfflinePlayer(entry.getKey()).getName();
			msg += "&6&l" + num++ + ". &e" + username + " &7 - &f" + entry.getValue() + "\n";
		}
		
		Util.sendMessageFormatted(sender, msg);
	}
	
	public boolean isValidSite(String site) {
		return validVoteSites.contains(site);
	}
	
	public void rewardVote(Player p) {
		
	}
}

class VoteConfig {
	
}