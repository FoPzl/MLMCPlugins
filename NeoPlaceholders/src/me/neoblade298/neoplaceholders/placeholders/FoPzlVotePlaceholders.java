package me.neoblade298.neoplaceholders.placeholders;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.fopzl.vote.VoteInfo;

public class FoPzlVotePlaceholders extends PlaceholderExpansion {
	@Override
	public boolean canRegister() {
		return true;
	}
	
	@Override
	public boolean register() {
		return canRegister() && super.register();
	}
	
	@Override
	public String getAuthor() {
		return "FoPzl";
	}
	
	@Override
	public boolean persist() {
		return true;
	}
	
	@Override
	public String getIdentifier() {
		return "fopzlvote";
	}
	
	@Override
	public String getRequiredPlugin() {
		return "FoPzlVote";
	}
	
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if(p == null) return "Loading...";
		
		String args[] = identifier.split("_");
		// fopzlvote_all, fopzlvote_month, and fopzlvote_streak
		if(args[0].equalsIgnoreCase("fopzlvote")) {
			switch (args[1]) {
			case "all":
				return VoteInfo.getInstance().getStats(p).getTotalVotes() + "";
			case "month":
				return VoteInfo.getInstance().getStats(p).getVotesThisMonth() + "";
			case "streak":
				return VoteInfo.getInstance().getStats(p).getStreak() + "";
			}
		}
		
    	return "§cInvalid placeholder!";
	}
}
