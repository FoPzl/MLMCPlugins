package me.neoblade298.neopvp.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neopvp.PvpAccount;
import me.neoblade298.neopvp.PvpManager;

public class CmdAPvpSet implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player"), new CommandArgument("wins/losses/bounty/elo"),
			new CommandArgument("amount")));

	@Override
	public String getDescription() {
		return "Sets a field in the player's pvp account";
	}

	@Override
	public String getKey() {
		return "set";
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		PvpAccount acct = PvpManager.getAccount(Bukkit.getPlayer(args[0]));
		int amt = Integer.parseInt(args[2]);
		switch (args[1]) {
		case "wins":
			acct.setWins(amt);
			Util.msg(s, "&7Successfully changed player's wins!");
			break;
		case "losses":
			acct.setLosses(amt);
			Util.msg(s, "&7Successfully changed player's losses!");
			break;
		case "bounty":
			acct.setPvpBalance(amt);
			Util.msg(s, "&7Successfully changed player's bounty!");
			break;
		case "elo":
			acct.setElo(amt);
			Util.msg(s, "&7Successfully changed player's elo!");
			break;
		default:
			Util.msg(s, "&cInvalid field to change!");
			break;
		}
	}
	
	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
