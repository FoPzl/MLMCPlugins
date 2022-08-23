package me.fopzl.vote.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.fopzl.vote.Vote;

public class VotePartyCommand implements CommandExecutor {
	private Vote main;

	public VotePartyCommand(Vote main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(args.length < 1) return false;
		
		if(sender.hasPermission("mlvote.admin")) {
			switch(args[0]) {
				case "add":
					if(args.length < 2) return false;
					// TODO: add to vp #
					return true;
				case "set":
					if(args.length < 2) return false;
					// TODO: set vp #
					return true;
			}
		}
		
		if (args[0].equalsIgnoreCase("status")) {
			// TODO
		}
		
		return false;
	}

}
