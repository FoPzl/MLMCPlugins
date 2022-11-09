package me.neoblade298.neoquests.objectives.builtin;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import me.neoblade298.neocore.io.LineConfig;
import me.neoblade298.neoquests.objectives.Objective;
import me.neoblade298.neoquests.objectives.ObjectiveEvent;
import me.neoblade298.neoquests.objectives.ObjectiveInstance;

public class CreateTownObjective extends Objective {
	
	public CreateTownObjective() {
		super();
	}

	public CreateTownObjective(LineConfig cfg) {
		super(ObjectiveEvent.CREATE_TOWN, cfg, true);
	}

	@Override
	public Objective create(LineConfig cfg) {
		return new CreateTownObjective(cfg);
	}

	@Override
	public String getKey() {
		return "create-town";
	}

	public boolean checkEvent(NewTownEvent e, ObjectiveInstance o) {
		o.incrementCount();
		return true;
	}

	@Override
	public String getDisplay() {
		return "Create town";
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
	
	@Override
	public void initialize(ObjectiveInstance oi) {
		TownyAPI api = TownyAPI.getInstance();
		if (api.getResident(oi.getPlayer()).getTownOrNull() != null) {
			oi.incrementCount();
		}
	}
}
