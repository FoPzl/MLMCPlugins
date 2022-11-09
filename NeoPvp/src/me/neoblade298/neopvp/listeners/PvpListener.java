package me.neoblade298.neopvp.listeners;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.protection.regions.RegionQuery.QueryOption;

import me.neoblade298.neocore.util.Util;
import me.neoblade298.neopvp.NeoPvp;
import me.neoblade298.neopvp.PvpAccount;
import me.neoblade298.neopvp.PvpManager;
import me.neoblade298.neopvp.wars.War;
import me.neoblade298.neopvp.wars.WarManager;

public class PvpListener implements Listener {
	private static final long ONE_DAY = 1000 * 60 * 60 * 24;

	@EventHandler
	public void onPvp(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player pv = (Player) e.getEntity();
		
		if (e.getDamager() instanceof Player) {
			handlePvpDamage((Player) e.getDamager(), pv, e);
		}
		else if (e.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) e.getDamager();
			if (proj.getShooter() instanceof Player) {
				handlePvpDamage((Player) proj.getShooter(), pv, e);
			}
		}

		
	}
	
	private void handlePvpDamage(Player pa, Player pv, EntityDamageByEntityEvent e) {
		PvpAccount attacker = PvpManager.getAccount(pa);
		PvpAccount victim = PvpManager.getAccount(pv);

		// Make check for if user is in an allowed protection region
		RegionContainer ca = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery qa = ca.createQuery();
		ApplicableRegionSet seta = qa.getApplicableRegions(BukkitAdapter.adapt(pa.getLocation()), QueryOption.NONE);

		RegionContainer cv = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery qv = cv.createQuery();
		ApplicableRegionSet setv = qv.getApplicableRegions(BukkitAdapter.adapt(pv.getLocation()), QueryOption.NONE);

		// pvp protection
		if (seta.testState(WorldGuardPlugin.inst().wrapPlayer(pa), NeoPvp.PROTECTION_ALLOWED_FLAG)
				|| setv.testState(WorldGuardPlugin.inst().wrapPlayer(pv), NeoPvp.PROTECTION_ALLOWED_FLAG)) {

			if (attacker.isProtected()) {
				Util.msg(pa, "&cYou cannot attack others while pvp protected!");
				e.setCancelled(true);
				return;
			}

			if (victim.isProtected()) {
				Util.msg(pa, "&cThis player is currently pvp protected!");
				e.setCancelled(true);
				return;
			}
		}
		
		// war protection
		for (War war : WarManager.getOngoingWars().values()) {
			if (!war.getWorld().equals(pa.getWorld())) continue;
			
			for (int i = 0; i <= 1; i++) {
				if (war.getTeams()[i].isMember(pa) && war.getTeams()[i].isMember(pv)) {
					Util.msg(pa, "&cYou're on the same war team!");
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		// Ignore event
		String world = e.getEntity().getWorld().getName();
		if (world.equalsIgnoreCase("Event") || world.equalsIgnoreCase("ClassPVP")) return;
		Player victim = e.getEntity();
		Player killer = victim.getKiller();
		
		// War logic takes priority, ignores /pvp
		if (WarManager.handleKill(killer, victim)) return;
		
		if (killer == null) {
			// Inventory protection for 24 hours, this DOES NOT APPLY if it's a pvp death 
			if (victim.getFirstPlayed() + ONE_DAY > System.currentTimeMillis()) {
				if (!world.equalsIgnoreCase("Argyll") && !world.equalsIgnoreCase("ClassPVP")) {
					e.setKeepInventory(true);
					e.getDrops().clear();
					double timeLeft = victim.getFirstPlayed() + ONE_DAY - System.currentTimeMillis();
					double hoursLeft = timeLeft / 1000 / 60 / 60;
					Util.msg(victim, "&7Your inventory was kept because you're a new player. This does not apply to PVP deaths!");
					Util.msg(victim, "&7Time until inventory protection expires: &e" + PvpAccount.df.format(hoursLeft) + "&7.");
				}
			}
		}
		else if (killer == victim) {
			return;
		}
		else {
			// Drop skull
			if (!e.getKeepInventory()) {
				ItemStack head = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				meta.setOwningPlayer(victim);
				meta.setDisplayName("§e" + victim.getName() + "'s Head");
				meta.setLore(Arrays.asList("§7Killer: §c" + victim.getKiller().getName()));
				head.setItemMeta(meta);
				victim.getWorld().dropItem(victim.getLocation(), head);
			}

			// Handle all pvp stats
			PvpManager.handleKill(killer, victim);
		}
	}
}
