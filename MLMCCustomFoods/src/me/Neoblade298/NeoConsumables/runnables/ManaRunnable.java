package me.Neoblade298.NeoConsumables.runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;

public class ManaRunnable extends BukkitRunnable {
	Player p;
	double mana;
	int reps;
	PlayerData data;
	
	public ManaRunnable(Player p, double mana, int reps) {
		this.p = p;
		this.mana = mana;
		this.reps = reps;
		data = SkillAPI.getPlayerData(p);
	}
	
	@Override
	public void run() {
		if (p != null && !p.isDead() && data != null) {
			this.reps -= 1;
			data.giveMana(mana);
			if (this.reps <= 0) {
				this.cancel();
			}
		}
		else {
			this.cancel();
		}
	}
}
