package me.neoblade298.neocore.messaging;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.io.FileLoader;
import me.neoblade298.neocore.util.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;

public class MessagingManager {
	private static HashMap<String, BaseComponent[][]> messages = new HashMap<String, BaseComponent[][]>();
	
	private static final FileLoader msgLoader;
	
	static {
		msgLoader = (cfg, file) -> {
			for (String key : cfg.getKeys(false)) {
				ConfigurationSection sec = cfg.getConfigurationSection(key);
				messages.put(key.toUpperCase(), parsePage(sec));
			}
		};
	}
	
	public static void reload() throws NeoIOException {
		messages.clear();
		NeoCore.loadFiles(new File(NeoCore.inst().getDataFolder(), "messages"), msgLoader);
	}
	
	public static void sendMessage(CommandSender s, String key) {
		sendMessage(s, key, 1);
	}
	
	public static void sendMessage(CommandSender s, String key, int page) {
		page--;
		if (messages.containsKey(key.toUpperCase())) {
			BaseComponent[][] msgs = messages.get(key.toUpperCase());
			if (msgs.length <= page) {
				Util.msg(s, "&cThis doesn't have " + page + " pages!");
				return;
			}
			
			s.spigot().sendMessage(msgs[page]);
		}
		else {
			Util.msg(s, "&cMessage " + key + " doesn't exist!");
			Bukkit.getLogger().warning("[NeoCore] Failed to send message to " + s.getName() + ", key doesn't exist: " + key);
		}
	}
	
	public static BaseComponent[][] parsePage(ConfigurationSection cfg) {
		BaseComponent[][] list;
		if (cfg.contains("pages")) {
			ConfigurationSection pages = cfg.getConfigurationSection("pages");
			Set<String> keys = pages.getKeys(false);
			list = new BaseComponent[keys.size()][];
			
			int i = 0;
			for (String key : pages.getKeys(false)) {
				list[i++] = parseMessage(pages.getConfigurationSection(key));
			}
		}
		else {
			list = new BaseComponent[1][];
			list[0] = parseMessage(cfg);
		}
		return list;
	}
	
	public static BaseComponent[] parseMessage(ConfigurationSection cfg) {
		boolean firstKey = true;
		ComponentBuilder builder = null;
		for (String key : cfg.getKeys(false)) {
			ConfigurationSection sec = cfg.getConfigurationSection(key);
			if (firstKey) {
				firstKey = false;
				builder = new ComponentBuilder(Util.translateColors(sec.getString("text")));
			}
			else {
				builder.append(Util.translateColors(sec.getString("text")), FormatRetention.NONE);
			}
			
			if (sec.getString("suggest") != null) {
				builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, sec.getString("suggest")));
			}
			
			if (sec.getString("url") != null) {
				builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, sec.getString("url")));
			}
			
			if (sec.getString("run") != null) {
				builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, sec.getString("run")));
			}
			
			if (sec.getString("hover") != null) {
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Util.translateColors(sec.getString("hover")))));
			}
		}
		return builder.create();
	}
}
