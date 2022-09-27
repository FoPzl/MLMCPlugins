package me.ShanaChans.MLMCGuilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.MLMCGuilds.GuildManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class GuildInvite implements Subcommand
{

	@Override
	public String getDescription() 
	{
		// TODO Auto-generated method stub
		return "Invites a player to your guild.";
	}

	@Override
	public String getKey() 
	{
		// TODO Auto-generated method stub
		return "invite";
	}

	@Override
	public String getPermission() 
	{
		// TODO Auto-generated method stub
		return "guilds.rank.invite";
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
		if(args.length <= 1 || Bukkit.getPlayer(args[1]) == null)
		{
			player.getPlayer().sendMessage("Invalid Player.");
			return;
		}
		GuildManager.inviteGuild(player, args[1]);
	}

	@Override
	public String getArgs() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
