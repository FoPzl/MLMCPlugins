package me.neoblade298.neoleaderboard.points;

public enum NationPointType implements PointType {
	RAID("Town raids"),
	WAR("Wars"),
	NATION_EVENT("Nation event points");
	

	private final String display;
	private NationPointType(final String display) {
		this.display = display;
	}
	public String getDisplay() {
		return display;
	}
}
