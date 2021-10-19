package me.Neoblade298.NeoConsumables;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerAttributeUnloadEvent;

import me.Neoblade298.NeoConsumables.objects.Attributes;
import me.Neoblade298.NeoConsumables.objects.Consumable;
import me.Neoblade298.NeoConsumables.objects.ConsumableType;
import me.Neoblade298.NeoConsumables.objects.SettingsChanger;
import me.Neoblade298.NeoConsumables.runnables.AttributeTask;
import me.neoblade298.neosettings.NeoSettings;
import me.neoblade298.neosettings.objects.Settings;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NeoConsumables extends JavaPlugin implements Listener {
	HashMap<String, Consumable> consumables = new HashMap<String, Consumable>();
	// These runnables take away attributes from players when they're done being used
	public HashMap<UUID, HashMap<Consumable, AttributeTask>> attributes = new HashMap<UUID, HashMap<Consumable, AttributeTask>>();
	public HashMap<UUID, Long> globalCooldowns = new HashMap<UUID, Long>();
	public HashMap<UUID, HashMap<Consumable, Long>> foodCooldowns = new HashMap<UUID, HashMap<Consumable, Long>>();
	public boolean isInstance = false;
	public Settings settings;
	public Settings hiddenSettings;

	public void onEnable() {
		isInstance = new File(getDataFolder(), "instance.yml").exists();
		loadConfigs();
		
		// Setup databases
		for (Player p : Bukkit.getOnlinePlayers()) {
			UUID uuid = p.getUniqueId();
			attributes.put(uuid, new HashMap<Consumable, AttributeTask>());
			foodCooldowns.put(uuid, new HashMap<Consumable, Long>());
		}

	    getCommand("cons").setExecutor(new Commands(this));
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public void loadConfigs() {
		consumables.clear();
		NeoSettings nsettings = (NeoSettings) Bukkit.getPluginManager().getPlugin("NeoSettings");
		settings = nsettings.createSettings("Consumables", this, false);
		settings.addSetting("InventoryUse", false);
		hiddenSettings = nsettings.createSettings("Tokens", this, true);
		hiddenSettings.addSetting("boss", false);
		
		for (File file : new File(getDataFolder(), "consumables").listFiles()) {
			FileConfiguration itemConfig = YamlConfiguration.loadConfiguration(file);
			for (String s : itemConfig.getKeys(false)) {
				String name = itemConfig.getString(s + ".name");
				Consumable cons = new Consumable(this, name);
				
				// Lore
				ArrayList<String> lore = new ArrayList<String>();
				for (String loreLine : itemConfig.getStringList(s + ".lore")) {
					lore.add(loreLine.replaceAll("&", "�"));
				}
				cons.setLore(lore);
				
				// Potion effects
				ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>();
				PotionEffectType type;
				for (String potion : itemConfig.getStringList(s + ".effects")) {
					String[] split = potion.split(",");
					type = PotionEffectType.getByName(split[0]);
					int amp = Integer.parseInt(split[1]);
					int duration = Integer.parseInt(split[2]);
					PotionEffect effect = new PotionEffect(type, duration, amp);
					potions.add(effect);
				}
				cons.setPotions(potions);
				
				// Attributes
				Attributes attribs = new Attributes();
				for (String attribute : itemConfig.getStringList(s + ".attributes")) {
					String[] split = attribute.split(",");
					String attr = split[0];
					int amp = Integer.parseInt(split[1]);
					attribs.addAttribute(attr, amp);
				}
				if (!attribs.isEmpty()) {
					cons.setAttributes(attribs);
					cons.setAttributeTime(itemConfig.getInt(s + ".attributetime"));
				}
				
				ArrayList<Sound> sounds = new ArrayList<Sound>();
				for (String sname : itemConfig.getStringList(s + ".sound-effects")) {
					Sound sound = Sound.valueOf(sname.toUpperCase());
					if (sound != null) {
						sounds.add(sound);
					}
				}
				cons.setSounds(sounds);
				
				ArrayList<SettingsChanger> settingsChangers = new ArrayList<SettingsChanger>();
				for (String settingString : itemConfig.getStringList(s + ".settings")) {
					String[] args = settingString.split(" ");
					String setting = args[0].substring(0, args[0].indexOf('.'));
					String subsetting = args[0].substring(args[0].indexOf('.') + 1);
					Object value = null;
					long expiration = Long.parseLong(args[2]);
					if (args[3].equalsIgnoreCase("string")) {
						value = args[1];
					}
					else if (args[3].equalsIgnoreCase("boolean")) {
						value = Boolean.parseBoolean(args[1]);
					}
					else if (args[3].equalsIgnoreCase("integer")) {
						value = Integer.parseInt(args[1]);
					}
					settingsChangers.add(new SettingsChanger(this.settings, subsetting, value, expiration));
				}
				cons.setSettingsChangers(settingsChangers);

				cons.setSaturation(itemConfig.getDouble(s + ".saturation"));
				cons.setHunger(itemConfig.getInt(s + ".hunger"));
				cons.setHealth(itemConfig.getInt(s + ".health.amount"));
				cons.setHealthDelay(itemConfig.getInt(s + ".health.delay"));
				cons.setHealthTime(itemConfig.getInt(s + ".health.repetitions"));
				cons.setMana(itemConfig.getInt(s + ".mana.amount"));
				cons.setManaDelay(itemConfig.getInt(s + ".mana.delay"));
				cons.setManaTime(itemConfig.getInt(s + ".mana.repetitions"));
				cons.setCooldown(itemConfig.getLong(s + ".cooldown"));
				cons.setCommands(itemConfig.getStringList(s + ".commands"));
				cons.setWorlds(itemConfig.getStringList(s + ".worlds"));
				cons.setIgnoreGcd(itemConfig.getBoolean(s + ".ignore-gcd"));
				cons.setType(ConsumableType.fromString(itemConfig.getString(s + ".type")));
				consumables.put(cons.getName(), cons);
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if ((!e.getAction().equals(Action.RIGHT_CLICK_AIR)) && (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			return;
		}
		if (!e.getHand().equals(EquipmentSlot.HAND)) {
			return;
		}
		if (!SkillAPI.isLoaded(p)) {
			return;
		}
		ItemStack item = p.getInventory().getItemInMainHand();
		if ((!item.hasItemMeta()) || (!item.getItemMeta().hasDisplayName())) {
			return;
		}
		
		ItemMeta meta = item.getItemMeta();
		String name = ChatColor.stripColor(meta.getDisplayName());
		boolean quickEat = false;
		if (meta.hasLore()) {
			for (String line : meta.getLore()) {
				if (line.contains("Quick Eat") && !item.getType().equals(Material.PRISMARINE_CRYSTALS)) {
					quickEat = true;
				}
			}
		}

		// Find the food in the inv
		Consumable consumable = null;
		if (quickEat) {
			ItemStack[] contents = p.getInventory().getContents();
			for (int i = 0; i < 36; i++) {
				ItemStack invItem = contents[i];
				if (invItem != null && invItem.hasItemMeta() && invItem.getItemMeta().hasLore()) {
					ItemMeta invMeta = invItem.getItemMeta();
					String invName = ChatColor.stripColor(invMeta.getDisplayName());
					if (!consumables.containsKey(invName)) {
						continue;
					}
					consumable = consumables.get(invName);
					if (!consumable.isSimilar(invMeta)) {
						continue;
					}
					if (!consumable.canUse(p)) {
						continue;
					}
					if (consumable.getType() != ConsumableType.FOOD) {
						continue;
					}
					break;
				}
			}
		}
		else {
			if (consumables.containsKey(name)) {
				consumable = consumables.get(name);
				if (!consumable.isSimilar(meta)) {
					return;
				}
				if (!consumable.canUse(p)) {
					return;
				}
			}
		}
		
		if (consumable == null) {
			return;
		}
		
		// Use consumable
		consumable.use(p, item);
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!e.isRightClick()) {
			return;
		}
		Player p = (Player) e.getWhoClicked();
		if (!((boolean) settings.getValue(p.getUniqueId(), "InventoryUse"))) {
			return;
		}
		ItemStack item = e.getClickedInventory().getItem(e.getSlot());
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return;
		}
		ItemMeta meta = item.getItemMeta();
		String name = ChatColor.stripColor(meta.getDisplayName());
		
		if (!consumables.containsKey(name)) {
			return;
		}
		Consumable cons = consumables.get(name);
		
		// Only food can be inventory click eaten
		if (cons.getType() != ConsumableType.FOOD) {
			return;
		}
		if (!cons.isSimilar(meta)) {
			return;
		}
		if (!cons.canUse(p)) {
			return;
		}
		
		e.setCancelled(true);
		cons.use(p, item);
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (!foodCooldowns.containsKey(uuid)) {
			foodCooldowns.put(uuid, new HashMap<Consumable, Long>());
		}
		if (!attributes.containsKey(uuid)) {
			attributes.put(uuid, new HashMap<Consumable, AttributeTask>());
		}
		
		if (isInstance) {
			// Remove cooldowns for food if joining a boss instance
			foodCooldowns.get(uuid).clear();
			attributes.get(uuid).clear();
			globalCooldowns.remove(uuid);
		}
	}

	@EventHandler
	public void onPlayerPlace(BlockPlaceEvent e) {
		ItemStack item = e.getItemInHand();
		if (item.getType().equals(Material.CHEST) && item.hasItemMeta() && item.getItemMeta().hasLore()
				&& item.getItemMeta().getLore().get(0).contains("Potential Rewards")) {
			e.setCancelled(true);
		}
	}

	public boolean isOffGcd(Player p) {
		if (this.globalCooldowns.containsKey(p.getUniqueId())) {
			return System.currentTimeMillis() > this.globalCooldowns.get(p.getUniqueId());
		}
		return true;
	}
	
	private void resetAttributes(Player p) {
		HashMap<Consumable, AttributeTask> tasks = attributes.get(p.getUniqueId());
		for (Consumable c : tasks.keySet()) {
			tasks.get(c).getTask().cancel();
		}
		tasks.clear();
	}
	
	@EventHandler
	public void onAttributeUnload(PlayerAttributeUnloadEvent e) {
		resetAttributes(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		resetAttributes(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		resetAttributes(e.getPlayer());
	}
}