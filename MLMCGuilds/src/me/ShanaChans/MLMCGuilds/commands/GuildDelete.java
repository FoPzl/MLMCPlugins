package me.ShanaChans.MLMCGuilds.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.MLMCGuilds.GuildManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class GuildDelete implements Subcommand
{

	@Override
	public String getDescription() 
	{
		return "Deletes the guild.";
	}

	@Override
	public String getKey() 
	{
		return "delete";
	}

	@Override
	public String getPermission() 
	{
		return "guilds.rank.owner";
	}

	@Override
	public SubcommandRunner getRunner() 
	{
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public void run(CommandSender sender, String[] args) 
	{
		Player player = (Player) sender;
		GuildManager.deleteGuild(player.getPlayer());
		
	}
	
	@Override
	public String getArgs() {
		// TODO Auto-generated method stub
		return null;
	}

}
