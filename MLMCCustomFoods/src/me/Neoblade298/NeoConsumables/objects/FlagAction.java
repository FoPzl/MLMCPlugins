package me.Neoblade298.NeoConsumables.objects;

public class FlagAction {
	boolean add;
	String flag;
	int duration;
	
	public FlagAction(String flag, int duration, boolean add) {
		this.flag = flag;
		this.duration = duration;
		this.add = add;
	}

	public boolean isAdd() {
		return add;
	}

	public String getFlag() {
		return flag;
	}

	public int getDuration() {
		return duration;
	}
}
