package me.neoblade298.townychatbridge.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.chatcontrol.api.ChatChannelEvent;
import org.mineacademy.chatcontrol.api.ChatControlAPI;
import org.mineacademy.chatcontrol.operator.Tag.Type;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.bungee.BungeeAPI;
import me.neoblade298.neocore.bungee.PluginMessageEvent;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.townychatbridge.TownyChatBridge;

public class InstanceListener implements Listener {
	private HashSet<UUID> towns = new HashSet<UUID>();
	private HashMap<UUID, UUID> townToNation = new HashMap<UUID, UUID>(); // Key town uuid, value nation uuid
	private HashMap<UUID, HashSet<UUID>> nationToTowns = new HashMap<UUID, HashSet<UUID>>();
	private HashMap<UUID, HashSet<Player>> onlinePlayers = new HashMap<UUID, HashSet<Player>>(); // Key town uuid, value player
	private HashMap<Player, UUID> playerToTown = new HashMap<Player, UUID>();

	public InstanceListener() {
		new BukkitRunnable() {
			public void run() {
				try {
					
					// Towns in a nation
					ResultSet rs = NeoCore.getStatement()
							.executeQuery("SELECT b.uuid, c.uuid FROM MLMC.TOWNY_TOWNS as b,"
									+ " MLMC.TOWNY_NATIONS as c WHERE b.nation = c.name;");
					while (rs.next()) {
						UUID tuuid = UUID.fromString(rs.getString(1));
						towns.add(tuuid);
						UUID nuuid = UUID.fromString(rs.getString(2));
						townToNation.put(tuuid, nuuid);
						HashSet<UUID> towns = nationToTowns.getOrDefault(nuuid, new HashSet<UUID>());
						towns.add(tuuid);
						nationToTowns.putIfAbsent(nuuid, towns);
					}
					
					// Towns not in a nation
					rs = NeoCore.getStatement()
							.executeQuery("SELECT b.uuid FROM MLMC.TOWNY_TOWNS as b WHERE b.nation = '';");
					while (rs.next()) {
						UUID tuuid = UUID.fromString(rs.getString(1));
						towns.add(tuuid);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(TownyChatBridge.inst());
	}

	@EventHandler
	public void onTownChat(ChatChannelEvent e) {
		if (!(e.getSender() instanceof Player)) return;
		if (!e.getChannel().getName().equalsIgnoreCase("town")) return;
		e.setCancelled(true);
		Player p = (Player) e.getSender();
		if (!playerToTown.containsKey(p)) return;
		String nick = ChatControlAPI.getPlayerCache(p).getTag(Type.NICK);
		String display = nick != null ? "*" + nick : p.getName();
		UUID tuuid = playerToTown.get(p);
		
		handleTownChat(tuuid, display, e.getMessage());
		BungeeAPI.sendPluginMessage(p, "townchatin", e.getMessage(), tuuid.toString(), display, Long.toString(System.currentTimeMillis()));
	}

	@EventHandler
	public void onNationChat(ChatChannelEvent e) {
		if (!(e.getSender() instanceof Player)) return;
		if (!e.getChannel().getName().equalsIgnoreCase("nation")) return;
		e.setCancelled(true);
		Player p = (Player) e.getSender();
		if (!playerToTown.containsKey(p)) return;
		String nick = ChatControlAPI.getPlayerCache(p).getTag(Type.NICK);
		String display = nick != null ? "*" + nick : p.getName();
		UUID tuuid = playerToTown.get(p);

		handleNationChat(tuuid, null, display, e.getMessage());
		BungeeAPI.sendPluginMessage(p, "nationchatin", e.getMessage(), tuuid.toString(), display, Long.toString(System.currentTimeMillis()));
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		switch (e.getChannel()) {
		case "townchatout":
			handleTownChat(e);
			break;
		case "nationchatout":
			handleNationChat(e);
			break;
		case "townyevent":
			handleTownyEvent(e);
			break;
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		new BukkitRunnable() {
			public void run() {
				try {
					ResultSet rs = NeoCore.getStatement().executeQuery(
							"SELECT b.uuid FROM MLMC.TOWNY_RESIDENTS as a, MLMC.TOWNY_TOWNS as b "
									+ "WHERE a.town = b.name AND a.name = '"
									+ p.getName() + "';");

					if (!rs.next()) return;
					String townuuid = rs.getString(1);

					// Add to online players
					if (townuuid.isEmpty()) return;
					UUID tuuid = UUID.fromString(townuuid);
					HashSet<Player> players = onlinePlayers.getOrDefault(tuuid, new HashSet<Player>());
					players.add(p);
					onlinePlayers.putIfAbsent(tuuid, players);
					playerToTown.put(p, tuuid);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}.runTaskAsynchronously(TownyChatBridge.inst());
	}

	private void handleTownChat(PluginMessageEvent e) {
		UUID town = UUID.fromString(e.getMessages().get(1));
		long timestamp = Long.parseLong(e.getMessages().get(3));
		if (timestamp + TownyChatBridge.CHAT_TIMEOUT < System.currentTimeMillis()) return;
		
		handleTownChat(town, e.getMessages().get(2), e.getMessages().get(0));
	}
	
	private void handleTownChat(UUID town, String name, String msg) {
		String formatted = "&f[&3TC&f] &f" + name + ": &b" + msg;
		if (onlinePlayers.containsKey(town)) {
			for (Player p : onlinePlayers.get(town)) {
				Util.msg(p, formatted, false);
			}
		}
	}
	
	private void handleNationChat(PluginMessageEvent e) {
		UUID town = UUID.fromString(e.getMessages().get(1));
		long timestamp = Long.parseLong(e.getMessages().get(4));
		if (timestamp + TownyChatBridge.CHAT_TIMEOUT < System.currentTimeMillis()) return;
		
		handleNationChat(town, e.getMessages().get(3), e.getMessages().get(2), e.getMessages().get(0));
	}
	
	private void handleNationChat(UUID tuuid, String town, String name, String msg) {
		
		String formatted;
		if (town != null) {
			formatted = "&f[&6NC&f] &f[&e" + town + "&f] " + name + ": &e" + msg;
		}
		else {
			formatted = "&f[&6NC&f] &f" + name + ": &e" + msg;
		}
		
		UUID nation = townToNation.get(tuuid);
		for (UUID townInNation : nationToTowns.get(nation)) {
			if (onlinePlayers.containsKey(townInNation)) {
				for (Player p : onlinePlayers.get(townInNation)) {
					Util.msg(p, formatted, false);
				}
			}
		}
	}

	private void handleTownyEvent(PluginMessageEvent e) {
		String type = e.getMessages().get(0);
		ArrayList<String> msgs = e.getMessages();
		switch (type) {
		case "newtown": towns.add(UUID.fromString(e.getMessages().get(1)));
		break;
		case "newnation": townToNation.put(UUID.fromString(msgs.get(1)), UUID.fromString(msgs.get(2)));
		break;
		case "townjoinnation": townToNation.put(UUID.fromString(msgs.get(1)), UUID.fromString(msgs.get(2)));
		break;
		case "townleavenation": townToNation.remove(UUID.fromString(msgs.get(1)));
		break;
		}
	}
}
