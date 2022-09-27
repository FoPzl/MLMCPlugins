package me.ShanaChans.MLMCGuilds.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.MLMCGuilds.GuildManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class GuildJoin implements Subcommand
{

	@Override
	public String getDescription() 
	{
		// TODO Auto-generated method stub
		return "Join an existing guild.";
	}

	@Override
	public String getKey() 
	{
		// TODO Auto-generated method stub
		return "join";
	}

	@Override
	public String getPermission() 
	{
		// TODO Auto-generated method stub
		return "guilds.join";
	}

	@Override
	public SubcommandRunner getRunner() 
	{
		// TODO Auto-generated method stub
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public void run(CommandSender sender, String[] args) 
	{
		Player player = (Player) sender;
		if(args.length > 1)
		{
			GuildManager.joinGuild(args[1], player.getPlayer());
		}
	}
	
	@Override
	public String getArgs() {
		// TODO Auto-generated method stub
		return null;
	}

}
