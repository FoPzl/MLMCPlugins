package me.ShanaChans.SellAll.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class SellAllReset implements Subcommand
{
	public String getDescription() 
	{
		return "Resets the sell caps";
	}

	@Override
	public String getKey() 
	{
		return "reset";
	}

	@Override
	public String getPermission() 
	{
		return "sellall.reset";
	}
	
	@Override
	public boolean isHidden()
	{
		return true;
	}

	@Override
	public SubcommandRunner getRunner() 
	{
		return SubcommandRunner.BOTH;
	}

	@Override
	public void run(CommandSender sender, String[] args) 
	{
		if(args.length > 0)
		{
			if(args[0].toLowerCase().equals("all"))
			{
				SellAllManager.resetPlayers();
				sender.sendMessage("§6All player limits reset!");
			}
			else if(Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline())
			{
				SellAllManager.resetPlayer(Bukkit.getPlayer(args[0]));
				sender.sendMessage("§6Player's limits reset!");
			}
		}
	}

}