package me.neoblade298.neoleaderboard.commands;

import java.util.Arrays;
import org.bukkit.command.CommandSender;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;

import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neoleaderboard.points.NationPointType;
import me.neoblade298.neoleaderboard.points.PointsManager;

public class CmdNLAAddNation implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("nation"),
			new CommandArgument("point type"), new CommandArgument("amount")));

	@Override
	public String getDescription() {
		return "Add nation points to a nation";
	}

	@Override
	public String getKey() {
		return "addnation";
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
	public CommandArguments getArgs() {
		return args;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		NationPointType type = null;
		try {
			type = NationPointType.valueOf(args[1].toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			Util.msg(s, "&cInvalid type! Valid types are:");
			for (NationPointType t : NationPointType.values()) {
				Util.msg(s, "&7- &c" + t);
			}
			return;
		}
		Nation n = TownyAPI.getInstance().getNation(args[0]);
		if (n == null) {
			Util.msg(s, "&cNation does not exist!");
			return;
		}
		
		int amount = Integer.parseInt(args[2]);
		PointsManager.addNationPoints(n.getUUID(), amount, type);
		Util.msg(s, "&7Successfully added &e" + amount + " &6" + type + " &7points to nation &c" + n.getName());
	}
}
