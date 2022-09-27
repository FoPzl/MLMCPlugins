package me.neoblade298.neocore.commands;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	private TreeMap<String, Subcommand> handlers = new TreeMap<String, Subcommand>();
	private String base, perm;
	private ChatColor color;
	
	public CommandManager(String base, JavaPlugin plugin) {
		this(base, null, ChatColor.RED, plugin);
	}
	
	public CommandManager(String base, String perm, JavaPlugin plugin) {
		this(base, perm, ChatColor.RED, plugin);
	}
	
	public CommandManager(String base, ChatColor color, JavaPlugin plugin) {
		this(base, null, ChatColor.RED, plugin);
	}
	
	public CommandManager(String base, String perm, ChatColor color, JavaPlugin plugin) {
		this.base = base;
		this.perm = perm;
		this.color = color;
		plugin.getCommand(base).setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		return handleCommand(sender, args);
	}
	
	public boolean handleCommand(CommandSender sender, String[] args) {
		if (args.length == 0) {
			if (handlers.containsKey("") ) {
				runCommand("", sender, args);
				return true;
			}
			else {
				return false;
			}
		}
		else if (StringUtils.isNumeric(args[0]) && handlers.get("") != null && handlers.get("") instanceof CmdList) {
			runCommand("", sender, args);
			return true;
		}
		else if (handlers.containsKey(args[0].toUpperCase())) {
			runCommand(args[0], sender, args);
			return true;
		}
		else if (!handlers.containsKey(args[0].toUpperCase()) && handlers.containsKey("")) {
			Subcommand cmd = handlers.get("");
			CommandArguments cArgs = cmd.getArgs();
			if (cArgs != null) {
				if (cArgs.getMin() > 0 || cArgs.getMax() > 0) {
					runCommand("", sender, args);
					return true;
				}
			}
		}
		return false;
	}
	
	public void runCommand(String key, CommandSender s, String[] args) {
		runCommand(key, s, args, true);
	}
	
	public void runCommand(String key, CommandSender s, String[] args, boolean shouldCheck) {
		runCommand(handlers.get(key.toUpperCase()), s, args, shouldCheck);
	}
	
	public void runCommand(Subcommand sc, CommandSender s, String[] args, boolean shouldCheck) {
		String[] reducedArgs = args;
		
		// Do not reduce args if key is ""
		if (args.length > 0 && sc.getKey().length() != 0) {
			reducedArgs = Arrays.copyOfRange(args, 1, args.length);
		}
		
		if (!shouldCheck || check(sc, s, reducedArgs)) {
			sc.run(s, reducedArgs);
		}
	}
	
	private boolean check(Subcommand cmd, CommandSender s, String[] args) {
		if (((perm != null && perm.length() != 0) && !s.hasPermission(perm)) && !s.isOp()) {
			s.sendMessage("§cYou're missing the permission: " + perm);
			return false;
		}
		
		if (((cmd.getPermission() != null && cmd.getPermission().length() != 0) && !s.hasPermission(cmd.getPermission())) && !s.isOp()) {
			s.sendMessage("§cYou're missing the permission: " + cmd.getPermission());
			return false;
		}

		if ((cmd.getRunner() == SubcommandRunner.PLAYER_ONLY && !(s instanceof Player)) ||
				(cmd.getRunner() == SubcommandRunner.CONSOLE_ONLY && !(s instanceof ConsoleCommandSender))) {
			s.sendMessage("§cYou are the wrong type of user for this command!");
			return false;
		}
		
		if (cmd.getArgs() != null) {
			CommandArguments cargs = cmd.getArgs();
			if (args.length < cargs.getMin()) {
				s.sendMessage("§cThis command requires at least " + cargs.getMin() + " args but received " + args.length + ".");
				return false;
			}
			
			if (args.length > cargs.getMax()) {
				s.sendMessage("§cThis command requires at most " + cargs.getMax() + " args but received " + args.length + ".");
				return false;
			}
		}
		
		return true;
	}
	
	public void register(Subcommand... cmds) {
		for (Subcommand cmd : cmds) {
			handlers.put(cmd.getKey().toUpperCase(), cmd);
			
			if (cmd.getAliases() != null) {
				for (String alias : cmd.getAliases()) {
					handlers.put(alias.toUpperCase(), new HiddenSubcommand(cmd, alias));
				}
			}
		}
	}
	
	public void registerCommandList(String key, String perm, ChatColor color) {
		handlers.put(key.toUpperCase(), new CmdList(key, base, perm, handlers, this.color, color));
	}
	
	public void registerCommandList(String key, ChatColor color) {
		registerCommandList(key, null, color);
	}
	
	public void registerCommandList(String key) {
		registerCommandList(key, null, null);
	}
	
	public Subcommand getCommand(String key) {
		return handlers.get(key.toUpperCase());
	}
	
	public Set<String> getKeys() {
		return handlers.keySet();
	}
}
