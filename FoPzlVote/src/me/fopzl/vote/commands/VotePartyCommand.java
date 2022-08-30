package me.fopzl.vote.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.fopzl.vote.VoteParty;

public class VotePartyCommand implements CommandExecutor {
	private VoteParty voteParty;

	public VotePartyCommand(VoteParty vp) {
		voteParty = vp;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(args.length < 1) return false;
		
		if(sender.hasPermission("mlvote.admin")) {
			switch(args[0]) {
				case "add":
					if(args.length < 2) return false;
					voteParty.addPoints(Integer.parseInt(args[1]));
					return true;
				case "set":
					if(args.length < 2) return false;
					voteParty.setPoints(Integer.parseInt(args[1]));
					return true;
			}
		}
		
		if (args[0].equalsIgnoreCase("status")) {
			voteParty.showStatus((Player)sender);
			return true;
		}
		
		return false;
	}

}
