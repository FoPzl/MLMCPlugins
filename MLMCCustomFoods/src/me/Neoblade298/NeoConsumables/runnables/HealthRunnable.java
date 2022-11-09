package me.Neoblade298.NeoConsumables.runnables;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sucy.skill.api.event.SkillHealEvent;

public class HealthRunnable extends BukkitRunnable {
	Player p;
	double healing;
	int reps;
	
	public HealthRunnable(Player p, double healing, int reps) {
		this.p = p;
		this.healing = healing;
		this.reps = reps;
	}
	
	@Override
	public void run() {
		if (p != null && !p.isDead()) {
			this.reps -= 1;
            SkillHealEvent event = new SkillHealEvent(p, p, this.healing, true);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
    			p.setHealth(Math.min(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), p.getHealth() + event.getAmount()));
            }
			if (this.reps <= 0) {
				this.cancel();
			}
		}
		else {
			this.cancel();
		}
	}
}
