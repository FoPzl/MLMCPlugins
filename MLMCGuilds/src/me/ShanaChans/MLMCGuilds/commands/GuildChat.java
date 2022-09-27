package me.ShanaChans.MLMCGuilds.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.MLMCGuilds.GuildManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class GuildChat implements Subcommand{

	private String key;
	
	public GuildChat(String key)
	{
		this.key = key;
	}
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Switches to guild chat.";
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public String getPermission() {
		// TODO Auto-generated method stub
		return "guilds.chat";
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
		if(!GuildManager.getPlayers().containsKey(player.getPlayer().getUniqueId()))
		{
			player.getPlayer().sendMessage("You are not in a guild!");
			return;
		}
		if((key.equals("chat") && args.length > 1) || (key.equals("") && args.length > 0))
		{
			GuildManager.sendChat(player, args);
			return;
		}
		GuildManager.toggleChat(player.getPlayer());
	}
	
	@Override
	public String getArgs() {
		// TODO Auto-generated method stub
		return null;
	}

}
