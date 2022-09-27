package me.neoblade298.neomythicextension.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.instancing.InstanceType;

public class IsInstanceCondition implements ICasterCondition {
	protected boolean action;
    
    public IsInstanceCondition(MythicLineConfig mlc) {
    	action = mlc.getBoolean("action", true);
    }

	@Override
	public boolean check(SkillCaster c) {
		if (action) {
			return NeoCore.getInstanceType() == InstanceType.SESSIONS;
		}
		else {
			return NeoCore.getInstanceType() != InstanceType.SESSIONS;
		}
	}
}
