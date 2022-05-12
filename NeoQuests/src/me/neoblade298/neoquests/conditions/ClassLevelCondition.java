package me.neoblade298.neoquests.conditions;

import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;

import me.neoblade298.neoquests.util.LineConfig;

public class ClassLevelCondition implements Condition {
	private static final String key;
	private int min, max;
	
	static {
		key = "class-level";
		Condition.register(key, new ClassLevelCondition());
	}
	
	public ClassLevelCondition() {}
	
	public ClassLevelCondition(LineConfig cfg) {
		min = cfg.getInt("min", -1);
		max = cfg.getInt("max", 999);
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public boolean passes(Player p) {
		PlayerData data = SkillAPI.getPlayerData(p);
		if (data != null) return false;
		
		PlayerClass cls = data.getClass("class");
		if (cls == null) return false;
		
		int level = cls.getLevel();
		return level >= min && level <= max;
	}

	@Override
	public String getExplanation(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Condition newInstance(LineConfig cfg) {
		// TODO Auto-generated method stub
		return null;
	}

}
