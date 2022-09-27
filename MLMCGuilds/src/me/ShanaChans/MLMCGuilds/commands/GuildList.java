package me.ShanaChans.MLMCGuilds.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.MLMCGuilds.GuildManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class GuildList implements Subcommand{

	@Override
	public String getArgs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Displays list of guilds to player";
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return "list";
	}

	@Override
	public String getPermission() {
		// TODO Auto-generated method stub
		return "guilds.list";
	}

	@Override
	public SubcommandRunner getRunner() {
		// TODO Auto-generated method stub
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public void run(CommandSender sender, String[] args) 
	{
		Player player = (Player) sender;
		GuildManager.listGuild(player);
		
	}

}
