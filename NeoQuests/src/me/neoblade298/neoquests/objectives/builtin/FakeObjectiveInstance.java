package me.neoblade298.neoquests.objectives.builtin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import me.neoblade298.neoquests.objectives.Objective;
import me.neoblade298.neoquests.objectives.ObjectiveInstance;
import me.neoblade298.neoquests.objectives.ObjectiveSetInstance;

public class FakeObjectiveInstance extends ObjectiveInstance {
	// Finds #.letters
	private static Pattern pattern = Pattern.compile("\\{(\\d)(\\.)(.*?)\\}");

	public FakeObjectiveInstance(Player p, Objective obj, ObjectiveSetInstance set) {
		super(p, obj, set);
	}

	public String getDisplay() {
		String line = obj.getDisplay();
		Matcher matcher = pattern.matcher(line);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String found = line.substring(matcher.start() + 1, matcher.end() - 1);
			String args[] = found.split("\\.");
			int objNum = Integer.parseInt(args[0]);
			boolean needed = args[1].equalsIgnoreCase("needed");
			ObjectiveInstance o = set.getObjectives().get(objNum);
			if (needed) {
				matcher.appendReplacement(sb, "" + o.getObjective().getNeeded());
			}
			else {
				matcher.appendReplacement(sb, "" + o.getCount());
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
