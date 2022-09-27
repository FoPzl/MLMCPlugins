package me.neoblade298.neocore.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;

public class CmdCoreMessage implements Subcommand {

	@Override
	public String getPermission() {
		return "mycommand.staff";
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "msg";
	}

	@Override
	public String getDescription() {
		return "Sends a message to a player";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		parseAndRun(s, args);
	}
	
	public static void parseAndRun(CommandSender s, String[] args) {
		int offset = 0;
		CommandSender recipient = s;
		if (Bukkit.getPlayer(args[0]) != null) {
			recipient = Bukkit.getPlayer(args[0]);
			offset = 1;
		}
		
		// message only
		if (offset == 0) {
			Util.msg(recipient, Util.connectArgs(args));
		}
		// message and page
		else if (offset == 1) {
			Util.msg(recipient, Util.connectArgs(args, 1));
		}
	}

	@Override
	public String getArgOverride() {
		return "{player} [msg]";
	}
}
