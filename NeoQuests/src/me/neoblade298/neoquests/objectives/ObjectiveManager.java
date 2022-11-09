package me.neoblade298.neoquests.objectives;

import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;

import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.io.LineConfig;
import me.neoblade298.neocore.io.LineConfigManager;
import me.neoblade298.neoquests.NeoQuests;
import me.neoblade298.neoquests.objectives.builtin.*;

public class ObjectiveManager {
	private static LineConfigManager<Objective> mngr;
	
	public ObjectiveManager() {
		mngr = new LineConfigManager<Objective>(NeoQuests.inst(), "objectives");

		register(new CreateTownObjective());
		register(new DeliverItemsObjective());
		register(new FakeObjective());
		register(new FakeBarObjective());
		register(new GetSkillObjective());
		register(new GetStoredItemObjective());
		register(new GetTagObjective());
		register(new InteractNpcObjective());
		register(new JoinTownObjective());
		register(new KillMythicmobsObjective());
		register(new OpenProfessionInventoryObjective());
		register(new PlantSeedObjective());
		register(new ProfessionCraftObjective());
		register(new ReachLevelObjective());
		register(new ReachLocationObjective());
		register(new ReachTierObjective());
		register(new RespondConversationObjective());
		register(new RunCommandObjective());
		register(new SayObjective());
		register(new SlotAugmentObjective());
	}
	
	public static ArrayList<ObjectiveSet> parseObjectiveSets(ConfigurationSection cfg) throws NeoIOException {
		ArrayList<ObjectiveSet> objs = new ArrayList<ObjectiveSet>();
		for (String key : cfg.getKeys(false)) {
			ObjectiveSet set = new ObjectiveSet(cfg.getConfigurationSection(key));
			if (set != null) {
				objs.add(set);
			}
		}
		return objs;
	}
	
	public static Objective get(LineConfig cfg) throws NeoIOException {
		return mngr.get(cfg);
	}
	
	private void register(Objective obj) {
		mngr.register(obj);
	}
}
