package me.fopzl.vote;

import java.io.File;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.fopzl.vote.commands.MLVoteCommand;
import me.fopzl.vote.commands.VotePartyCommand;

public class Vote extends JavaPlugin {
	private VoteRewards rewards;
	private VoteParty voteParty;
	private HashSet<String> validVoteSites;
	
	private VoteIO io;
	
	public void onEnable() {
		super.onEnable();
		
		rewards = new VoteRewards();
		voteParty = new VoteParty(this);
		io = new VoteIO();
		
		this.getCommand("mlvote").setExecutor(new MLVoteCommand(this));
		this.getCommand("vp").setExecutor(new VotePartyCommand(voteParty));
		
		getServer().getPluginManager().registerEvents(new VoteListener(this), this);
		
		loadAllConfigs();
		
		Bukkit.getServer().getLogger().info("FoPzlVote Enabled");
	}
	
	public void onDisable() {
		Bukkit.getServer().getLogger().info("FoPzlVote Disabled");
		super.onDisable();
	}
	
	public void loadAllConfigs() {
		File mainCfg = new File(getDataFolder(), "config.yml");
		if(!mainCfg.exists()) {
			saveResource("config.yml", false);
		}
		this.loadConfig(YamlConfiguration.loadConfiguration(mainCfg));

		File rewardsCfg = new File(getDataFolder(), "rewards.yml");
		if(!rewardsCfg.exists()) {
			saveResource("rewards.yml", false);
		}
		rewards.loadConfig(YamlConfiguration.loadConfiguration(rewardsCfg));	
		
		File votepartyCfg = new File(getDataFolder(), "voteparty.yml");
		if(!votepartyCfg.exists()) {
			saveResource("voteparty.yml", false);
		}
		voteParty.loadConfig(YamlConfiguration.loadConfiguration(votepartyCfg));
	}
	
	public void loadConfig(YamlConfiguration cfg) {
		validVoteSites = new HashSet<String>();
		for(String site : cfg.getStringList("websites")) {
			validVoteSites.add(site);
		}
		
		VoteStats.setStreakLimit(cfg.getInt("streak-vote-limit"));
		VoteStats.setStreakResetTime(cfg.getInt("streak-reset-leniency"));
	}
	
	public void incVoteParty() {
		voteParty.addPoints(1);
	}
	
	public void showLeaderboard(CommandSender sender) {
		List<Object[]> topVoters = io.getTopVoters(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 10);
		int num = 1;
		String msg = "&4[&c&lMLMC&4] &eTop Monthly Voters:";
		for(Object[] entry : topVoters) {
			String username = Bukkit.getServer().getOfflinePlayer((UUID)entry[0]).getName();
			msg += "\n&6&l" + num++ + ". &e" + username + " &7 - &f" + (String)entry[1];
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