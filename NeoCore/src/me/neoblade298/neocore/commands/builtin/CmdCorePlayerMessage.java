package me.neoblade298.neocore.commands.builtin;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class CmdCorePlayerMessage implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(
			new CommandArgument("message"), new CommandArgument("page", false)));

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public String getKey() {
		return "pmsg";
	}

	@Override
	public String getDescription() {
		return "Plays a message, usable by player but hidden";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		CmdCoreSendMessage.parseAndRun(s, args);
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
}
