package me.ShanaChans.MLMCGuilds.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.MLMCGuilds.GuildManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class GuildLeave implements Subcommand
{

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Leave current guild.";
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return "leave";
	}

	@Override
	public String getPermission() {
		// TODO Auto-generated method stub
		return "guilds.leave";
	}

	@Override
	public SubcommandRunner getRunner() {
		// TODO Auto-generated method stub
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public void run(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		Player player = (Player) sender;
		GuildManager.leaveGuild(player.getPlayer());
	}
	
	@Override
	public String getArgs() {
		// TODO Auto-generated method stub
		return null;
	}
}
