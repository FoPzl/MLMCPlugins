package me.neoblade298.neosessions.commands.director;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neosessions.NeoSessions;
import me.neoblade298.neosessions.directors.DirectorManager;

public class CmdSessionsShow implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player"),
			new CommandArgument("session")));

	@Override
	public String getDescription() {
		return "Show existing sessions";
	}

	@Override
	public String getKey() {
		return "show";
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
		try {
			Statement stmt = NeoCore.getStatement();
			HashMap<String, HashMap<String, ArrayList<String>>> hosts = new HashMap<String, HashMap<String, ArrayList<String>>>();
			for (String host : DirectorManager.getSessionHosts()) {
				hosts.put(host, new HashMap<String, ArrayList<String>>());
			}

			ResultSet rs = stmt.executeQuery("SELECT * FROM sessions_players");
			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				String instance = rs.getString("instance");
				String sessionKey = rs.getString("session_key");
				ArrayList<String> names = hosts.get(instance)
						.getOrDefault(sessionKey, new ArrayList<String>());
				names.add(Bukkit.getOfflinePlayer(uuid).getName());
				hosts.get(instance).putIfAbsent(sessionKey, names);
			}

			for (String host : DirectorManager.getSessionHosts()) {
				HashMap<String, ArrayList<String>> sessions = hosts.get(host);
				if (sessions.size() == 0) {
					Util.msg(s, "&c" + host + "&7: Empty", false);
				}
				else {
					String temp = "&c" + host + "&7: ";
					for (Entry<String, ArrayList<String>> e : sessions.entrySet()) {
						temp += "&6" + e.getKey() + " &7(&e" + e.getValue().get(0);
						for (int i = 1; i < e.getValue().size(); i++) {
							temp += "&7, &e" + e.getValue().get(i);
						}
						temp += "&7), ";
					}
					temp = temp.substring(0, temp.length() - 2);
					Util.msg(s, temp, false);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}

}
