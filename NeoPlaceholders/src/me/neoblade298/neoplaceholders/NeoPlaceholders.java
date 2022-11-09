package me.neoblade298.neoplaceholders;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.neoblade298.neoplaceholders.placeholders.*;
import me.neoblade298.neoplaceholders.placeholders.core.NeoCoreFieldPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.core.NeoCoreTagPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.core.QuestTagPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.mythicmobs.MinibossPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.mythicmobs.MinibossShortPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.mythicmobs.MythicmobsPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.other.ChatColorPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.other.EtcPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.other.LordboardPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.other.SkillAPIPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.other.VaultPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.research.ResearchKillsBossPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.research.ResearchKillsPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.research.ResearchNameBossPlaceholder;
import me.neoblade298.neoplaceholders.placeholders.research.ResearchNamePlaceholder;
import me.neoblade298.neoplaceholders.placeholders.research.ResearchPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.research.ResearchPointsBossPlaceholders;
import me.neoblade298.neoplaceholders.placeholders.research.ResearchPointsPlaceholders;

public class NeoPlaceholders extends JavaPlugin {

	public void onEnable() {
		Bukkit.getServer().getLogger().info("NeoPlaceholders Enabled");
		
		new MythicmobsPlaceholders().register();
		new SkillAPIPlaceholders().register();
		new BossMultipliersPlaceholders().register();
		new NeoCoreFieldPlaceholders().register();
		new NeoCoreTagPlaceholders().register();
		new NeoRelicsPlaceholders().register();
		new VaultPlaceholders(this).register();
		new EtcPlaceholders(this).registerPlaceholders();
		new NeoProfessionsPlaceholders().register();
		new NeoBossInstancesPlaceholders().register();
		new ResearchPlaceholders().register();
		new ResearchNamePlaceholder().register();
		new ResearchNameBossPlaceholder().register();
		new ResearchKillsPlaceholders().register();
		new ResearchPointsPlaceholders().register();
		new ResearchKillsBossPlaceholders().register();
		new ResearchPointsBossPlaceholders().register();
		new MinibossPlaceholders().register();
		new MinibossShortPlaceholders().register();
		new ChatColorPlaceholders().register();
		new SkillAPIPlaceholders().register();
		new LordboardPlaceholders().register();
		new QuestTagPlaceholders().register();
		new FoPzlVotePlaceholders().register();
	}

	public void onDisable() {
		org.bukkit.Bukkit.getServer().getLogger().info("NeoPlaceholders Disabled");
		super.onDisable();
	}
}
