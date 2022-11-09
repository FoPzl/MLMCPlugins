package me.neoblade298.neoplaceholders.placeholders.other;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.neoblade298.neoplaceholders.NeoPlaceholders;

public class EtcPlaceholders {
	private NeoPlaceholders main;
	
	public EtcPlaceholders (NeoPlaceholders main) {
		this.main = main;
	}

	public void registerPlaceholders() {

		// World placeholder
		PlaceholderAPI.registerPlaceholder(this.main, "MLMCWorld", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				boolean online = e.isOnline();
				Player p = e.getPlayer();
				String placeholder = "Loading...";

				if (online && p != null) {
					String world = p.getWorld().getName();
					if (world.startsWith("Aurum")) {
						return "Resource";
					}
					else if (world.equalsIgnoreCase("Aegis")) {
						return "Old Towny";
					}
					else if (world.equalsIgnoreCase("Eretras")) {
						return "New Towny";
					}
					return world;
				}
				return placeholder;
			}
		});
	}
}
