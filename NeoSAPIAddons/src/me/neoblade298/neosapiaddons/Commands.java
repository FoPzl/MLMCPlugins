package me.neoblade298.neosapiaddons;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.util.FlagManager;

import me.neoblade298.neocore.util.Util;
import me.neoblade298.neosapiaddons.methods.ProfessClass;
import me.neoblade298.neosapiaddons.methods.ResetClass;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	SAPIAddons main;
	public Commands(SAPIAddons main) {
		this.main = main;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (sender.hasPermission("neosapiaddons.use")) {
			if (args.length == 0) {
				sender.sendMessage("§8neosapiaddons.use");
				sender.sendMessage("§c/nsapi debug");
				sender.sendMessage("§c/nsapi skillup [player] [skill] - increase a skill");
				sender.sendMessage("§c/nsapi refresh [player] - update equips");
				sender.sendMessage("§c/nsapi profess [player] [class] - safely profess player to next tier");
				sender.sendMessage("§c/nsapi reset [player] - reset player to previous tier");
				sender.sendMessage("§c/nsapi attr [attr] - check attributes, even hidden ones");
				sender.sendMessage("§c/nsapi flag <player> [flag] - check if player has flag");
				sender.sendMessage("§c/nsapi checkskill <player> [skill] - checks a player's skill level");
				sender.sendMessage("§c/nsapi update [player] - Fixes AP and SP");
				return true;
			}
			if (args[0].equalsIgnoreCase("debug")) {
				SAPIAddons.debug = !SAPIAddons.debug;
				if (SAPIAddons.debug) {
					sender.sendMessage("§4[§c§lMLMC§4] §7Debug mode enabled");
				}
				else {
					sender.sendMessage("§4[§c§lMLMC§4] §7Debug mode disabled");
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("reset")) {
				ResetClass.resetPlayer(Bukkit.getPlayer(args[1]), sender);
				return true;
			}
			else if (args[0].equalsIgnoreCase("profess")) {
				ProfessClass.professPlayer(Bukkit.getPlayer(args[1]), sender, args[2]);
				return true;
			}
			else if (args[0].equalsIgnoreCase("skillup")) {
				Skill skill = SkillAPI.getSkill(args[2].replaceAll("_", " "));
				SkillAPI.getPlayerData(Bukkit.getPlayer(args[1])).upgradeSkill(skill, true);
				sender.sendMessage("§4[§c§lMLMC§4] §7Skill upgraded.");
				return true;
			}
			else if (args[0].equalsIgnoreCase("refresh")) {
	            Player p = Bukkit.getPlayer(args[1]);
                SkillAPI.getPlayerData(p).getEquips().update(p);
                return true;
			}
			else if (args[0].equalsIgnoreCase("attr")) {
	            Player p = (Player) sender;
                sender.sendMessage("§4[§c§lMLMC§4] §7Attribute has: " + SkillAPI.getPlayerData(p).getAttribute(args[1]) + ".");
                return true;
			}
			else if (args[0].equalsIgnoreCase("checkskill")) {
				Player p = Bukkit.getPlayer(args[1]);
				String skill = Util.connectArgs(args, 2);
				PlayerSkill ps = SkillAPI.getPlayerData(p).getSkill(skill);
				if (ps == null) {
					Util.msg(sender, "&cThis skill doesn't exist for this player!");
				}
				Util.msg(sender, "&7Player &c" + p.getName() + " &7skill level of &6" + ps.getData().getName() + " &7is &e" + ps.getLevel());
				return true;
			}
			else if (args[0].equalsIgnoreCase("flag")) {
				if (args.length == 2) {
		            Player p = (Player) sender;
	                sender.sendMessage("§4[§c§lMLMC§4] §7Flag " + args[1] + ": " + FlagManager.hasFlag(p, args[1]));
	                return true;
				}
				else {
		            Player p = Bukkit.getPlayer(args[1]);
	                sender.sendMessage("§4[§c§lMLMC§4] §7Flag " + args[2] + ": " + FlagManager.hasFlag(p, args[2]));
	                return true;
				}
			}
			else if (args[0].equalsIgnoreCase("update")) {
				if (args.length == 0) {
		            SAPIAddons.correctStats(SkillAPI.getPlayerData((Player) sender));
				}
				else {
		            SAPIAddons.correctStats(SkillAPI.getPlayerData(Bukkit.getPlayer(args[1])));
				}
                return true;
			}

		}
		sender.sendMessage("Something went wrong.");
		return true;
	}
}