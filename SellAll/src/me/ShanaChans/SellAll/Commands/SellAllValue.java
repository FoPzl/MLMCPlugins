package me.ShanaChans.SellAll.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class SellAllValue implements Subcommand
{

	@Override
	public String getDescription() {
		return "Get value of item in your hand!";
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getPermission() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		// TODO Auto-generated method stub
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public void run(CommandSender sender, String[] args) 
	{
		Player p = (Player) sender;
		SellAllManager.getPlayers().get(p.getUniqueId()).getValue(p);
	}

}
