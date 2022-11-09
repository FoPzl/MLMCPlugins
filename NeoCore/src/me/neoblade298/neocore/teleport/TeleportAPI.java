package me.neoblade298.neocore.teleport;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.util.Util;

public class TeleportAPI implements Listener {
	private static final int TELEPORT_DELAY = 4; // 4 seconds
	private static HashMap<Player, ArrayList<BukkitTask>> teleports = new HashMap<Player, ArrayList<BukkitTask>>();
	
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();
		
		if (!teleports.containsKey(p)) return;
		Util.msg(p, "&cTeleport cancelled, you took damage!");
		cancelTeleport(p);
	}
	
	public static void teleportPlayer(Player p, Location loc) {
		final Location formerLoc = p.getLocation();
		ArrayList<BukkitTask> tasks = new ArrayList<BukkitTask>(TELEPORT_DELAY + 1);
		Util.msg(p, "&7Teleporting! Don't move for 4s...");
		BukkitTask teleport = new BukkitRunnable() {
			public void run() {
				p.teleport(loc);
			}
		}.runTaskLater(NeoCore.inst(), TELEPORT_DELAY * 20);
		tasks.add(teleport);
		
		for (int i = 1; i < TELEPORT_DELAY; i++) {
			tasks.add(
				new BukkitRunnable() {
					public void run() {
						if (!teleports.containsKey(p)) return;
						
						Location loc = p.getLocation();
						// Player moved more than 1 block
						if (Math.abs(loc.getX() - formerLoc.getX()) > 1 ||
								Math.abs(loc.getY() - formerLoc.getY()) > 1 ||
								Math.abs(loc.getZ() - formerLoc.getZ()) > 1) {
							Util.msg(p, "&cTeleport cancelled, you moved!");
							cancelTeleport(p);
						}
					}
				}.runTaskLater(NeoCore.inst(), i * 20));
		}
		teleports.put(p, tasks);
	}
	
	public static void cancelTeleport(Player p) {
		if (teleports.containsKey(p)) {
			for (BukkitTask task : teleports.remove(p)) {
				task.cancel();
			}
		}
	}
}
