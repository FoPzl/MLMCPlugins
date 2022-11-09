package me.ShanaChans.SellAll.Commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class SellAllList implements Subcommand
{
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("page number", false)));

	@Override
	public String getDescription() 
	{
		return "Check the prices for all materials!";
	}

	@Override
	public String getKey() 
	{
		return "list";
	}
	
	@Override
	public CommandArguments getArgs()
	{
		return args;
	}
	
	@Override
	public String getPermission() 
	{
		return null;
	}
	
	@Override
	public String[] getAliases() 
	{
		return new String[] {"price", "prices"};
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
		
		if(args.length > 0)
		{
			try {
				SellAllManager.getItemList(player, Integer.parseInt(args[0]) - 1, SellAllManager.getPlayerSort(player));
			} catch(NumberFormatException e){
				
			}
		}
		else
		{
			SellAllManager.getItemList(player, 0, SellAllManager.getPlayerSort(player));
		}
	}

}
