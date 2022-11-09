package me.neoblade298.neoquests.objectives;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neoquests.NeoQuests;
import me.neoblade298.neoquests.objectives.builtin.FakeBarObjective;
import me.neoblade298.neoquests.objectives.builtin.FakeBarObjectiveInstance;
import me.neoblade298.neoquests.objectives.builtin.FakeObjective;
import me.neoblade298.neoquests.objectives.builtin.FakeObjectiveInstance;
import me.neoblade298.neoquests.quests.Quest;
import me.neoblade298.neoquests.quests.QuestInstance;

public class ObjectiveSetInstance {
	private Player p;
	private QuestInstance quest;
	private ObjectiveSet set;
	private ArrayList<ObjectiveInstance> objs;
	private String key;
	
	public ObjectiveSetInstance(Player p, QuestInstance quest, ObjectiveSet set) {
		this.p = p;
		this.quest = quest;
		this.set = set;
		this.key = set.getKey();
		
		objs = new ArrayList<ObjectiveInstance>();
		for (Objective o : set.getObjectives()) {
			if (o instanceof FakeObjective) {
				objs.add(new FakeObjectiveInstance(p, o, this));
			}
			else if (o instanceof FakeBarObjective) {
				objs.add(new FakeBarObjectiveInstance(p, o, this, ((FakeBarObjective) o).getConnection()));
			}
			else {
				objs.add(new ObjectiveInstance(p, o, this));
			}
		}
	}
	
	public String getKey() {
		return key;
	}
	
	public void checkCompletion() {
		for (ObjectiveInstance o : objs) {
			if (!o.isComplete()) {
				return;
			}
		}
		quest.completeObjectiveSet(this);
	}
	
	public int getNext() {
		return set.getNext();
	}
	
	public void startListening() {
		for (ObjectiveInstance o : objs) {
			o.startListening();
		}
		
		new BukkitRunnable() {
			public void run() {
				for (ObjectiveInstance o : objs) {
					o.getObjective().initialize(o);
				}
			}
		}.runTaskLater(NeoQuests.inst(), 20L);
	}
	
	public void stopListening() {
		for (ObjectiveInstance o : objs) {
			o.stopListening();
		}
	}
	
	public void finalizeObjectives() {
		for (ObjectiveInstance o : objs) {
			o.finalize(p);
		}
	}
	
	public ObjectiveSet getSet() {
		return set;
	}
	
	public void setObjectiveCounts(ArrayList<Integer> counts) {
		if (counts.size() != objs.size()) {
			Bukkit.getLogger().warning("[NeoQuests] Player " + p.getName() + " failed to load objective set " + key + " for quest " + quest.getQuest().getKey() + ", " +
					"counts.length " + counts.size() + " != objs.size " + objs.size());
			for (ObjectiveInstance oi : objs) {
				oi.setCount(0, true);
			}
			return;
		}
		
		int i = 0;
		for (ObjectiveInstance oi : objs) {
			if (oi.getObjective().getNeeded() < counts.get(i++)) {
				Bukkit.getLogger().warning("[NeoQuests] Player " + p.getName() + " failed to load objective set " + key + " for quest " + quest.getQuest().getKey() + ", " +
						"objective " + oi.getObjective().getKey() + " needed " + oi.getObjective().getNeeded() + " < counts[i] " + counts.get(i));
				for (ObjectiveInstance obi : objs) {
					obi.setCount(0, true);
				}
				return;
			}
		}
		
		i = 0;
		for (ObjectiveInstance oi : objs) {
			oi.setCount(counts.get(i++), true);
		}
	}
	
	public ArrayList<ObjectiveInstance> getObjectives() {
		return objs;
	}
	
	public ArrayList<Integer> getCounts() {
		ArrayList<Integer> counts = new ArrayList<Integer>();
		for (int i = 0; i < objs.size(); i++) {
			counts.add(objs.get(i).getCount());
		}
		return counts;
	}
	
	public String serializeCounts() {
		String counts = "";
		for (int i = 0; i < objs.size(); i++) {
			if (i == 0) {
				counts += objs.get(0).getCount();
				continue;
			}
			counts += "," + objs.get(i).getCount();
		}
		return counts;
	}
	
	public Quest getQuest() {
		return quest.getQuest();
	}
}
