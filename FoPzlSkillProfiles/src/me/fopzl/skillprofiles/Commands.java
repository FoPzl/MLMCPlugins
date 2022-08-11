package me.fopzl.skillprofiles;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commands implements CommandExecutor {
	SkillProfiles main;
	
	public Commands(SkillProfiles main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(args.length < 2) return false;
		
		switch(args[0]) {
			case "save":
				main.save((Player)sender, args[1]);
				return true;
			case "load":
				main.load((Player)sender, args[1]);
				return true;
			default:
				return false;
		}
	}
}
