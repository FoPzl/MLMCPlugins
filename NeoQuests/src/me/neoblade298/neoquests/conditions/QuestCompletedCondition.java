package me.neoblade298.neoquests.conditions;

import org.bukkit.entity.Player;

import me.neoblade298.neocore.io.LineConfig;
import me.neoblade298.neoquests.quests.CompletedQuest;
import me.neoblade298.neoquests.quests.QuestsManager;

public class QuestCompletedCondition implements Condition {
	private static final String key;
	private ConditionResult result;
	private String questname;
	private boolean success, hide;
	private int stage;
	
	static {
		key = "quest-not-completed";
	}
	
	public QuestCompletedCondition() {}
	
	public QuestCompletedCondition(LineConfig cfg) {
		result = ConditionResult.valueOf(cfg.getString("result", "INVISIBLE").toUpperCase());
		hide = cfg.getBool("hide", false);
		
		questname = cfg.getString("quest", "N/A").toUpperCase();
		stage = cfg.getInt("stage", -1);
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public boolean passes(Player p) {
		CompletedQuest cq = QuestsManager.getQuester(p).getCompletedQuest(questname);
		if (cq != null) {
			if (stage != -1) {
				return cq.getStage() == stage;
			}
			return true;
		}
		return false;
	}

	@Override
	public String getExplanation(Player p) {
		CompletedQuest cq = QuestsManager.getQuester(p).getCompletedQuest(questname);
		if (cq != null) {
			if (stage != -1) {
				return "You must complete quest " + cq.getQuest().getDisplay() + " in a different way!";
			}
			return "Error";
		}
		else {
			return "Quest " + QuestsManager.getQuest(questname).getDisplay() + " is not complete!";
		}
	}

	@Override
	public Condition create(LineConfig cfg) {
		return new QuestCompletedCondition(cfg);
	}

	@Override
	public ConditionResult getResult() {
		return result;
	}
}
