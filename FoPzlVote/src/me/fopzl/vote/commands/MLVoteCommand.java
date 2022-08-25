package me.fopzl.vote.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.fopzl.vote.Util;
import me.fopzl.vote.Vote;

public class MLVoteCommand implements CommandExecutor {
	private Vote main;
	
	public MLVoteCommand(Vote main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(args.length < 1) return false;
		
		if(sender.hasPermission("mlvote.admin")) {
			switch(args[0]) {
				case "vote":
					if(args.length < 3) return false;
					
					main.rewardVote(Bukkit.getServer().getPlayer(args[1]));
					// TODO: reqs specify this includes vote site, but how?
					return true;
				case "reload":
					main.loadConfig();
					Util.sendMessageFormatted(sender, "&4[&c&lMLMC&4] &7Reloaded config");
					return true;
			}
		}
		
		if (args[0].equalsIgnoreCase("leaderboard")) {
			main.showLeaderboard((Player)sender);
			return true;
		}
		
		return false;
	}

}
