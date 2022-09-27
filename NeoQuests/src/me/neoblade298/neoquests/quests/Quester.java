package me.neoblade298.neoquests.quests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.util.Util;
import me.neoblade298.neoquests.NeoQuests;
import me.neoblade298.neoquests.commands.CmdQuestsRecommended;
import me.neoblade298.neoquests.conditions.Condition;
import me.neoblade298.neoquests.conditions.ConditionManager;
import me.neoblade298.neoquests.conversations.ConversationManager;
import me.neoblade298.neoquests.listeners.ObjectiveListener;
import me.neoblade298.neoquests.objectives.ObjectiveSetInstance;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Quester {
	private Player p;
	private int acct;
	private HashMap<String, CompletedQuest> completedQuests = new HashMap<String, CompletedQuest>();
	private HashMap<String, QuestInstance> activeQuests = new HashMap<String, QuestInstance>();
	private TreeMap<String, Questline> activeQuestlines = new TreeMap<String, Questline>();
	private Location loc;
	
	public Quester(Player p, int acct) {
		this.p = p;
		this.acct = acct;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public void completeQuest(QuestInstance qi, int stage, boolean success) {
		cleanupQuest(qi.getQuest().getKey());
		completedQuests.put(qi.getQuest().getKey().toUpperCase(), new CompletedQuest(qi.getQuest(), stage, success));
		Questline ql = qi.getQuest().getQuestline();
		p.sendTitle("§fQuest Completed", "§6" + qi.getQuest().getDisplay(), 10, 70, 10);
		p.sendMessage("§4[§c§lMLMC§4] §7You completed quest: §6" + qi.getQuest().getDisplay() + "§7!");
		if (ql != null) {
			if (ql.getLastQuest().equals(qi.getQuest().getKey())) {
				activeQuestlines.remove(ql.getKey().toUpperCase());
			}
			// Only continue the questline if the player still has it
			else if (activeQuestlines.containsKey(ql.getKey().toUpperCase())) {
				new BukkitRunnable() {
					public void run() {
						ConversationManager.startConversation(p, ql.getNextQuest(p).getStartConversation(), false);
					}
				}.runTaskLater(NeoQuests.inst(), 60L);
			}
		}
	}
	
	public void cancelQuest(String key) {
		if (activeQuests.containsKey(key.toUpperCase())) {
			QuestInstance qi = cleanupQuest(key.toUpperCase());
			
			// If cancelling the first quest in a questline, that questline is no longer active
			if (qi.getQuest().getQuestline() != null) {
				Questline ql = qi.getQuest().getQuestline();
				if (ql.getFirstQuest().getKey().equals(qi.getQuest().getKey())) {
					activeQuestlines.remove(ql.getKey());
				}
			}

			p.sendMessage("§4[§c§lMLMC§4] §7You cancelled quest: §6" + qi.getQuest().getDisplay() + "§7!");
			p.sendTitle("§fQuest Cancelled", "§6" + qi.getQuest().getDisplay(), 10, 70, 10);
		}
	}
	
	private QuestInstance cleanupQuest(String key) {
		QuestInstance qi = activeQuests.remove(key.toUpperCase());
		qi.stopListening();
		return qi;
	}
	
	public void startQuest(Quest q) {
		startQuest(q, false);
	}
	
	public void startQuest(Quest q, boolean ignoreConditions) {
		if (!ignoreConditions) {
			Condition c = ConditionManager.getBlockingCondition(p, q.getConditions());
			if (c != null) {
				p.sendMessage("§4[§c§lMLMC§4] §cCould not start quest: §6" + q.getDisplay() + "§c: " + c.getExplanation(p));
				return;
			}
		}
		p.sendTitle("§fQuest Started", "§6" + q.getDisplay(), 10, 70, 10);
		p.sendMessage("§4[§c§lMLMC§4] §7You started quest: §6" + q.getDisplay() + "§7! Type §c/quest§7!");
		QuestInstance qi = new QuestInstance(this, q);
		activeQuests.put(q.getKey().toUpperCase(), qi);
		if (q.getQuestline() != null) addQuestline(q.getQuestline());
		qi.setupInstances(true);
		new BukkitRunnable() {
			public void run() {
				if (qi.getStage() == 0) {
					qi.displayObjectives(p);
				}
			}
		}.runTaskLater(NeoQuests.inst(), 40L);
	}
	
	public void displayQuests(CommandSender s) {
		if (activeQuests.size() > 0) {
			for (QuestInstance qi : activeQuests.values()) {
				Util.msg(s, "&7&m= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =", false);
				ComponentBuilder builder = new ComponentBuilder("§6§l" + qi.getQuest().getDisplay()) ;
				ComponentBuilder quitquest = new ComponentBuilder(" §7§o[Click to Quit]")
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quests quit " + qi.getQuest().getKey()));
				s.spigot().sendMessage(builder.append(quitquest.create()).create());
				qi.displayObjectives(s);
			}
			Util.msg(s, "&7&m= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =", false);
			ComponentBuilder builder = new ComponentBuilder("§7§o[Other Available Quests]")
					.event(new HoverEvent(Action.SHOW_TEXT, new Text("/quests guide")))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quests guide"));
			s.spigot().sendMessage(builder.create());
		}
		else {
			displayGuide(s);
		}
	}
	
	public void displayGuide(CommandSender s) {
		if (activeQuestlines.size() > 0) {
			ArrayList<String> nullQuestlines = new ArrayList<String>();
			boolean first = true;
			for (Entry<String, Questline> e : activeQuestlines.entrySet()) {
				Questline ql = e.getValue();
				Quest next = ql.getNextQuest(p);
				if (next == null) {
					Bukkit.getLogger().warning("[NeoQuests] Player questline " + ql.getDisplay() + " returned null for next, this should never happen "
							+ "as questlines are removed once the last quest is completed. Removing from questlines.");
					nullQuestlines.add(e.getKey());
					continue;
				}
				Util.msg(s, "&7&m= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =", false);
				if (first) {
					Util.msg(s, "&6&lActive Questlines:");
				}
				ComponentBuilder builder = new ComponentBuilder("§7- §6" + ql.getDisplay() + " §7(§e" + next.getDisplay() + "§7) ");
				ComponentBuilder takequest = new ComponentBuilder("§7§o[Click to Take]")
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quests take " + next.getKey()))
						.event(new HoverEvent(Action.SHOW_TEXT, new Text("/quests take")));
				s.spigot().sendMessage(builder.append(takequest.create()).create());
			}
			
			// Only add line if there's actually a nonnull questline
			if (nullQuestlines.size() != activeQuestlines.size()) {
				Util.msg(s, "&7&m= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =", false);
			}
			for (String key : nullQuestlines) {
				activeQuestlines.remove(key);
			}
			ComponentBuilder rec = new ComponentBuilder("§7§o[Recommended Quests] ")
					.event(new HoverEvent(Action.SHOW_TEXT, new Text("/quests recommended")))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quests recommended " + this.p.getName()))
					.append("§7§o [Challenge Quests]", FormatRetention.NONE)
					.event(new HoverEvent(Action.SHOW_TEXT, new Text("/quests challenges")))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quests challenges " + this.p.getName()));
			s.spigot().sendMessage(rec.create());
		}
		else {
			CmdQuestsRecommended.run(s, new String[] { s.getName() }, false);
			ComponentBuilder rec = new ComponentBuilder("§7§o[Challenge Quests]")
					.event(new HoverEvent(Action.SHOW_TEXT, new Text("/quests challenges")))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quests challenges " + this.p.getName()));
			s.spigot().sendMessage(rec.create());
		}
	}
	
	// Call when switching to another quest account
	public void stopListening() {
		ObjectiveListener.stopListening(p);
	}
	
	public void startListening() {
		for (QuestInstance qi : activeQuests.values()) {
			qi.startListening();
		}
	}
	
	public Collection<QuestInstance> getActiveQuests() {
		return activeQuests.values();
	}
	
	public void addActiveQuest(QuestInstance qi) {
		activeQuests.put(qi.getQuest().getKey().toUpperCase(), qi);
	}
	
	public CompletedQuest getCompletedQuest(String quest) {
		return completedQuests.get(quest.toUpperCase());
	}
	
	public Collection<CompletedQuest> getCompletedQuests() {
		return completedQuests.values();
	}
	
	public void addQuestline(String qlname) {
		Questline ql = QuestsManager.getQuestline(qlname);
		if (ql == null) {
			Bukkit.getLogger().warning("[NeoQuests] Failed to add questline " + qlname + " to player " + p.getName() + ", questline doesn't exist.");
			return;
		}
		this.activeQuestlines.put(ql.getKey().toUpperCase(), ql);
	}
	
	public void addQuestline(Questline ql) {
		this.activeQuestlines.put(ql.getKey().toUpperCase(), ql);
	}
	
	public void removeQuestline(String ql) {
		this.activeQuestlines.remove(ql.toUpperCase());
	}
	
	public void addCompletedQuest(CompletedQuest cq) {
		this.completedQuests.put(cq.getQuest().getKey().toUpperCase(), cq);
	}
	
	public Collection<Questline> getActiveQuestlines() {
		return activeQuestlines.values();
	}
	
	public void setActiveQuests(HashMap<String, QuestInstance> activeQuests) {
		this.activeQuests = activeQuests;
	}
	
	public HashMap<String, QuestInstance> getActiveQuestsHashMap() {
		return activeQuests;
	}
	
	public boolean hasActiveQuest(String key) {
		return activeQuests.containsKey(key.toUpperCase());
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	
	public int getAccount() {
		return acct;
	}
	
	public void reset() {
		this.loc = null;
		this.activeQuestlines.clear();
		for (QuestInstance qi : activeQuests.values()) {
			qi.cleanupInstances();
		}
		this.activeQuests.clear();
		this.completedQuests.clear();
	}
	
	public void reloadQuests() {
		try {
			for (QuestInstance qi : activeQuests.values()) {
				QuestInstance reloadedqi = new QuestInstance(this, QuestsManager.getQuest(qi.getQuest().getKey()), qi.getStage());
				addActiveQuest(reloadedqi);
				for (ObjectiveSetInstance osi : qi.getObjectiveSetInstances()) {
					reloadedqi.setupInstances(false);
					reloadedqi.getObjectiveSetInstance(osi.getKey()).setObjectiveCounts(osi.getCounts());
				}
				qi.cleanupInstances(); // Must be done after for loop because the instances get cleared
			}
			QuestsManager.initializeOrGetQuester(p).startListening();
		}
		catch (Exception e) {
			Bukkit.getLogger().warning("[NeoQuests] Failed to reload quester " + p.getName());
		}
	}
	
	public void setStage(String key, int stage) {
		activeQuests.get(key.toUpperCase()).setStage(stage);
	}
}
