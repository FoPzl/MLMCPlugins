package me.neoblade298.neoquests.objectives.builtin;

import org.bukkit.entity.Player;

import me.neoblade298.neocore.bar.BarAPI;
import me.neoblade298.neocore.bar.CoreBar;
import me.neoblade298.neoquests.objectives.Objective;
import me.neoblade298.neoquests.objectives.ObjectiveInstance;
import me.neoblade298.neoquests.objectives.ObjectiveSetInstance;

public class FakeBarObjectiveInstance extends ObjectiveInstance {

	public FakeBarObjectiveInstance(Player p, Objective obj, ObjectiveSetInstance set, int connection) {
		super(p, obj, set);
		
		CoreBar cb = BarAPI.getBar(p);
		Objective connected = set.getSet().getObjectives().get(connection);
		String topic = "Q-" + set.getQuest().getKey() + "-" + set.getKey() + "-" + connected.hashCode();
		cb.setTitle("§7(§c/q§7) " + obj.getDisplay());
		cb.setTopic(topic);
		cb.setProgress(0);
	}
}
