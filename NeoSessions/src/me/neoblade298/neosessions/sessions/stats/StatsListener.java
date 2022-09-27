package me.neoblade298.neosessions.sessions.stats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.sucy.skill.api.event.SkillHealEvent;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.neoblade298.neosessions.sessions.PlayerStatus;
import me.neoblade298.neosessions.sessions.SessionManager;
import me.neoblade298.neosessions.sessions.SessionPlayer;

public class StatsListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void monitorPlayerDamage(EntityDamageByEntityEvent e) {
		// For now, this only matters in instances
		BukkitAPIHelper api = MythicBukkit.inst().getAPIHelper();
		
		// If a player projectile is dealing damage
		if (e.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) e.getDamager();
			if (proj.getShooter() instanceof Player) {
				handlePlayerDealDamage(e, (Player) proj.getShooter());
			}
		}
		// If the player is dealing damage
		else if (e.getDamager() instanceof Player) {
			handlePlayerDealDamage(e, (Player) e.getDamager());
		}
		// If a player-owned armor stand (skill) deals damage
		else if (api.isMythicMob(e.getDamager()) && api.isMythicMob(e.getEntity())) {
			ActiveMob am = api.getMythicMobInstance(e.getDamager());
			if (am.getOwner().isPresent()) {
				Player p = Bukkit.getPlayer(am.getOwner().get());
				if (p == null) {
					return;
				}
				
				handlePlayerDealDamage(e, p);
			}
		}
		// If the player is taking damage
		else if (e.getEntity() instanceof Player) {
			if (e.getEntity() instanceof Player) return; // Ignore pvp
			Player p = (Player) e.getEntity();
			SessionPlayer sp = SessionManager.getPlayer(p);
			if (sp == null || sp.getStatus() != PlayerStatus.PARTICIPATING) {
				e.setCancelled(true);
				return;
			}
			
			for (Stats stats : sp.getSession().getStats()) {
				stats.getStatsPlayer(sp).addDamageTaken(e.getDamage());
			}
		}
	}
	
	private void handlePlayerDealDamage(EntityDamageByEntityEvent e, Player p) {
		if (e.getEntity() instanceof Player) return; // Ignore pvp
		if (e.getFinalDamage() > 9000000) return; // Ignore instakills
		
		SessionPlayer sp = SessionManager.getPlayer(p);
		if (sp == null || sp.getStatus() != PlayerStatus.PARTICIPATING) {
			e.setCancelled(true);
			return;
		}

		for (Stats stats : sp.getSession().getStats()) {
			stats.getStatsPlayer(sp).addDamageTaken(e.getFinalDamage());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onSkillHeal(SkillHealEvent e) {
		if (!(e.getHealer() instanceof Player)) return;

		Player p = (Player) e.getHealer();
		SessionPlayer sp = SessionManager.getPlayer(p);
		if (sp == null || sp.getStatus() != PlayerStatus.PARTICIPATING) {
			e.setCancelled(true);
			return;
		}
		
		if (e.getTarget() == e.getHealer()) {
			for (Stats stats : sp.getSession().getStats()) {
				stats.getStatsPlayer(sp).addSelfHeal(e.getEffectiveHeal());
			}
		}
		else if (e.getTarget() instanceof Player) {
			for (Stats stats : sp.getSession().getStats()) {
				stats.getStatsPlayer(sp).addAllyHeal(e.getEffectiveHeal());
			}
		}
	}
}
