package me.neoblade298.neochangelog;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Commands implements CommandExecutor {
	private Main main = null;

	public Commands(Main plugin) {
		main = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		List<List<String>> days = main.getFM().getDays();

		// View first 3 days
		if (args.length < 1) {
			if (sender.hasPermission("changelog.view")) {
				if (days == null) {
					sender.sendMessage("Changelog is empty!");
					return true;
				}
				for (int day = 2; day >= 0; day--) {
					printDay(sender, day, days);
				}
				ComponentBuilder builder = new ComponentBuilder("§e<< Previous")
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to view previous page!")))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/changelog 1"))
						.append("§f ----- ", FormatRetention.NONE)
						.append("§eNext >>")
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("There are no next pages!")));
				sender.spigot().sendMessage(builder.create());
				return true;
			}
			else {
				sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
				return true;
			}
		}

		// View 3 days starting from some page
		else if (args.length == 1 && StringUtils.isNumeric(args[0])) {
			if (sender.hasPermission("changelog.view")) {
				int page = Integer.parseInt(args[0]);
				int chosenDay = page * 3;
				if (chosenDay + 2 >= days.size() || chosenDay < 0) {
					sender.sendMessage(ChatColor.RED + "No more changelog pages!");
					return true;
				}
				int day = chosenDay + 2 >= days.size() ? days.size() - 1 : chosenDay + 2;
				while (day >= 0 && day >= chosenDay) {
					printDay(sender, day, days);
					day--;
				}
				int next = page - 1 < 0 ? 0 : page - 1;
				int prev = chosenDay + 3 < days.size() ? page + 1 : page;
				ComponentBuilder builder = new ComponentBuilder("§e<< Previous")
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to view previous page!")))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/changelog " + prev))
						.append("§f ----- ")
						.append("§eNext >>")
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to view next page!")))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/changelog " + next));
				sender.spigot().sendMessage(builder.create());
				return true;
			}
			else {
				sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
				return true;
			}
		}

		// View x-y pages
		else if (args.length == 1 && StringUtils.isNumeric(args[0])) {
			if (sender.hasPermission("changelog.view")) {
				int arg1 = Integer.parseInt(args[0]);
				if (arg1 >= days.size()) {
					sender.sendMessage(ChatColor.RED + "There are not that many changelog pages!");
					return true;
				}
				for (int day = arg1 + 2; day >= 0; day--) {
					printDay(sender, day, days);
				}
				return true;
			}
			else {
				sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
				return true;
			}
		}

		else if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("help")) {
				if (sender.hasPermission("changelog.edit")) {
					sender.sendMessage(ChatColor.YELLOW + "/changelog #-#");
					sender.sendMessage(ChatColor.YELLOW + "/changelog add [message]");
					sender.sendMessage(ChatColor.YELLOW + "/changelog addto [day index] [message]");
					sender.sendMessage(ChatColor.YELLOW + "/changelog remove [day index] [index]");
					sender.sendMessage(ChatColor.YELLOW + "/changelog removeday [day index]");
					sender.sendMessage(ChatColor.YELLOW + "/changelog removedays [day index]");
					return true;
				}
				else {
					sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("add")) {
				if (sender.hasPermission("changelog.edit")) {
					if (args.length > 1) {
						String log = args[1];
						for (int i = 2; i < args.length; i++) {
							log += " " + args[i];
						}
						if (main.getFM().addLog(log)) {
							sender.sendMessage("Successfully added log!");
							return true;
						}
						return false;
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("addto")) {
				if (sender.hasPermission("changelog.edit")) {
					if (args.length > 2 && StringUtils.isNumeric(args[1])) {
						int arg1 = Integer.parseInt(args[1]) - 1;
						String log = args[2];
						for (int i = 3; i < args.length; i++) {
							log += " " + args[i];
						}
						if (main.getFM().addLog(log, arg1)) {
							sender.sendMessage("Successfully added log!");
							return true;
						}
						return false;
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("remove")) {
				if (sender.hasPermission("changelog.edit")) {
					if (args.length == 3) {
						if (StringUtils.isNumeric(args[1]) && StringUtils.isNumeric(args[2])) {
							int day = Integer.parseInt(args[1]) - 1;
							int num = Integer.parseInt(args[2]);
							if (main.getFM().removeLog(day, num)) {
								sender.sendMessage("Successfully removed log!");
								return true;
							}
							return false;
						}
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("removeday")) {
				if (sender.hasPermission("changelog.edit")) {
					if (args.length == 2) {
						if (StringUtils.isNumeric(args[1])) {
							int arg1 = Integer.parseInt(args[1]);
							if (main.getFM().removeDay(arg1)) {
								sender.sendMessage("Successfully removed day!");
								return true;
							}
							return false;
						}
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("removedays")) {
				if (sender.hasPermission("changelog.edit")) {
					if (args.length == 2) {
						if (StringUtils.isNumeric(args[1])) {
							int arg1 = Integer.parseInt(args[1]);
							if (main.getFM().removeDays(arg1)) {
								sender.sendMessage("Successfully removed days!");
								return true;
							}
							return false;
						}
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
					return true;
				}
			}
		}
		return false;
	}
	
	private void printDay(CommandSender sender, int day, List<List<String>> days) {
		sender.sendMessage(
				"" + ChatColor.RED + "Date: " + days.get(day).get(0) + ChatColor.GRAY + " (" + (day + 1) + ")");
		sender.sendMessage("" + ChatColor.RED + ChatColor.STRIKETHROUGH + "---------------");
		for (int entry = 1; entry < days.get(day).size(); entry++) {
			sender.sendMessage(ChatColor.GOLD + "" + entry + ": " + ChatColor.GRAY
					+ ((String) days.get(day).get(entry)).replaceAll("&", "§"));
		}
	}
}
