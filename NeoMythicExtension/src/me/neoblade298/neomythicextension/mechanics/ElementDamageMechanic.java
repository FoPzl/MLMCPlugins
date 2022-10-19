package me.neoblade298.neomythicextension.mechanics;

import org.bukkit.entity.LivingEntity;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;

public class ElementDamageMechanic implements ITargetedEntitySkill {
	protected final int damage;
	protected final String element;

    @Override
    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

	public ElementDamageMechanic(MythicLineConfig config) {
        this.element = config.getString(new String[] {"e", "element"}, "fire").toUpperCase();
        this.damage = config.getInteger(new String[] {"d", "damage"}, 20);
	}
	
	@Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		try {
			if (target.getBukkitEntity() instanceof LivingEntity) {
				LivingEntity ent = (LivingEntity) target.getBukkitEntity();
				DamageMetadata damage = new DamageMetadata(1, DamageType.SKILL, DamageType.MAGIC);
				damage.add(this.damage, Element.valueOf(this.element), DamageType.SKILL, DamageType.MAGIC);

				AttackMetadata attack = new AttackMetadata(damage, ent, null);
				MythicLib.plugin.getDamage().registerAttack(attack);
				return SkillResult.SUCCESS;
			}
			return SkillResult.INVALID_TARGET;
		} catch (Exception e) {
			e.printStackTrace();
			return SkillResult.ERROR;
		}
    }
}
