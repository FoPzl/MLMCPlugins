package me.fopzl.vote;

import java.util.HashSet;

import org.bukkit.Bukkit;
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
	
	public void showLeaderboard(Player p) {
		// TODO: sql
		String msg = "TODO";
		BungeeAPI.sendPluginMessage(p, "global", msg); // TODO: verify channel
	}
	
	public boolean isValidSite(String site) {
		return validVoteSites.contains(site);
	}
	
	public void rewardVote(Player p) {
		
	}
}
