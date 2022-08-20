package me.fopzl.vote;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.fopzl.vote.commands.MLVoteCommand;
import me.fopzl.vote.commands.VotePartyCommand;

public class Vote extends JavaPlugin {
	public void onEnable() {
		Bukkit.getServer().getLogger().info("FoPzlVote Enabled");
		
		this.getCommand("mlvote").setExecutor(new MLVoteCommand(this));
		this.getCommand("vp").setExecutor(new VotePartyCommand(this));
	}
	
	public void onDisable() {
		Bukkit.getServer().getLogger().info("FoPzlVote Disabled");
		super.onDisable();
	}
}
