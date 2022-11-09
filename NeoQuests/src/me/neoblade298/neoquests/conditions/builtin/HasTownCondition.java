package me.neoblade298.neoquests.conditions.builtin;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import me.neoblade298.neocore.io.LineConfig;
import me.neoblade298.neoquests.conditions.Condition;

public class HasTownCondition extends Condition {
	private static final String key;
	private boolean negate;
	
	static {
		key = "has-town";
	}
	
	public HasTownCondition() {}
	
	public HasTownCondition(LineConfig cfg) {
		super(cfg);
		negate = cfg.getBool("negate", false);
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public boolean passes(Player p) {
		return TownyAPI.getInstance().getResident(p).hasTown() ^ negate;
	}

	@Override
	public String getExplanation(Player p) {
		return negate ? "You must not be in a town!" : "You must be in a town!";
	}

	@Override
	public Condition create(LineConfig cfg) {
		return new HasTownCondition(cfg);
	}
}
