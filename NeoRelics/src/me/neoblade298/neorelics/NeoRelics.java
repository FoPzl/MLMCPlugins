package me.neoblade298.neorelics;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerAttributeLoadEvent;
import com.sucy.skill.api.event.PlayerAttributeUnloadEvent;

import de.tr7zw.nbtapi.NBTItem;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.io.FileLoader;

public class NeoRelics extends JavaPlugin implements Listener {
	private static HashMap<String, Relic> relics;
	public static HashMap<UUID, PlayerSet> playersets;
	private static HashSet<Player> disableRecalculate;
	private static File file;
	public static boolean debug;
	private static FileLoader relicLoader;
	
	static {
		relicLoader = (cfg, file) -> {
			for (String key : cfg.getKeys(false)) {
				Relic relic = new Relic(cfg.getConfigurationSection(key));
				relics.put(key, relic);
			}
		};
	}
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("NeoRelics Enabled");
		getServer().getPluginManager().registerEvents(this, this);
	    this.getCommand("relic").setExecutor(new Commands(this));

		file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveResource("config.yml", false);
		}
		relics = new HashMap<String, Relic>();
	    disableRecalculate = new HashSet<Player>();
	    playersets = new HashMap<UUID, PlayerSet>();
	    reload();
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoRelics Disabled");
	    super.onDisable();
	}
	
	public static void reload() {
		relics.clear();
		disableRecalculate.clear();
		try {
			NeoCore.loadFiles(file, relicLoader);
		} catch (NeoIOException e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, Relic> getRelics() {
		return relics;
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player)) return;
		Player p = (Player) e.getPlayer();
		
		if (!SkillAPI.getSettings().isWorldEnabled(p.getWorld())) return;

		recalculateSetEffect(p);
	}
	
	@EventHandler
	public void onAttributeLoad(PlayerAttributeLoadEvent e) {
		recalculateSetEffect(e.getPlayer());
	}

	@EventHandler
	public void onAttributeUnload(PlayerAttributeUnloadEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		playersets.remove(uuid);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		disableRecalculate.add(p);
		UUID uuid = p.getUniqueId();
		playersets.remove(uuid);
	}
	
	@EventHandler
	public void onKicked(PlayerKickEvent e) {
		Player p = e.getPlayer();
		disableRecalculate.add(p);
		UUID uuid = p.getUniqueId();
		playersets.remove(uuid);
	}
	
	@EventHandler
	public void onItemBreak(PlayerItemBreakEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (!SkillAPI.getSettings().isWorldEnabled(p.getWorld())) return;
		
		if (playersets.containsKey(uuid)) {
			if (checkRelic(p, e.getBrokenItem())) playersets.get(uuid).decrementNum();
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onChangeSlot(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		if (!SkillAPI.getSettings().isWorldEnabled(p.getWorld())) return;
		
		ItemStack oldItem = p.getInventory().getContents()[e.getPreviousSlot()];
		ItemStack newItem = p.getInventory().getContents()[e.getNewSlot()];
		
		if (checkRelic(p, newItem) && !checkRelic(p, oldItem)) {
			playersets.get(p.getUniqueId()).incrementNum();
		}
		else if (!checkRelic(p, newItem) && checkRelic(p, oldItem)) {
			playersets.get(p.getUniqueId()).decrementNum();
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (!SkillAPI.getSettings().isWorldEnabled(p.getWorld())) return;
		
		ItemStack item = e.getItemDrop().getItemStack();
		if (checkRelic(p, item)) playersets.get(p.getUniqueId()).decrementNum();
	}
	
	// Used to calculate a set effect from scratch
	private void recalculateSetEffect(Player p) {
		if (!SkillAPI.isLoaded(p)) return;
		
		// InventoryCloseEvent happens after PlayerQuitEvent, so you have to
		// briefly disable recalculation. It reenables itself because Player
		// object disappears on logout from hashset.
		if (disableRecalculate.contains(p)) return;
		ItemStack main = p.getInventory().getItemInMainHand();
		ItemStack off = p.getInventory().getItemInOffHand();
		ItemStack[] armor = p.getInventory().getArmorContents();
		
		if (playersets.containsKey(p.getUniqueId())) {
			playersets.remove(p.getUniqueId()).remove();
		}
		
		int num = 0;
		if (checkRelic(p, main)) num++;
		if (checkRelic(p, off)) num++;
		for (ItemStack item : armor) {
			if (checkRelic(p, item)) num++;
		}
		
		if (num > 0) playersets.get(p.getUniqueId()).setNumRelics(num);
	}
	
	private boolean checkRelic(Player p, ItemStack item) {
		if (playersets.containsKey(p.getUniqueId())) {
			return hasRelic(p, item, playersets.get(p.getUniqueId()).getSet());
		}
		else {
			return hasRelic(p, item);
		}
	}
	
	// Checks if the given item has the provided relic in it
	private boolean hasRelic(Player p, ItemStack item, Relic relic) {
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
		if (item != null && !item.getType().isAir()) {
			NBTItem nbti = new NBTItem(item);
			for (int i = 1; i <= nbti.getInteger("slotsCreated"); i++) {
				String augmentName = nbti.getString("slot" + i + "Augment");
				if (augmentName.equalsIgnoreCase(relic.getKey())) {
					return true;
				}
			}
		}
		return false;
	}
	
	// Checks if the given item has a relic in it, gives the player that set if true
	private boolean hasRelic(Player p, ItemStack item) {
		if (item != null && !item.getType().isAir()) {
			NBTItem nbti = new NBTItem(item);
			for (int i = 1; i <= nbti.getInteger("slotsCreated"); i++) {
				String augmentName = nbti.getString("slot" + i + "Augment");
				if (augmentName.startsWith("Relic")) {
					if (relics.containsKey(augmentName)) {
						PlayerSet pSet = new PlayerSet(relics.get(augmentName), 0, p);
						playersets.put(p.getUniqueId(), pSet);
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
