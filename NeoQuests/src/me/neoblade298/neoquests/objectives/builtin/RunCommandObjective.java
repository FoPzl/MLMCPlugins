package me.neoblade298.neoquests.objectives.builtin;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.neoblade298.neocore.io.LineConfig;
import me.neoblade298.neoquests.objectives.Objective;
import me.neoblade298.neoquests.objectives.ObjectiveEvent;
import me.neoblade298.neoquests.objectives.ObjectiveInstance;

public class RunCommandObjective extends Objective {
	private String command;
	private boolean startsWith = false, endsWith = false, contains = false;

	public RunCommandObjective() {
		super();
	}

	public RunCommandObjective(LineConfig cfg) {
		super(ObjectiveEvent.COMMAND, cfg, true);
		command = cfg.getLine();
		if (command.startsWith("*") && command.endsWith("*")) {
			contains = true;
			command = command.substring(1, command.length() - 1);
		}
		else if (command.startsWith("*")) {
			endsWith = true;
			command = command.substring(1);
		}
		else if (command.endsWith("*")) {
			startsWith = true;
			command = command.substring(0, command.length() - 1);
		}
	}

	@Override
	public Objective create(LineConfig cfg) {
		return new RunCommandObjective(cfg);
	}

	@Override
	public String getKey() {
		return "run-command";
	}

	public boolean checkEvent(PlayerCommandPreprocessEvent e, ObjectiveInstance o) {
		String msg = e.getMessage().substring(1); // Remove / from start of command
		if (contains) {
			if (msg.contains(command)) {
				o.incrementCount();
				return true;
			}
		}
		else if (endsWith) {
			if (msg.endsWith(command)) {
				o.incrementCount();
				return true;
			}
		}
		else if (startsWith) {
			if (msg.startsWith(command)) {
				o.incrementCount();
				return true;
			}
		}
		else {
			if (msg.equalsIgnoreCase(command)) {
				o.incrementCount();
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDisplay() {
		return "Hide this objective!";
	}

	@Override
	public boolean isHidden() {
		return true;
	}
}
