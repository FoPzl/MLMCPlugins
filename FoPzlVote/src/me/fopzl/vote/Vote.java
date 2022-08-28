package me.fopzl.vote;

import java.time.LocalDate;
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

public class Vote extends JavaPlugin {
	private VoteRewards rewards;
	private VoteParty voteParty;
	private HashSet<String> validVoteSites = new HashSet<String>();
	
	private VoteIO io;
	
	public void onEnable() {
		super.onEnable();
		
		rewards = new VoteRewards();
		voteParty = new VoteParty(this);
		io = new VoteIO();
		
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
		rewards.loadConfig();	
		voteParty.loadConfig();
	}
	
	public void incVoteParty() {
		voteParty.addPoints(1);
	}
	
	public void showLeaderboard(CommandSender sender) {
		HashMap<UUID, Integer> topVoters = io.getTopVoters(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
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
	
	public void rewardVote(Player p, String voteSite) {
		VoteStats stats = io.getStats(p);
		stats.addVote(voteSite);
		rewards.rewardVote(p, stats.voteStreak);
	}
}