package me.neoblade298.neopvp.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neopvp.wars.War;
import me.neoblade298.neopvp.wars.WarManager;

public class CmdAWarTeam1 implements Subcommand {

	@Override
	public String getDescription() {
		return "Modify team 1";
	}

	@Override
	public String getKey() {
		return "team1";
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.PLAYER_ONLY;
	}
	
	@Override
	public String getArgOverride() {
		return "[create [name] [display] / +-[nation/town/player] [name] / setspawn / setmascotspawn]";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		runCommand(s, args, 1);
	}
	
	public static void runCommand(CommandSender s, String args[], int team) {
		String arg1 = args[0].toLowerCase();
		War war = WarManager.getWarCreator(s);
		TownyAPI api = TownyAPI.getInstance();
		Town t = null;
		Nation n = null;
		
		switch (arg1) {
		case "create":
			String key = args[1];
			String name = Util.translateColors(Util.connectArgs(args, 2));
			war.createTeam(team, key, name);
			break;
		case "+nation":
			if (war.getTeams()[team - 1] == null) {
				Util.msg(s, "&cYou must first create the team with /awar team" + team + " create [name]");
				return;
			}
			n = TownyAPI.getInstance().getNation(args[1]);
			if (n == null) {
				Util.msg(s, "&cThat nation doesn't exist");
			}
			war.addTeamNation(team, n);
			break;
		case "-nation":
			if (war.getTeams()[team - 1] == null) {
				Util.msg(s, "&cYou must first create the team with /awar team" + team + " create [name]");
				return;
			}
			n = TownyAPI.getInstance().getNation(args[1]);
			if (n == null) {
				Util.msg(s, "&cThat nation doesn't exist");
			}
			war.removeTeamNation(team, n);
			break;
		case "+town":
			if (war.getTeams()[team - 1] == null) {
				Util.msg(s, "&cYou must first create the team with /awar team" + team + " create [name]");
				return;
			}
			t = api.getTown(args[1]);
			if (t != null) {
				war.getTeams()[team - 1].addWhitelistedTown(t);
				Util.msg(s, "Successfully added town &e" + t.getName() + " &7to whitelisted towns.");
			}
			else {
				Util.msg(s, "&cFailed to find town.");
			}
			break;
		case "-town":
			if (war.getTeams()[team - 1] == null) {
				Util.msg(s, "&cYou must first create the team with /awar team" + team + " create [name]");
				return;
			}
			t = api.getTown(args[1]);
			if (t != null) {
				war.getTeams()[team - 1].removeWhitelistedTown(t);
				Util.msg(s, "Successfully removed town &e" + t.getName() + " &7from whitelisted towns.");
			}
			else {
				Util.msg(s, "&cFailed to find town.");
			}
			break;
		case "+player":
			if (war.getTeams()[team - 1] == null) {
				Util.msg(s, "&cYou must first create the team with /awar team" + team + " create [name]");
				return;
			}
			war.getTeams()[team - 1].addWhitelistedPlayer(args[1]);
			Util.msg(s, "Successfully added player &e" + args[1] + " &7to whitelisted players.");
			break;
		case "-player":
			if (war.getTeams()[team - 1] == null) {
				Util.msg(s, "&cYou must first create the team with /awar team" + team + " create [name]");
				return;
			}
			war.getTeams()[team - 1].removeWhitelistedPlayer(args[1]);
			Util.msg(s, "Successfully removed player &e" + args[1] + " &7from whitelisted players.");
			break;
		case "setspawn":
			if (war.getTeams()[team - 1] == null) {
				Util.msg(s, "&cYou must first create the team with /awar team" + team + " create [name]");
				return;
			}
			war.setTeamSpawn(team, ((Player) s).getLocation());
			break;
		case "setmascotspawn":
			if (war.getTeams()[team - 1] == null) {
				Util.msg(s, "&cYou must first create the team with /awar team" + team + " create [name]");
				return;
			}
			war.setMascotSpawn(team, ((Player) s).getLocation());
			break;
		default:
			Util.msg(s, "&cInvalid argument!");
			return;
		}
		
		war.displayCreator(s);
	}

}
