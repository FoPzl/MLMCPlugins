package me.fopzl.vote;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.fopzl.vote.commands.MLVoteCommand;
import me.fopzl.vote.commands.VotePartyCommand;

public class Vote extends JavaPlugin {
	private HashSet<String> validVoteSites = new HashSet<String>();
	
	public void onEnable() {
		super.onEnable();
		Bukkit.getServer().getLogger().info("FoPzlVote Enabled");
		
		this.getCommand("mlvote").setExecutor(new MLVoteCommand(this));
		this.getCommand("vp").setExecutor(new VotePartyCommand(this));
		
		getServer().getPluginManager().registerEvents(new VoteListener(this), this);
	}
	
	public void onDisable() {
		Bukkit.getServer().getLogger().info("FoPzlVote Disabled");
		super.onDisable();
	}
	
	public boolean isValidSite(String site) {
		return validVoteSites.contains(site);
	}
	
	public void rewardVote(Player p) {
		
	}
}
