package me.neoblade298.neoanalysis;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;

import me.neoblade298.neoanalysis.commands.CmdAnalysisBosskill;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.commands.CommandManager;
import me.neoblade298.neoquests.NeoQuests;
import me.neoblade298.neoquests.quests.QuestInstance;
import me.neoblade298.neoquests.quests.Quester;
import me.neoblade298.neoquests.quests.QuestsManager;


public class NeoAnalysis extends JavaPlugin implements org.bukkit.event.Listener {
	// SQL
	public ArrayList<String> bosses;
	
	private static NeoAnalysis inst;
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("NeoAnalysis Enabled");
		getServer().getPluginManager().registerEvents(this, this);
		
		// Bosses
		bosses = new ArrayList<String>();
		bosses.add("KilledRatface");
		bosses.add("KilledSpiderQueen");
		bosses.add("KilledAngvoth");
		bosses.add("KilledLucius");
		bosses.add("KilledHamvil");
		bosses.add("KilledEirik");
		bosses.add("KilledBanditKing");
		bosses.add("KilledAberration");
		bosses.add("KilledZachai");
		bosses.add("KilledHollister");
		bosses.add("KilledBlight");
		bosses.add("KilledDhaga");
		bosses.add("KilledFrostQueen");
		bosses.add("KilledRhain");
		bosses.add("KilledMaleficent");
		bosses.add("KilledSalondeon");
		
		initCommands();
		
		inst = this;
	}
	
	private void initCommands() {
		CommandManager mngr = new CommandManager("analysis", this);
		mngr.register(new CmdAnalysisBosskill());
		mngr.registerCommandList("");
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoAnalysis Disabled");
	    super.onDisable();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent e) {
		saveSql(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onKick(PlayerKickEvent e) {
		saveSql(e.getPlayer());
	}
	
	private void saveSql(Player p) {
		UUID uuid = p.getUniqueId();
		Quester quester = QuestsManager.getQuester(p, 1);
		// Find latest quest
		String q = "";
		boolean iteratedAll = true;
		if (quester != null) {
			q += quester.getCompletedQuests().size() + "-";
			for (QuestInstance qi : quester.getActiveQuests()) {
				if (q.length() + qi.getQuest().getKey().length() < 77) {
					q += qi.getQuest().getKey() + ",";
				}
				else {
					iteratedAll = false;
					break;
				}
			}
			if (!iteratedAll) q += "...";
		}
		final String quest = q;
		
		new BukkitRunnable() {
			public void run() {
				
				// Find latest boss
				String boss = "";
				for (String perm : bosses) {
					if (NeoQuests.getPlayerTags(1).exists(perm, uuid)) {
						boss = perm;
					}
					else {
						break;
					}
				}
				
				// Find class and level
				String pClass = "";
				int level = 0;
				PlayerData pd = SkillAPI.getPlayerData(p);
				if (pd != null) {
					PlayerClass pc = pd.getClass("class");
					if (pc != null) {
						pClass = pc.getData().getName();
						level = pc.getLevel();
					}
				}
				long joined = p.getFirstPlayed();
				long now = System.currentTimeMillis();
				
				// Find last town
				Resident r = TownyAPI.getInstance().getResident(p);
				Town town = r.getTownOrNull();
				String t = "";
				boolean isMayor = false;
				if (town != null) {
					t = town.getName();
					isMayor = r.isMayor();
				}
				
				try {
					Statement stmt = NeoCore.getStatement();
					stmt.executeUpdate("REPLACE INTO analysis_players values('" + uuid + "','" + p.getName() + "'," + joined + "," + now + ",'" + pClass + 
							"'," + level + ",'" + quest + "','" + boss + "','" + t + "'," + isMayor + ");");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}.runTaskAsynchronously(this);
	}
	
	public static NeoAnalysis inst() {
		return inst;
	}
	
}
