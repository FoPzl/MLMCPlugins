package me.neoblade298.neosessions.sessions.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neosessions.sessions.SessionPlayer;

public class Stats {
	private long startTime;
	private int multiplier;
	private HashMap<SessionPlayer, StatsPlayer> players = new HashMap<SessionPlayer, StatsPlayer>();
	private String key, display;
	
	public Stats(String key, String display, int multiplier, Collection<SessionPlayer> sps) {
		this.key = key;
		this.display = display;
		this.startTime = System.currentTimeMillis();
		this.multiplier = multiplier;
		
		for (SessionPlayer sp : sps) {
			players.put(sp, new StatsPlayer());
		}
	}
	
	public String getKey() {
		return key;
	}
	
	public StatsPlayer getStatsPlayer(SessionPlayer sp) {
		return players.get(sp);
	}
	
	public void display(CommandSender s) {
		for (String line : generateDisplay()) {
			Util.msg(s, line);
		}
	}
	
	public void display() {
		for (String line : generateDisplay()) {
			for (SessionPlayer sp : players.keySet()) {
				if (sp.getPlayer() != null) {
					Util.msg(sp.getPlayer(), line);
				}
			}
		}
	}
	
	private ArrayList<String> generateDisplay() {
		ArrayList<String> msgs = new ArrayList<String>();
		
		// Calculate time
		long time = System.currentTimeMillis() - startTime;
        String timer = "n/a";
		final long hr = TimeUnit.MILLISECONDS.toHours(time);
        final long min = TimeUnit.MILLISECONDS.toMinutes(time - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(time - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(time - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        if (hr > 0) {
        	timer = String.format("%2d:%02d:%02d.%03d", hr, min, sec, ms);
        }
        else {
        	timer = String.format("%2d:%02d.%03d", min, sec, ms);
        }

		msgs.add("§cBoss Stats §7[" + NeoCore.getInstanceDisplay() + "§7] (§4§l" +
		display + " x" + multiplier + "§7) [Time:§c " + timer + "§7]");
		msgs.add("§7----- ");
		msgs.add("§7[§cDamage Dealt §7/ §4Damage Taken §7/ §2Self Healing §7/ §aAlly Healing§7]");
        
        for (Entry<SessionPlayer, StatsPlayer> e : players.entrySet()) {
        	StatsPlayer stats = e.getValue();
			String pClass = e.getKey().getClassName();
			int damageDealt = (int) Math.round((stats.getDamageDealt() * 100) / 100);
			int damageTaken = (int) Math.round((stats.getDamageTaken() * 100) / 100);
			int selfHeal = (int) Math.round((stats.getSelfHealed() * 100) / 100);
			int allyHeal = (int) Math.round((stats.getAllyHealed() * 100) / 100);
			msgs.add("§e" + e.getKey().getName() + "§7 (§e" + pClass + "§7) - [§c" +
			damageDealt + " §7/ §4" + damageTaken + " §7/ §2" + selfHeal + " §7/ §a" + allyHeal + "§7]");
        }
        return msgs;
	}
}
