package me.neoblade298.neoquests.objectives.builtin;

import me.neoblade298.neocore.io.LineConfig;
import me.neoblade298.neoquests.objectives.Objective;

public class FakeBarObjective extends Objective {
	private String line;
	protected int connection; // The obj that the bar's progress is connected to
	
	public FakeBarObjective() {
		super();
		this.needed = 0;
	}

	public FakeBarObjective(LineConfig cfg) {
		super(null, cfg);
		this.needed = 0;
		this.line = cfg.getLine();
		this.connection = cfg.getInt("connection", 0);
	}

	@Override
	public Objective create(LineConfig cfg) {
		return new FakeBarObjective(cfg);
	}

	@Override
	public String getKey() {
		return "fake-bar";
	}

	@Override
	public String getDisplay() {
		return line;
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
	
	public int getConnection() {
		return connection;
	}
}
