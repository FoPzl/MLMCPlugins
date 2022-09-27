package me.neoblade298.neosapiaddons.methods;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;

import me.neoblade298.neosapiaddons.PointSet;
import me.neoblade298.neosapiaddons.SAPIAddons;

public class ProfessClass {
	public static void professPlayer(Player p, CommandSender s, String classAfter) {
		PlayerClass pclass = SkillAPI.getPlayerData(p).getClass("class");
		String classname = new String();
		
		if (pclass == null) {
			classname = "N/A";
			professTier1(p, classAfter);
		}
		else {
			classname = pclass.getData().getName();
			professTier2(p, classAfter);
		}

		s.sendMessage(
				"§7Professed the class of §e" + p.getName() + " §7from §6" + classname + " §7to §6" + classAfter + "§7.");
	}
	
	private static void professTier1(Player p, String afterClass) {
		PlayerData data = SkillAPI.getPlayerData(p);
		data.profess(SkillAPI.getClass(afterClass));
		PlayerClass pclass = data.getClass("class");

		int currentLv = 1;
		int maxLv = pclass.getData().getMaxLevel();
		PointSet setSP = SAPIAddons.skillPoints.get(maxLv);
		PointSet setAP = SAPIAddons.attrPoints.get(maxLv);
		int expectedSP = setSP.getBasePoints() + setSP.getPointsPerLvl() * (currentLv - setSP.getBaseLvl());
		int expectedAP = setAP.getBasePoints() + setAP.getPointsPerLvl() * (currentLv - setAP.getBaseLvl());

		pclass.setPoints(expectedSP);
		data.setAttribPoints(expectedAP);

		data.init(p);
	}

	private static void professTier2(Player p, String afterClass) {
		PlayerData data = SkillAPI.getPlayerData(p);
		data.getClass("class").setLevel(30);
		data.profess(SkillAPI.getClass(afterClass));
		PlayerClass pclass = data.getClass("class");
		

		int currentLv = 30;
		int maxLv = pclass.getData().getMaxLevel();
		pclass.setLevel(30);
		PointSet setSP = SAPIAddons.skillPoints.get(maxLv);
		PointSet setAP = SAPIAddons.attrPoints.get(maxLv);
		int expectedSP = setSP.getBasePoints() + setSP.getPointsPerLvl() * (currentLv - setSP.getBaseLvl());
		int expectedAP = setAP.getBasePoints() + setAP.getPointsPerLvl() * (currentLv - setAP.getBaseLvl());

		pclass.setPoints(expectedSP);
		data.setAttribPoints(expectedAP);

		data.init(p);

		// Skill up the essence
		data.upgradeSkill(SkillAPI.getSkill(afterClass + "'s Essence"), true);
	}
}
