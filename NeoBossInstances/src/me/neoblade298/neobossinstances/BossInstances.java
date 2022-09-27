package me.neoblade298.neobossinstances;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerLoadCompleteEvent;
import com.sucy.skill.api.event.SkillHealEvent;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerData;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.utils.numbers.RandomInt;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import me.neoblade298.neobossinstances.stats.PlayerStat;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.instancing.InstanceType;
import me.neoblade298.neocore.player.PlayerFields;

public class BossInstances extends JavaPlugin implements Listener {

	// Config items
	File file = null;
	FileConfiguration conf = null;
	String returnCommand = null;
	String sendCommand = null;
	int cmdDelay = 0;
	boolean isInstance = false;
	Plugin main = this;
	Location mainSpawn = null;
	Location instanceSpawn = null;
	boolean isDebug = false;
	boolean disableFights = false;

	// SQL
	static String sqlUser = "neoblade298";
	static String sqlPass = "7H56480g09&Z01pz";
	static String connection = "jdbc:mysql://66.70.180.136:3306/MLMC?useSSL=false";

	// Databases
	// Cooldowns: Key is boss name, payload is ConcurrentHashMap where key is
	// playername and
	// payload is last fought
	public ConcurrentHashMap<String, ConcurrentHashMap<UUID, Long>> cooldowns = new ConcurrentHashMap<String, ConcurrentHashMap<UUID, Long>>();
	private static HashMap<String, Boss> bossInfo = new HashMap<String, Boss>();
	public ConcurrentHashMap<String, Long> dropCooldown = new ConcurrentHashMap<String, Long>();
	public ArrayList<String> raidBossesFought = new ArrayList<String>();
	ArrayList<String> instanceNames = null;
	ArrayList<String> activeBosses = new ArrayList<String>();
	public ConcurrentHashMap<String, Integer> bossMultiplier = new ConcurrentHashMap<String, Integer>();
	public ConcurrentHashMap<String, ArrayList<Player>> activeFights = new ConcurrentHashMap<String, ArrayList<Player>>();
	public ConcurrentHashMap<String, ArrayList<Player>> inBoss = new ConcurrentHashMap<String, ArrayList<Player>>();
	public ConcurrentHashMap<String, ArrayList<BukkitRunnable>> bossRunnables = new ConcurrentHashMap<String, ArrayList<BukkitRunnable>>();
	public ConcurrentHashMap<String, Long> bossRunnableTimers = new ConcurrentHashMap<String, Long>();
	public ConcurrentHashMap<UUID, Integer> spectatorAcc = new ConcurrentHashMap<UUID, Integer>();
	public ConcurrentHashMap<String, ArrayList<String>> healthbars = new ConcurrentHashMap<String, ArrayList<String>>();
	public static HashMap<UUID, Boss> spectatingBoss = new HashMap<UUID, Boss>();
	public static ConcurrentHashMap<UUID, String> fightingBoss = new ConcurrentHashMap<UUID, String>();
	public ConcurrentHashMap<String, PlayerStat> playerStats = new ConcurrentHashMap<String, PlayerStat>();
	public ConcurrentHashMap<String, Long> statTimers = new ConcurrentHashMap<String, Long>();
	public HashSet<String> joiningPlayers = new HashSet<String>();
	public HashSet<String> leavingPlayers = new HashSet<String>();
	public PlayerFields settings;
	
	private static BossInstances inst;

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("boss").setExecutor(new Commands(this));
		settings = NeoCore.createPlayerFields("BossMultipliers", this, false);
		isInstance = NeoCore.getInstanceType() == InstanceType.SESSIONS;
		loadConfig();

		// If not an instance, set up player cooldowns
		if (!isInstance) {
			try {
				Connection con = DriverManager.getConnection(BossInstances.connection, BossInstances.sqlUser,
						BossInstances.sqlPass);
				Statement stmt = con.createStatement();
				ResultSet rs;

				rs = stmt.executeQuery("SELECT * FROM neobossinstances_cds");
				while (rs.next()) {
					ConcurrentHashMap<UUID, Long> cds = cooldowns.get(rs.getString(2));
					cds.put(UUID.fromString(rs.getString(1)), rs.getLong(3));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		Bukkit.getServer().getLogger().info("[NeoBossInstances] NeoBossInstances Enabled, loaded in form: " + NeoCore.getInstanceType());
		inst = this;
	}

	public void loadConfig() {
		bossInfo.clear();

		// Save config if doesn't exist
		file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveResource("config.yml", false);
		}
		conf = YamlConfiguration.loadConfiguration(file);

		// Load values from config
		sendCommand = getConfig().getString("Send_Command");
		returnCommand = getConfig().getString("Return_Command");
		cmdDelay = getConfig().getInt("Command_Delay");
		mainSpawn = parseLocation(getConfig().getString("Main_Spawn"));
		instanceSpawn = parseLocation(getConfig().getString("Instance_Spawn"));

		ConfigurationSection bosses = getConfig().getConfigurationSection("Bosses");
		instanceNames = (ArrayList<String>) getConfig().getStringList("Instances");

		// Populate boss and raid information (stats too)
		for (String boss : bosses.getKeys(false)) {
			ConfigurationSection bossSection = bosses.getConfigurationSection(boss);
			int cooldown = bossSection.getInt("Cooldown");
			String cmd = bossSection.getString("Command");
			String displayName = bossSection.getString("Display-Name");
			BossType type = BossType.valueOf(bossSection.getString("Type", "BOSS").toUpperCase());
			int timeLimit = bossSection.getInt("Time-Limit");
			Location loc = parseLocation(bossSection.getString("Coordinates"));
			String placeholder = bossSection.getString("Placeholder");
			ArrayList<String> mythicmobs = (ArrayList<String>) bossSection.getStringList("Mythicmobs");
			settings.initializeField(boss, 1);
			cooldowns.put(boss, new ConcurrentHashMap<UUID, Long>());

			if (type.equals(BossType.BOSS)) {
				bossInfo.put(boss,
						new Boss(boss, loc, cmd, cooldown, displayName, placeholder, mythicmobs));
			}
			else {
				Boss info = new Boss(boss, loc, cmd, cooldown, displayName, type, timeLimit, placeholder,
						mythicmobs);

				// If the raid has extra bosses within it, add them to the boss info
				if (bossSection.contains("Bosses")) {
					ConfigurationSection raidBosses = bossSection.getConfigurationSection("Bosses");
					ArrayList<RaidBoss> raidBossList = new ArrayList<RaidBoss>();
					for (String raidBoss : raidBosses.getKeys(false)) {
						ConfigurationSection raidBossSection = raidBosses.getConfigurationSection(raidBoss);
						String rcmd = raidBossSection.getString("Command");
						Location rloc = parseLocation(raidBossSection.getString("Coordinates"));
						raidBossList.add(new RaidBoss(rloc, rcmd, raidBoss));
					}
					info.setRaidBosses(raidBossList);
				}
				if (bossSection.contains("Spawners")) {
					ConfigurationSection spawnerSec = bossSection.getConfigurationSection("Spawners");
					ArrayList<SpawnerSet> spawners = new ArrayList<SpawnerSet>();
					for (String key : spawnerSec.getKeys(false)) {
						spawners.add(new SpawnerSet(key, spawnerSec.getInt(key)));
					}
					info.setSpawners(spawners);
				}
				bossInfo.put(boss, info);
			}
		}
	}

	public void onDisable() {
		// If not instance, save cooldowns from ConcurrentHashMap to SQL
		// Only save cooldowns that still matter (still on cooldown)
		if (!isInstance) {
			try {
				Connection con = DriverManager.getConnection(BossInstances.connection, BossInstances.sqlUser,
						BossInstances.sqlPass);
				Statement stmt = con.createStatement();

				// First clear all the cooldowns on the SQL currently
				stmt.executeUpdate("delete from neobossinstances_cds;");

				// Then add the cooldowns from the ConcurrentHashMap into SQL
				for (String boss : cooldowns.keySet()) {
					int cooldown = bossInfo.get(boss).getCooldown();
					ConcurrentHashMap<UUID, Long> lastFought = cooldowns.get(boss);
					for (UUID uuid : lastFought.keySet()) {
						// Only add to the cooldown list if it's still relevant
						if ((System.currentTimeMillis() - lastFought.get(uuid)) < (cooldown * 1000)) {
							stmt.executeUpdate("INSERT INTO neobossinstances_cds VALUES ('" + uuid + "','" + boss + "',"
									+ lastFought.get(uuid) + ")");
						}
					}
				}

				int deleted = stmt.executeUpdate("delete from neobossinstances_fights;");
				Bukkit.getServer().getLogger()
						.info("[NeoBossInstances] Cleared " + deleted + " fights from NeoBossInstances");
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Bukkit.getServer().getLogger().info("[NeoBossInstances] NeoBossInstances Disabled");
	}

	public Location parseLocation(String toParse) {
		String[] sloc = toParse.split(" ");
		double x = Double.parseDouble(sloc[0]);
		double y = Double.parseDouble(sloc[1]);
		double z = Double.parseDouble(sloc[2]);
		float pitch = Float.parseFloat(sloc[3]);
		float yaw = Float.parseFloat(sloc[4]);
		World world = Bukkit.getServer().getWorld("Argyll");
		return new Location(world, x, y, z, yaw, pitch);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		// If instance, check where to send a player
		if (isInstance) {
			Player p = e.getPlayer();
			String uuid = p.getUniqueId().toString();
			joiningPlayers.add(p.getName());

			if (p.isDead()) {
				p.spigot().respawn();
			}
			for (PotionEffect pe : p.getActivePotionEffects()) {
				p.removePotionEffect(pe.getType());
			}

			// Connect
			try {
				Connection con = DriverManager.getConnection(BossInstances.connection, BossInstances.sqlUser,
						BossInstances.sqlPass);
				Statement stmt = con.createStatement();
				ResultSet rs;

				// Check where the player should be, teleport them there
				rs = stmt.executeQuery("SELECT *, COUNT(*) FROM neobossinstances_fights WHERE uuid = '" + uuid + "';");
				rs.next();
				String boss = rs.getString(2);
				int multiplier = rs.getInt(4);
				if (boss == null) {
					p.teleport(instanceSpawn);
					return;
				}

				Boss b = bossInfo.get(boss);
				p.teleport(b.getCoords());
				con.close();

				// Set up databases
				Bukkit.getServer().getLogger()
						.info("[NeoBossInstances] " + p.getName() + " sent to boss " + boss + ".");
				bossMultiplier.put(boss, multiplier);
				if (!activeFights.containsKey(boss)) {
					ArrayList<Player> activeFightsPlayers = new ArrayList<Player>();
					ArrayList<Player> inBossPlayers = new ArrayList<Player>();
					activeFightsPlayers.add(p);
					inBossPlayers.add(p);
					activeFights.put(boss, activeFightsPlayers);
					inBoss.put(boss, inBossPlayers);
				}
				else {
					activeFights.get(boss).add(p);
					inBoss.get(boss).add(p);
				}
				fightingBoss.put(p.getUniqueId(), boss);

				// Recalculate everyone's health bars every time someone joins
				for (Player partyMember : activeFights.get(boss)) {
					ArrayList<String> healthList = new ArrayList<String>();
					healthbars.put(partyMember.getName(), healthList);
					for (Player bossFighter : activeFights.get(boss)) {
						if (!bossFighter.equals(partyMember)) {
							healthList.add(bossFighter.getName());
						}
					}
					Collections.sort(healthList);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				joiningPlayers.remove(p.getName());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMythicmobKill(MythicMobDeathEvent e) {
		try {
			if (!isInstance) return;
			if (e.getKiller() == null || !(e.getKiller() instanceof Player)) return;
			
			Player p = (Player) e.getKiller();
			Boss b = bossInfo.get(fightingBoss.get(p.getUniqueId()));
			if (b.getBossType().equals(BossType.DUNGEON)) {
				if (e.getMob().getSpawner() != null) {
					b.checkSpawnerKill(e.getMob().getSpawner());
					if (b.getSpawnersAlive().size() == 0) {
						finishDungeon(b);
					}
				}
				
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void finishDungeon(Boss b) {
		ArrayList<Player> fighters = inBoss.get(b.getName());
		for (Player fighter : fighters) {
			fighter.sendMessage("§4[§c§lMLMC§4] §7The dungeon has been cleared!");
		}
		
		new BukkitRunnable() {
			public void run() {
				Commands.showStats(b.getName(), b.getDisplayName());
			}
		}.runTaskLater(this, 60L);
		
		new BukkitRunnable() {
			public void run() {
				for (Player fighter : fighters) {
					Commands.returnPlayer(fighter);
				}
			}
		}.runTaskLater(this, 120L);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLoad(PlayerLoadCompleteEvent e) {
		Player p = e.getPlayer();


		// If last one to load, summon the boss, need to add to activebosses
		if (!fightingBoss.containsKey(p.getUniqueId())) return;
		String boss = fightingBoss.get(p.getUniqueId());

		Boss b = bossInfo.get(boss);
		for (Player fighter : activeFights.get(boss)) {
			if (!SkillAPI.isLoaded(fighter)) {
				return;
			}
		}

		// Double check that they're teleported
		double px = p.getLocation().getX();
		double pz = p.getLocation().getZ();
		if (px >= 1555 && px <= 1570 && pz >= -725 && pz <= -709) {
			p.teleport(b.getCoords());
		}

		new BukkitRunnable() {
			public void run() {
				for (Player fighter : activeFights.get(boss)) {
					// Second heal to be safe
					fighter.setHealth(fighter.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
					PlayerData pd = SkillAPI.getPlayerData(fighter);
					if (pd.getClass("class") != null && pd.getClass("class").getData().getManaName().contains("MP")) {
						pd.setMana(pd.getMaxMana());
					}
				}
			}
		}.runTaskLater(this, 40L);

		// Wait 3 seconds so everyone can reorient themselves
		BukkitRunnable spawnBoss = new BukkitRunnable() {
			public void run() {
				
				if (!activeBosses.contains(boss)) {
					activeBosses.add(boss);
					if (b.getCmd() != null) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
								b.getCmd().replaceAll("<multiplier>", "" + bossMultiplier.get(boss)));
					}
					if (b.getBossType().equals(BossType.BOSS)) {
						Bukkit.getServer().getLogger()
								.info("[NeoBossInstances] " + p.getName() + " spawned boss " + boss + ".");
						for (Player target : activeFights.get(boss)) {
							target.sendMessage("§4[§c§lMLMC§4] §7The boss has been spawned!");
						}
					}
					else if (b.getBossType().equals(BossType.RAID)) {
						Bukkit.getServer().getLogger()
								.info("[NeoBossInstances] " + p.getName() + " started raid " + boss + ".");
						scheduleTimer(b.getTimeLimit(), boss);
						// Reset raid bosses fought
						for (RaidBoss raidBoss : b.getRaidBosses()) {
							raidBossesFought.remove(raidBoss.getName());
						}
					}
					else if (b.getBossType().equals(BossType.DUNGEON)) {
						Bukkit.getServer().getLogger()
								.info("[NeoBossInstances] " + p.getName() + " started dungeon " + boss + ".");
						scheduleTimer(b.getTimeLimit(), boss);
						int total = 0;
						
						// Start stats
						statTimers.put(boss, System.currentTimeMillis());
						for (Player p : activeFights.get(boss)) {
							playerStats.put(p.getName(), new PlayerStat(boss));
						}
						
						// Reset spawners
						HashSet<String> spawnersAlive = new HashSet<String>();
						for (SpawnerSet spawner : b.getSpawners()) {
							for (int i = 1; i <= spawner.getMax(); i++) {
								MythicSpawner ms = MythicBukkit.inst().getSpawnerManager()
										.getSpawnerByName(spawner.getInternal() + i);
								if (ms != null) {
									total++;
									ms.setMobLevel(new RandomInt(bossMultiplier.get(boss).toString()));
									ms.setRemainingCooldownSeconds(0L);
									spawnersAlive.add(ms.getInternalName());
								}
								else {
									Bukkit.getLogger().log(Level.WARNING,
											"[NeoBossInstances] Failed to load spawner " + spawner.getInternal() + i);
								}
							}
						}
						b.setTotalSpawners(total);
						b.setSpawnersAlive(spawnersAlive);
					}
				}
			}
		};
		spawnBoss.runTaskLater(this, 60L);
	}

	public void scheduleTimer(int time, String boss) {
		int ticks = time * 20;

		// Set up raid timer for lordboard
		bossRunnableTimers.put(boss, System.currentTimeMillis() + (time * 1000));

		// 30 minute warning
		if (time > 1800) {
			scheduleWarning(ticks, 36000, "§e30 §cminutes", boss);
		}
		// 15 minute warning
		if (time > 900) {
			scheduleWarning(ticks, 18000, "§e15 §cminutes", boss);
		}
		scheduleWarning(ticks, 6000, "§e5 §cminutes", boss);
		scheduleWarning(ticks, 3600, "§e3 §cminutes", boss);
		scheduleWarning(ticks, 2400, "§e2 §cminutes", boss);
		scheduleWarning(ticks, 1200, "§e1 §cminute", boss);

		BukkitRunnable kickPlayer = new BukkitRunnable() {
			public void run() {
				if (activeFights.containsKey(boss)) {
					for (Player p : activeFights.get(boss)) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
								returnCommand.replaceAll("%player%", p.getName()));
					}
				}
			}
		};
		kickPlayer.runTaskLater(main, ticks);
		if (bossRunnables.containsKey(boss)) {
			bossRunnables.get(boss).add(kickPlayer);
		}
		else {
			ArrayList<BukkitRunnable> list = new ArrayList<BukkitRunnable>();
			list.add(kickPlayer);
			bossRunnables.put(boss, list);
		}
	}

	public void scheduleWarning(int ticks, int timeToWarn, String time, String boss) {
		BukkitRunnable warnPlayer = new BukkitRunnable() {
			public void run() {
				if (activeFights.containsKey(boss)) {
					for (Player p : activeFights.get(boss)) {
						p.sendMessage("§4[§c§lMLMC§4] " + time + " remaining!");
					}
				}
			}
		};
		warnPlayer.runTaskLater(main, ticks - timeToWarn);
		if (bossRunnables.containsKey(boss)) {
			bossRunnables.get(boss).add(warnPlayer);
		}
		else {
			ArrayList<BukkitRunnable> list = new ArrayList<BukkitRunnable>();
			list.add(warnPlayer);
			bossRunnables.put(boss, list);
		}
	}

	public String findInstance(String boss) {
		try {
			Connection con = DriverManager.getConnection(BossInstances.connection, BossInstances.sqlUser,
					BossInstances.sqlPass);
			Statement stmt = con.createStatement();
			ResultSet rs;
			ArrayList<String> instanceNamesCopy = new ArrayList<String>(instanceNames);
			Collections.shuffle(instanceNamesCopy);
			for (String instance : instanceNamesCopy) {
				rs = stmt.executeQuery("SELECT * FROM neobossinstances_fights WHERE boss = '" + boss
						+ "' AND instance = '" + instance + "';");
				if (!rs.next()) {
					return instance;
				}
			}
			return "Not found";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Failed to connect";
	}

	public void handleLeave(Player p) {
		// Remove player from all fights locally
		handleLeaveFight(p);
		// Remove spectator mode
		handleLeaveSpectator(p);
	}

	public void handleLeaveSpectator(Player p) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "vanish " + p.getName() + " off");
		p.setInvulnerable(false);
		p.setGameMode(GameMode.SURVIVAL);
		if (spectatorAcc.containsKey(p.getUniqueId())) { // This happens only if they died
			SkillAPI.getPlayerAccountData(p).setAccount(spectatorAcc.remove(p.getUniqueId()));
		}
		if (spectatingBoss.containsKey(p.getUniqueId())) { // This happens regardless of if they died or not
			String boss = spectatingBoss.remove(p.getUniqueId()).getName();
			if (boss != null) {
				inBoss.get(boss).remove(p);
			}
		}
	}

	// For when a player dies and goes into spectate mode
	public void handleLeaveFight(Player p) {
		if (p != null) {
			UUID uuid = p.getUniqueId();
			leavingPlayers.add(p.getName());
			healthbars.remove(p.getName());
			if (!fightingBoss.containsKey(uuid)) return;
			String boss = fightingBoss.remove(uuid);
			statTimers.remove(boss);
			if (activeFights.get(boss).contains(p)) {
				spectatingBoss.put(uuid, bossInfo.get(boss));
				activeFights.get(boss).remove(p);
				Bukkit.getServer().getLogger()
						.info("[NeoBossInstances] " + p.getName() + " removed from boss " + boss + ".");
				for (Player player : activeFights.get(boss)) {
					healthbars.get(player.getName()).remove(p.getName());
				}
			}
			if (activeFights.get(boss).size() == 0) {
				Bukkit.getServer().getLogger().info(
						"[NeoBossInstances] " + p.getName() + " removed from boss " + boss + ", removed from list.");
				activeFights.remove(boss);
				activeBosses.remove(boss);
				bossMultiplier.remove(boss);
				bossRunnableTimers.remove(boss);
				if (bossRunnables.containsKey(boss)) {
					for (BukkitRunnable runnable : bossRunnables.get(boss)) {
						if (!runnable.isCancelled()) {
							runnable.cancel();
						}
					}
					bossRunnables.remove(boss);
				}

				// Send every spectator back, then remove from inBoss
				p.spigot().respawn();
				p.teleport(instanceSpawn);
				BukkitRunnable sendAllBack = new BukkitRunnable() {
					public void run() {
						Iterator<Player> fighters = inBoss.get(boss).iterator();
						while (fighters.hasNext()) {
							Player fighter = fighters.next();
							if (fighter.isOnline()) {
								returnToMain(fighter);
							}
						}
					}
				};
				sendAllBack.runTaskLater(main, 20L);
			}
			// Delete player from all fights in sql
			try {
				Connection con = DriverManager.getConnection(BossInstances.connection, BossInstances.sqlUser,
						BossInstances.sqlPass);
				Statement stmt = con.createStatement();
				stmt.executeUpdate("delete from neobossinstances_fights WHERE uuid = '" + uuid + "';");

				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			leavingPlayers.remove(p.getName());
		}
	}

	public String getCooldown(String name, Player p) {
		if (cooldowns.containsKey(name)) {
			int cooldown = bossInfo.get(name).getCooldown() * 1000;
			if (cooldowns.get(name).containsKey(p.getUniqueId())) {
				long lastUse = cooldowns.get(name).get(p.getUniqueId());
				long currTime = System.currentTimeMillis();
				if (currTime < lastUse + cooldown) {
					int time = (int) (((lastUse + cooldown) - currTime) / 1000);
					int minutes = time / 60;
					int seconds = time % 60;
					if (time > 0) return String.format("§c%d:%02d", minutes, seconds);
				}
			}
		}
		return "§c0";
	}

	public String getBossName(String boss, Player p) {
		Boss b = bossInfo.get(boss);
		int acct = SkillAPI.getPlayerAccountData(p).getActiveId();
		if (NeoCore.getPlayerTags("questaccount_" + acct).exists(b.getTag(), p.getUniqueId())) {
			return b.getPlaceholder();
		}
		return "§4???";
	}

	public String getBossName(String boss) {
		Boss b = bossInfo.get(boss);
		return b.getPlaceholder();
	}

	public int getBossCooldown(String boss, Player p) {
		if (cooldowns.containsKey(boss)) {
			int cooldown = bossInfo.get(boss).getCooldown() * 1000;
			if (cooldowns.get(boss).containsKey(p.getUniqueId())) {
				long lastUse = cooldowns.get(boss).get(p.getUniqueId());
				long currTime = System.currentTimeMillis();
				return (int) ((lastUse + cooldown - currTime) / 1000);
			}
			else {
				return 0;
			}
		}
		return -1;
	}

	public ConcurrentHashMap<String, ArrayList<Player>> getActiveFights() {
		return activeFights;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (isInstance) {
			Player p = e.getEntity();
			handleLeaveFight(p);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if (isInstance) {
			BukkitRunnable respawn = new BukkitRunnable() {
				public void run() {
					Player p = e.getPlayer();
					if (joiningPlayers.contains(p.getName())) return; // If a player logged in already dead
					if (leavingPlayers.contains(p.getName())) return; // Don't let a newly respawned player spectate an
																		// empty boss

					// If everyone in the fight is dead, return everyone
					if (spectatingBoss.containsKey(p.getUniqueId())) {
						p.teleport(spectatingBoss.get(p.getUniqueId()).getCoords()); // Tp after death to boss
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "vanish " + p.getName() + " on");
						p.setGameMode(GameMode.ADVENTURE);
						p.setInvulnerable(true);
						PlayerAccounts accs = SkillAPI.getPlayerAccountData(p);
						spectatorAcc.put(p.getUniqueId(), accs.getActiveId());
						SkillAPI.getPlayerAccountData(p).setAccount(13);
						p.sendMessage(
								"§4[§c§lMLMC§4] §7You died! You can now spectate, or leave with §c/boss return§7.");
					}
					else {
						p.teleport(instanceSpawn);
					}
				}
			};
			respawn.runTaskLater(this, 20L);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (isInstance) {
			Player p = e.getPlayer();
			if (SkillAPI.getPlayerData(p).getSkillBar().isEnabled()) {
				SkillAPI.getPlayerData(p).getSkillBar().toggleEnabled();
			}

			SkillAPI.saveSingle(p);
			handleLeave(p);
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		if (isInstance) {
			Player p = e.getPlayer();
			if (SkillAPI.getPlayerData(p).getSkillBar().isEnabled()) {
				SkillAPI.getPlayerData(p).getSkillBar().toggleEnabled();
			}

			SkillAPI.saveSingle(p);
			handleLeave(p);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (isInstance) {
			String p = e.getPlayer().getName();
			if (spectatorAcc.containsKey(e.getPlayer().getUniqueId())) {
				e.setCancelled(true);
				e.getPlayer().sendMessage("§cCan't drop items when you're dead!");
				return;
			}
			else if (!dropCooldown.containsKey(p) || dropCooldown.get(p) + 2000 < System.currentTimeMillis()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage("§cYou tried to drop something! Drop it again within 2 seconds to confirm!");
			}
			dropCooldown.put(p, System.currentTimeMillis());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void multiplyMobDamage(EntityDamageByEntityEvent e) {
		// For now, this only matters in instances
		if (!isInstance) {
			return;
		}
		BukkitAPIHelper api = MythicBukkit.inst().getAPIHelper();

		// If a mob is damaging a player
		if (api.isMythicMob(e.getDamager()) && e.getEntity() instanceof Player) {
			// Anything above 50k is probably some sort of instakill, don't multiply it
			if (e.getDamage() < 50000) {
				ActiveMob am = api.getMythicMobInstance(e.getDamager());
				if (am.getLevel() <= 6 && am.getLevel() >= 1) {
					e.setDamage(e.getDamage() * (1 + (0.1 * (am.getLevel() - 1))));
				}
				else if (am.getLevel() > 6) {
					e.setDamage(e.getDamage() * (1 + (0.3 * (am.getLevel() - 1))));
				}
				else if (am.getLevel() >= -99) {
					e.setDamage(e.getDamage() * (1 + (0.01 * (am.getLevel()))));
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void monitorPlayerDamage(EntityDamageByEntityEvent e) {
		// For now, this only matters in instances
		if (!isInstance) {
			return;
		}
		BukkitAPIHelper api = MythicBukkit.inst().getAPIHelper();

		// If the player is dealing damage
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (spectatorAcc.containsKey(p.getUniqueId())) {
				e.setCancelled(true);
				return;
			}

			// Make sure their stats are currently being counted
			if (!playerStats.containsKey(p.getName())) {
				return;
			}

			// Make sure self-damage isn't counted in stats
			if (!(e.getEntity() instanceof Player)) {
				if (!fightingBoss.containsKey(p.getUniqueId())) {
					return;
				}
				if (e.getFinalDamage() > 9000000) { // Ignore instakills
					return;
				}
				PlayerStat stats = playerStats.get(p.getName());
				stats.addDamageDealt(e.getFinalDamage());
			}
		}

		// If the player is taking damage
		else if (e.getEntity() instanceof Player) {
			// Make sure self-damage isn't counted
			if (!(e.getDamager() instanceof Player)) {
				Player p = (Player) e.getEntity();

				if (!fightingBoss.containsKey(p.getUniqueId())) {
					return;
				}
				if (!playerStats.containsKey(p.getName())) {
					return;
				}
				PlayerStat stats = playerStats.get(p.getName());
				stats.addDamageTaken(e.getFinalDamage());
			}
		}
		else if (api.isMythicMob(e.getDamager()) && api.isMythicMob(e.getEntity())) {
			ActiveMob am = api.getMythicMobInstance(e.getDamager());
			if (am.getOwner().isPresent()) {
				Player p = Bukkit.getPlayer(am.getOwner().get());
				if (p == null) {
					return;
				}

				if (spectatorAcc.containsKey(p.getUniqueId())) {
					e.setCancelled(true);
					return;
				}

				if (!fightingBoss.containsKey(p.getUniqueId())) {
					return;
				}

				if (!playerStats.containsKey(p.getName())) {
					return;
				}
				PlayerStat stats = playerStats.get(p.getName());
				stats.addDamageDealt(e.getFinalDamage());
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onSkillHeal(SkillHealEvent e) {
		if (!isInstance) {
			return;
		}

		String pName = e.getHealer().getName();
		if (playerStats.containsKey(pName)) {
			PlayerStat stats = playerStats.get(pName);
			// Self healing
			if (pName.equalsIgnoreCase(e.getTarget().getName())) {
				stats.addSelfHeal(e.getEffectiveHeal());
			}
			// Ally healing
			else {
				stats.addAllyHeal(e.getEffectiveHeal());
			}
		}
	}

	public ArrayList<String> getHealthBars(Player p) {
		return healthbars.get(p.getName());
	}

	public void returnToMain(Player p) {
		if (spectatingBoss.containsKey(p.getUniqueId())) {
			p.teleport(instanceSpawn);
			for (PotionEffect pe : p.getActivePotionEffects()) {
				p.removePotionEffect(pe.getType());
			}
			handleLeaveSpectator(p);
		}

		SkillAPI.saveSingle(p);
		p.sendMessage("§4[§c§lBosses§4] §7Sending you back in 3 seconds...");
		BukkitRunnable sendBack = new BukkitRunnable() {
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), returnCommand.replaceAll("%player%", p.getName()));
			}
		};
		sendBack.runTaskLater(main, 60L);
	}

	public long getBossTimer(Player p) {
		if (fightingBoss.containsKey(p.getUniqueId())) {
			return statTimers.getOrDefault(fightingBoss.get(p.getUniqueId()), -1L);
		}
		return -1;
	}

	public long getRaidTimer(Player p) {
		if (fightingBoss.containsKey(p.getUniqueId())) {
			return bossRunnableTimers.getOrDefault(fightingBoss.get(p.getUniqueId()), -1L);
		}
		return -1;
	}

	public static Boss getBoss(Player p) {
		String boss = fightingBoss.get(p.getUniqueId());
		if (boss != null) {
			return bossInfo.get(fightingBoss.get(p.getUniqueId()));
		}
		return null;
	}
	
	public static Boss getBoss(String boss) {
		return bossInfo.get(boss);
	}

	public static Set<UUID> getSpectators() {
		return spectatingBoss.keySet();
	}
	
	public static BossInstances inst() {
		return inst;
	}
}
