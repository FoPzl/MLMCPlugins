package me.neoblade298.neoquests.objectives;

import org.bukkit.entity.Player;

import me.neoblade298.neocore.bar.BarAPI;
import me.neoblade298.neocore.bar.CoreBar;
import me.neoblade298.neoquests.listeners.ObjectiveListener;

public class ObjectiveInstance {
	private Player p;
	protected Objective obj;
	protected ObjectiveSetInstance set;
	private int count;
	private String barPrefix;

	public ObjectiveInstance(Player p, Objective obj, ObjectiveSetInstance set) {
		this.p = p;
		this.obj = obj;
		this.set = set;
		this.barPrefix = "Q-" + set.getQuest().getKey() + "-" + set.getKey() + "-";
	}

	public Objective getObjective() {
		return obj;
	}

	public Player getPlayer() {
		return p;
	}

	public int getCount() {
		return count;
	}

	public boolean setCount(int count, boolean loading) {
		this.count = Math.min(obj.getNeeded(), count);
		if (!loading) updateBar(); // Only update bar if not initializing player
		if (isComplete()) {
			set.checkCompletion();
			return true;
		}
		return false;
	}

	public boolean addCount(int count) {
		this.count += count;
		this.count = Math.min(obj.getNeeded(), this.count);
		updateBar();
		if (isComplete()) {
			set.checkCompletion();
			return true;
		}
		return false;
	}

	public boolean incrementCount() {
		if (this.count < obj.getNeeded()) {
			this.count++;
			updateBar();
		}
		if (isComplete()) {
			set.checkCompletion();
			return true;
		}
		return false;
	}
	
	public void updateBar() {
		CoreBar cb = BarAPI.getBar(p);
		if (!cb.isEnabled()) return;
		if (isComplete()) {
			// Only increment progress bar if we're already following it
			if (cb.getTopic().equals(barPrefix + obj.hashCode())) {
				cb.setProgress(1);
			}
		}
		else {
			if (!cb.getTopic().equals(barPrefix + obj.hashCode()) && !obj.getDisplay().startsWith("Hide")) {
				cb.setTitle("§7(§c/q§7) " + obj.getDisplay());
				cb.setTopic(barPrefix + obj.hashCode());
			}
			cb.setProgress((double) this.count / (double) obj.getNeeded());
		}
	}

	public boolean isComplete() {
		return count >= obj.getNeeded();
	}
	
	public void finalize(Player p) {
		obj.finalize(p);
	}
	
	public void stopListening() {
		ObjectiveListener.stopListening(this);
	}
	
	public void startListening() {
		ObjectiveListener.startListening(this);
	}
}
