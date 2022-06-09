package me.neoblade298.neoquests.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neoquests.navigation.NavigationManager;

public class CmdANavigationExit implements Subcommand {

	@Override
	public String getDescription() {
		return "Creates a new pathway editor";
	}

	@Override
	public String getKey() {
		return "exit";
	}

	@Override
	public String getPermission() {
		return "neoquests.admin";
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = (Player) s;
		NavigationManager.exitPathwayEditor(p);
	}

	@Override
	public String getArgs() {
		return null;
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_RED;
	}
}