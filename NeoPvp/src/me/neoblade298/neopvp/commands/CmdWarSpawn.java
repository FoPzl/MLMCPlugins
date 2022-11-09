package me.neoblade298.neopvp.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.teleport.TeleportAPI;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neopvp.wars.War;
import me.neoblade298.neopvp.wars.WarManager;
import me.neoblade298.neopvp.wars.WarTeam;

public class CmdWarSpawn implements Subcommand {
	private static final CommandArguments args = new CommandArguments();

	@Override
	public String getDescription() {
		return "Teleport to the current war if you're a fighter";
	}

	@Override
	public String getKey() {
		return "spawn";
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
	public void run(CommandSender s, String[] args) {
		Player p = (Player) s;
		if (WarManager.getOngoingWars().size() > 0) {
			for (War war : WarManager.getOngoingWars().values()) {
				WarTeam t1 = war.getTeams()[0];
				WarTeam t2 = war.getTeams()[1];
				if (t1.isMember(p)) {
					TeleportAPI.teleportPlayer(p, t1.getSpawn());
					return;
				}
				else if (t2.isMember(p)) {
					TeleportAPI.teleportPlayer(p, t2.getSpawn());
					return;
				}
			}
		}
		Util.msg(s, "&cYou're not part of any ongoing war!");
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
