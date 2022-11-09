package me.neoblade298.neomythicextension.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.neoblade298.neocore.info.BossInfo;
import me.neoblade298.neocore.info.InfoAPI;
import me.neoblade298.neoresearch.Research;

public class ResearchPointsBossMechanic implements ITargetedEntitySkill {

	protected final int amount;
	protected int level;
	protected final String boss;
	protected final Research nr;
	protected final BossInfo bi;
	protected String display;

    @Override
    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

	public ResearchPointsBossMechanic(MythicLineConfig config) {
        this.boss = config.getString("boss", "default");
		this.bi = InfoAPI.getBossInfo(this.boss);
        this.amount = config.getInteger("a");
        this.display = bi.getDisplayWithLevel(false);
        
        nr = (Research) Bukkit.getPluginManager().getPlugin("NeoResearch");
	}
	
	@Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		try {
			if (target.getBukkitEntity() instanceof Player && data.getCaster() instanceof ActiveMob) {
				if (data.getCaster().getLevel() <= 0) {
					return SkillResult.CONDITION_FAILED;
				}
				Player p = (Player) target.getBukkitEntity();
				nr.giveResearchPointsBoss(p, this.amount, bi.getKey(), false);
				return SkillResult.SUCCESS;
			}
			return SkillResult.INVALID_TARGET;
		} catch (Exception e) {
			e.printStackTrace();
			return SkillResult.ERROR;
		}
    }
}
