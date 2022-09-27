package me.neoblade298.neocore.bar;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.events.PlayerTagChangedEvent;
import me.neoblade298.neocore.events.ValueChangeType;
import me.neoblade298.neocore.player.PlayerDataManager;
import me.neoblade298.neocore.player.PlayerTags;

public class BarAPI implements Listener {
	private static HashMap<Player, CoreBar> bars = new HashMap<Player, CoreBar>();
	private static PlayerTags ptags;
	
	public BarAPI() {
        ptags = PlayerDataManager.createPlayerTags("neocore", NeoCore.inst(), false);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!bars.containsKey(p)) {
			BossBar bar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);
			CoreBar cb = new CoreBar(p, bar, ptags);
			bars.put(p, cb);
		}
	}
	
	@EventHandler
	public void onTagChange(PlayerTagChangedEvent e) {
		Player p = e.getPlayer();
		
		if (e.getKey().equals("neocore") && e.getSubkey().equals("disable-corebar")) {
			if (e.getType() == ValueChangeType.ADDED) bars.get(p).setEnabled(false);
			if (e.getType() == ValueChangeType.REMOVED) bars.get(p).setEnabled(true);
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		CoreBar cb = bars.get(p);
		cb.setVisible(false);
	}
	
	public static CoreBar getBar(Player p) {
		return bars.get(p);
	}
}
