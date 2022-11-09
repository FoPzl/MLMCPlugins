package me.ShanaChans.SellAll.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class SellAllSort implements Subcommand
{
	@Override
	public String getDescription() 
	{
		return "Toggle your sort!";
	}

	@Override
	public String getKey() 
	{
		return "sort";
	}

	@Override
	public String getPermission() 
	{
		return null;
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
		
		if(!SellAllManager.settings.exists("SellAllSort", player.getUniqueId()))
		{
			SellAllManager.settings.set("SellAllSort", player.getUniqueId());
			player.sendMessage("§6Sell All Sort: Alphabetical");
		}
		else
		{
			SellAllManager.settings.reset("SellAllSort", player.getUniqueId());
			player.sendMessage("§6Sell All Sort: ID");
		}
	}

}
