package me.neoblade298.neoleaderboard.points;

public enum PlayerPointType implements PointType {
	KILL_PLAYER("Player kills", "Player kills (5 / kill)"),
	PLAYTIME("Time online", "Time online (0.1 / minute)"),
	KILL_BOSS("Boss kills", "Boss kills (0.05 * boss level / kill)"),
	NORMAL_CRAFT("Point items crafted", "8 Diamonds + 1 Netherite Ingot crafted together (10 / craft)"),
	EDIT_BLOCK("Blocks placed/broken", "Blocks placed/broken (0.002 / block)"),
	PLAYER_EVENT("Player event points", "Player event points");
	

	private final String display, extendedDisplay;
	private PlayerPointType(final String display, final String extendedDisplay) {
		this.display = display;
		this.extendedDisplay = extendedDisplay;
	}
	public String getDisplay() {
		return display;
	}
	public String getExtendedDisplay() {
		return extendedDisplay;
	}
}
