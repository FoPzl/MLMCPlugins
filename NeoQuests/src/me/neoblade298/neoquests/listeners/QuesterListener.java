package me.neoblade298.neoquests.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerAccountChangeEvent;

import me.neoblade298.neocore.bar.BarAPI;
import me.neoblade298.neocore.bar.CoreBar;
import me.neoblade298.neoquests.quests.Quester;
import me.neoblade298.neoquests.quests.QuestsManager;

public class QuesterListener implements Listener {
	@EventHandler
	public void onAccountChange(PlayerAccountChangeEvent e) {
		Player p = e.getNewAccount().getPlayer();
		QuestsManager.getQuester(p, e.getPreviousId()).setLocation(p.getLocation());
		Quester newAcc = QuestsManager.initializeOrGetQuester(p, e.getNewID());
		Quester oldAcc = QuestsManager.getQuester(p, e.getPreviousId());
		
		oldAcc.setLocation(p.getLocation());
		oldAcc.stopListening();
		CoreBar cb = BarAPI.getBar(p);
		if (cb.getTopic().startsWith("Q-")) cb.setVisible(false);
		if (newAcc.getLocation() != null) {
			if (!p.teleport(newAcc.getLocation())) {
				Bukkit.getLogger().warning("[NeoQuests] Failed to teleport player on class acccount change!");
			}
		}
		newAcc.startListening();
	}
	
	@EventHandler
	public void onWorldChange(PlayerTeleportEvent e) {
		if (SkillAPI.getSettings().isWorldEnabled(e.getFrom().getWorld()) && !SkillAPI.getSettings().isWorldEnabled(e.getTo().getWorld())) {
			Player p = e.getPlayer();
			QuestsManager.initializeOrGetQuester(p, SkillAPI.getPlayerAccountData(p).getActiveId()).setLocation(e.getFrom());
		}
	}
}
