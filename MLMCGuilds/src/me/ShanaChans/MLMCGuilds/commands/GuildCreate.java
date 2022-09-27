package me.ShanaChans.MLMCGuilds.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.MLMCGuilds.GuildManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class GuildCreate implements Subcommand
{

	@Override
	public String getDescription() 
	{
		return "Creates a guild.";
	}

	@Override
	public String getKey() 
	{
		
		return "create";
	}

	@Override
	public String getPermission() 
	{
		
		return "guilds.create";
	}

	@Override
	public SubcommandRunner getRunner() 
	{
		
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public void run(CommandSender s, String[] args) 
	{
		Player player = (Player) s;
		
		if(args.length > 1)
		{
			GuildManager.createGuild(args[1], player.getPlayer());
		}
		
	}
	
	@Override
	public String getArgs() {
		// TODO Auto-generated method stub
		return null;
	}

}
