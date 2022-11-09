package me.neoblade298.townychatbridge.towny;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.mineacademy.chatcontrol.api.ChatChannelEvent;
import org.mineacademy.chatcontrol.api.ChatControlAPI;
import org.mineacademy.chatcontrol.operator.Tag.Type;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.NationAddTownEvent;
import com.palmergames.bukkit.towny.event.NationRemoveTownEvent;
import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import me.neoblade298.neocore.bungee.BungeeAPI;
import me.neoblade298.neocore.bungee.PluginMessageEvent;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.townychatbridge.TownyChatBridge;

public class TownyListener implements Listener {
	TownyAPI api;
	
	@EventHandler
	public void onTownChat(ChatChannelEvent e) {
		if (!(e.getSender() instanceof Player)) return;
		if (!e.getChannel().getName().equalsIgnoreCase("town")) return;
		Player p = (Player) e.getSender();
		
		if (api == null) api = TownyAPI.getInstance();
		Town t = api.getResidentTownOrNull(api.getResident(p));
		
		// Only send message of players with a town
		if (t == null) return;
		String nick = ChatControlAPI.getPlayerCache(p).getTag(Type.NICK);
		BungeeAPI.sendPluginMessage(p, "townchatout", e.getMessage(), t.getUUID().toString(), nick != null ? "*" + nick : p.getName(),
				Long.toString(System.currentTimeMillis()));
	}
	
	@EventHandler
	public void onNationChat(ChatChannelEvent e) {
		if (!(e.getSender() instanceof Player)) return;
		if (!e.getChannel().getName().equalsIgnoreCase("nation")) return;
		Player p = (Player) e.getSender();
		
		if (api == null) api = TownyAPI.getInstance();
		Resident r = api.getResident(p);
		Town t = api.getResidentTownOrNull(r);
		Nation n = api.getResidentNationOrNull(r);
		
		// Only send message of players with a town and nation
		if (t == null || n == null) return;
		String nick = ChatControlAPI.getPlayerCache(p).getTag(Type.NICK);
		BungeeAPI.sendPluginMessage(p, "nationchatout", e.getMessage(), t.getUUID().toString(), nick != null ? "*" + nick : p.getName(),
				t.getName(), Long.toString(System.currentTimeMillis()));
	}

	@EventHandler
	public void onCreateTown(NewTownEvent e) {
		String[] list = new String[2];
		list[0] = "newtown";
		list[1] = e.getTown().getUUID().toString();
		BungeeAPI.sendPluginMessage("townyevents", list);
	}
	
	@EventHandler
	public void onCreateNation(NewNationEvent e) {
		String[] list = new String[3];
		list[0] = "newnation";
		list[1] = e.getNation().getUUID().toString();
		list[2] = e.getNation().getTowns().get(0).getUUID().toString();
		BungeeAPI.sendPluginMessage("townyevents", list);
	}

	@EventHandler
	public void onJoinNation(NationAddTownEvent e) {
		String[] list = new String[3];
		list[0] = "townjoinnation";
		list[1] = e.getTown().getUUID().toString();
		list[2] = e.getNation().getUUID().toString();
		BungeeAPI.sendPluginMessage("townyevents", list);
	}

	@EventHandler
	public void onLeaveNation(NationRemoveTownEvent e) {
		String[] list = new String[3];
		list[0] = "townleavenation";
		list[1] = e.getTown().getUUID().toString();
		list[2] = e.getNation().getUUID().toString();
		BungeeAPI.sendPluginMessage("townyevents", list);
	}
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		String type = e.getChannel();
		switch (type) {
		case "townchatin": handleIncomingTownChat(e);
		break;
		case "nationchatin": handleIncomingNationChat(e);
		break;
		}
	}
	
	private void handleIncomingTownChat(PluginMessageEvent e) {
		if (api == null) api = TownyAPI.getInstance();
		long timestamp = Long.parseLong(e.getMessages().get(3));
		if (timestamp + TownyChatBridge.CHAT_TIMEOUT < System.currentTimeMillis()) return;
		String msg = "&f[&3TC&f] &f" + e.getMessages().get(2) + ": &b" + e.getMessages().get(0);
		
		UUID tuuid = UUID.fromString(e.getMessages().get(1));
		Town town = TownyAPI.getInstance().getTown(tuuid);
		for (Player p : api.getOnlinePlayersInTown(town)) {
			Util.msg(p, msg, false);
		}
	}
	
	private void handleIncomingNationChat(PluginMessageEvent e) {
		if (api == null) api = TownyAPI.getInstance();
		long timestamp = Long.parseLong(e.getMessages().get(3));
		if (timestamp + TownyChatBridge.CHAT_TIMEOUT < System.currentTimeMillis()) return;
		
		UUID tuuid = UUID.fromString(e.getMessages().get(1));
		Town town = api.getTown(tuuid);
		Nation nation = town.getNationOrNull();
		String msg = "&f[&6NC&f] &f[&e" + town.getName() + "&f] " + e.getMessages().get(2) + ": &e" + e.getMessages().get(0);
		
		for (Player p : api.getOnlinePlayers(nation)) {
			Util.msg(p, msg, false);
		}
	}
}
