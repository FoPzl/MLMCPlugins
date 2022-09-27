package me.neoblade298.neoquests.objectives.builtin;

import me.neoblade298.neocore.events.PlayerTagChangedEvent;
import me.neoblade298.neocore.events.ValueChangeType;
import me.neoblade298.neocore.io.LineConfig;
import me.neoblade298.neoquests.NeoQuests;
import me.neoblade298.neoquests.objectives.Objective;
import me.neoblade298.neoquests.objectives.ObjectiveEvent;
import me.neoblade298.neoquests.objectives.ObjectiveInstance;

public class GetTagObjective extends Objective {
	private String tag;
	
	public GetTagObjective() {
		super();
	}

	public GetTagObjective(LineConfig cfg) {
		super(ObjectiveEvent.RECEIVE_TAG, cfg, true);
		tag = cfg.getString("tag", null);
	}

	@Override
	public Objective create(LineConfig cfg) {
		return new GetTagObjective(cfg);
	}

	@Override
	public String getKey() {
		return "get-tag";
	}

	public boolean checkEvent(PlayerTagChangedEvent e, ObjectiveInstance o) {
		if (!e.getKey().equals(NeoQuests.getPlayerTags(o.getPlayer()).getKey())) {
			return false;
		}
		
		if (e.getType() == ValueChangeType.ADDED && ((String) e.getValue().getValue()).equalsIgnoreCase(tag)) {
			o.incrementCount();
			return true;
		}
		return false;
	}

	@Override
	public String getDisplay() {
		return "Hide this objective!";
	}
	
	@Override
	public void initialize(ObjectiveInstance oi) {
		if (NeoQuests.getPlayerTags(oi.getPlayer()).exists(tag, oi.getPlayer().getUniqueId())) {
			oi.setCount(1, false);
		}
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
}
