package me.neoblade298.neoquests.conditions.builtin;

import org.bukkit.entity.Player;

import me.neoblade298.neocore.io.LineConfig;
import me.neoblade298.neoquests.conditions.Condition;
import me.neoblade298.neoquests.conditions.ConditionResult;
import me.neoblade298.neoquests.quests.CompletedQuest;
import me.neoblade298.neoquests.quests.Quest;
import me.neoblade298.neoquests.quests.Quester;
import me.neoblade298.neoquests.quests.QuestsManager;

public class QuestTakeableCondition extends Condition {
	private static final String key;
	private String questname;
	
	static {
		key = "quest-takeable";
	}
	
	public QuestTakeableCondition() {}
	
	public QuestTakeableCondition(String questname) {
		result = ConditionResult.INVISIBLE;
		
		this.questname = questname;
	}
	
	public QuestTakeableCondition(LineConfig cfg) {
		super(cfg);
		
		questname = cfg.getString("quest", "N/A").toUpperCase();
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public boolean passes(Player p) {
		Quester quester = QuestsManager.getQuester(p);
		CompletedQuest cq = quester.getCompletedQuest(questname);

		if (quester.hasActiveQuest(questname)) {
			return false;
		}
		if (cq == null) {
			return true;
		}
		Quest q = cq.getQuest();
		if (cq.isSuccess()) {
			return q.canRepeat();
		}
		return q.canRetry() || q.canRepeat();
	}

	@Override
	public String getExplanation(Player p) {
		Quester quester = QuestsManager.getQuester(p);
		CompletedQuest cq = quester.getCompletedQuest(questname);

		if (quester.hasActiveQuest(questname)) {
			return "You're already on that quest! Type /q!";
		}
		if (cq == null) {
			return "error";
		}
		if (cq.isSuccess()) {
			return "You successfully completed this quest and it can't be repeated!";
		}
		return "You can't retry or repeat this quest!";
	}

	@Override
	public Condition create(LineConfig cfg) {
		return new QuestTakeableCondition(cfg);
	}
}
