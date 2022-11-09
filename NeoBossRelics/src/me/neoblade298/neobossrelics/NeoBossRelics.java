package me.neoblade298.neobossrelics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

public class NeoBossRelics extends JavaPlugin implements org.bukkit.event.Listener {
	public HashMap<String, Set> sets;
	public HashMap<UUID, PlayerSet> playersets;
	private ArrayList<String> enabledWorlds;
	private HashSet<Player> disableRecalculate;
	private File file;
	public boolean debug;
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("NeoBossRelics Enabled");
		getServer().getPluginManager().registerEvents(this, this);
	    this.getCommand("relic").setExecutor(new Commands(this));

		file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveResource("config.yml", false);
		}
	    sets = new HashMap<String, Set>();
	    disableRecalculate = new HashSet<Player>();
	    playersets = new HashMap<UUID, PlayerSet>();
	    enabledWorlds = new ArrayList<String>();
	    loadConfig();
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoBossRelics Disabled");
	    super.onDisable();
	}
	
	public void loadConfig() {
		// Load sets
		sets.clear();
		disableRecalculate.clear();
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		
		ConfigurationSection setSection = conf.getConfigurationSection("sets");
		for (String setName : setSection.getKeys(false)) {
			HashMap<Integer, SetEffect> setEffects = new HashMap<Integer, SetEffect>();
			ConfigurationSection currentSet = setSection.getConfigurationSection(setName);
			
			// Get each number for the set
			for (String numString : currentSet.getKeys(false)) {
				ConfigurationSection currentSetNum = currentSet.getConfigurationSection(numString);
				int num = Integer.parseInt(numString);
				String flag = currentSetNum.getString("flag");
				ArrayList<String> attrs = (ArrayList<String>) currentSetNum.getStringList("attributes");
				HashMap<String, Integer> attrMap = new HashMap<String, Integer>();
				for (String attr : attrs) {
					String[] attrSplit = attr.split(":");
					attrMap.put(attrSplit[0], Integer.parseInt(attrSplit[1]));
				}
				SetEffect eff = new SetEffect(attrMap, flag);
				setEffects.put(num, eff);
			}
			Set set = new Set(setName, setEffects);
			sets.put(setName, set);
		}
		
		// Load enabled worlds
		enabledWorlds.clear();
		enabledWorlds = (ArrayList<String>) conf.getStringList("enabled-worlds");
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player)) return;
		Player p = (Player) e.getPlayer();
		if (!enabledWorlds.contains(p.getWorld().getName())) return;

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
		this.playersets.remove(uuid);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		disableRecalculate.add(p);
		UUID uuid = p.getUniqueId();
		this.playersets.remove(uuid);
	}
	
	@EventHandler
	public void onKicked(PlayerKickEvent e) {
		Player p = e.getPlayer();
		disableRecalculate.add(p);
		UUID uuid = p.getUniqueId();
		this.playersets.remove(uuid);
	}
	
	@EventHandler
	public void onItemBreak(PlayerItemBreakEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (!enabledWorlds.contains(p.getWorld().getName())) return;
		
		if (this.playersets.containsKey(uuid)) {
			if (checkRelic(p, e.getBrokenItem())) this.playersets.get(uuid).decrementNum();
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onChangeSlot(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		if (!enabledWorlds.contains(p.getWorld().getName())) return;
		
		ItemStack oldItem = p.getInventory().getContents()[e.getPreviousSlot()];
		ItemStack newItem = p.getInventory().getContents()[e.getNewSlot()];
		
		if (checkRelic(p, newItem) && !checkRelic(p, oldItem)) {
			this.playersets.get(p.getUniqueId()).incrementNum();
		}
		else if (!checkRelic(p, newItem) && checkRelic(p, oldItem)) {
			this.playersets.get(p.getUniqueId()).decrementNum();
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (!enabledWorlds.contains(p.getWorld().getName())) return;
		
		ItemStack item = e.getItemDrop().getItemStack();
		if (checkRelic(p, item)) this.playersets.get(p.getUniqueId()).decrementNum();
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
		
		if (this.playersets.containsKey(p.getUniqueId())) {
			this.playersets.remove(p.getUniqueId()).remove();
		}
		
		int num = 0;
		if (checkRelic(p, main)) num++;
		if (checkRelic(p, off)) num++;
		for (ItemStack item : armor) {
			if (checkRelic(p, item)) num++;
		}
		
		if (num > 0) this.playersets.get(p.getUniqueId()).setNumRelics(num);
	}
	
	private boolean checkRelic(Player p, ItemStack item) {
		if (this.playersets.containsKey(p.getUniqueId())) {
			return hasRelic(p, item, this.playersets.get(p.getUniqueId()).getSet());
		}
		else {
			return hasRelic(p, item);
		}
	}
	
	// Checks if the given item has the provided relic in it
	private boolean hasRelic(Player p, ItemStack item, Set set) {
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
		if (item != null && !item.getType().isAir()) {
			NBTItem nbti = new NBTItem(item);
			for (int i = 1; i <= nbti.getInteger("slotsCreated"); i++) {
				String augmentName = nbti.getString("slot" + i + "Augment");
				if (augmentName.equals(set.getName())) {
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
					if (sets.containsKey(augmentName)) {
						PlayerSet pSet = new PlayerSet(this, sets.get(augmentName), 0, p);
						playersets.put(p.getUniqueId(), pSet);
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
