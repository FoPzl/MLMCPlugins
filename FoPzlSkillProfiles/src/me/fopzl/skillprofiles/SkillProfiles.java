package me.fopzl.skillprofiles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.player.PlayerSkillBar;
import com.sucy.skill.api.skills.Skill;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.io.IOComponent;
import me.neoblade298.neoresearch.Research;
import me.neoblade298.neoresearch.StoredAttributes;

public class SkillProfiles extends JavaPlugin implements IOComponent {
	HashMap<UUID, PlayerProfiles> playerProfiles = new HashMap<UUID, PlayerProfiles>();
	HashMap<UUID, Long> cooldowns = new HashMap<UUID, Long>(); // stores time last used
	static final int COOLDOWN = 10000; // milliseconds
	
	boolean debug = false;
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("FoPzlSkillProfiles Enabled");
		this.getCommand("skillprofile").setExecutor(new Commands(this));
		NeoCore.registerIOComponent(this, this);
	}
	
	public void onDisable() {
		Bukkit.getServer().getLogger().info("FoPzlSkillProfiles Disabled");
		super.onDisable();
	}
	
	public boolean toggleDebug(CommandSender sender) {
		debug = !debug;
		sender.sendMessage("§7Debugging " + (debug ? "enabled" : "disabled"));
		return true;
	}
	
	@Override
	public void savePlayer(Player p, Statement insert, Statement delete) {
		autosavePlayer(p, insert, delete);
		playerProfiles.remove(p.getUniqueId());
	}
	
	@Override
	public void loadPlayer(Player p, Statement stmt) {
		UUID uuid = p.getUniqueId();
		
		if(debug) {
			Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Starting loading player " + p.getName() + "(uuid=" + uuid + ")");
		}
		
		/* base profiles */
		PlayerProfiles pp = new PlayerProfiles();
		playerProfiles.put(uuid, pp);
		try {
			ResultSet rs = stmt.executeQuery("select * from skillprofiles_profile where uuid = '" + uuid + "';");
			while(rs.next()) {
				int accId = rs.getInt("accNum");
				String name = rs.getString("name");
				
				if(debug) {
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading profile: accId = " + accId + ", profileName = " + name == null ? "(NULL)" : name);
				}
				
				AccountProfiles ap;
				if(pp.profiles.containsKey(accId)) {
					ap = pp.profiles.get(accId);
				} else {
					ap = new AccountProfiles();
					pp.profiles.put(accId, ap);
				}
				
				ap.profiles.put(name, new Profile());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			playerProfiles.remove(uuid);
			return;
		}
		
		/* attributes */
		try {
			ResultSet rs = stmt.executeQuery("select * from skillprofiles_attribute where prof_uuid = '" + uuid + "';");
			while(rs.next()) {
				int accId = rs.getInt("prof_accNum");
				String profName = rs.getString("prof_name");
				
				String attrName = rs.getString("name");
				int attrValue = rs.getInt("value");
				
				if(debug) {
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading attribute for profile: accId = " + accId + ", profileName = " + profName == null ? "(NULL)" : profName);
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading attribute: name = " + attrName == null ? "(NULL)" : attrName + ", value = " + attrValue);
				}
				
				pp.profiles.get(accId).profiles.get(profName).attributes.put(attrName, attrValue);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			playerProfiles.remove(uuid);
			return;
		}
		
		/* research attributes */
		try {
			ResultSet rs = stmt.executeQuery("select * from skillprofiles_researchattribute where prof_uuid = '" + uuid + "';");
			while(rs.next()) {
				int accId = rs.getInt("prof_accNum");
				String profName = rs.getString("prof_name");
				
				String rattrName = rs.getString("name");
				int rattrValue = rs.getInt("value");
				
				if(debug) {
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading research attribute for profile: accId = " + accId + ", profileName = " + profName == null ? "(NULL)" : profName);
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading research attribute: name = " + rattrName == null ? "(NULL)" : rattrName + ", value = " + rattrValue);
				}
				
				pp.profiles.get(accId).profiles.get(profName).researchAttributes.put(rattrName, rattrValue);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			playerProfiles.remove(uuid);
			return;
		}		
		
		/* skill levels */
		try {
			ResultSet rs = stmt.executeQuery("select * from skillprofiles_skillLevel where prof_uuid = '" + uuid + "';");
			while(rs.next()) {
				int accId = rs.getInt("prof_accNum");
				String profName = rs.getString("prof_name");
				
				String skillName = rs.getString("name");
				int skillLevel = rs.getInt("value");
				
				if(debug) {
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading skill level for profile: accId = " + accId + ", profileName = " + profName == null ? "(NULL)" : profName);
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading skill level: skill = " + skillName == null ? "(NULL)" : skillName + ", level = " + skillLevel);
				}
				
				pp.profiles.get(accId).profiles.get(profName).skillLevels.put(skillName, skillLevel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			playerProfiles.remove(uuid);
			return;
		}
		
		/* skill binds */
		try {
			ResultSet rs = stmt.executeQuery("select * from skillprofiles_skillBind where prof_uuid = '" + uuid + "';");
			while(rs.next()) {
				int accId = rs.getInt("prof_accNum");
				String profName = rs.getString("prof_name");
				
				String skillName = rs.getString("name");
				Material material = Material.valueOf(rs.getString("material"));
				
				if(debug) {
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading skill bind for profile: accId = " + accId + ", profileName = " + profName == null ? "(NULL)" : profName);
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading skill bind: skill = " + skillName == null ? "(NULL)" : skillName + ", material = " + material == null ? "(NULL)" : material.toString());
				}
				
				pp.profiles.get(accId).profiles.get(profName).skillBinds.put(skillName, material);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			playerProfiles.remove(uuid);
			return;
		}
		
		/* skill bar */
		try {
			ResultSet rs = stmt.executeQuery("select * from skillprofiles_skillBarSlot where prof_uuid = '" + uuid + "';");
			while(rs.next()) {
				int accId = rs.getInt("prof_accNum");
				String profName = rs.getString("prof_name");
				
				int slot = rs.getInt("slot");
				String skillName = rs.getString("name");
				
				if(debug) {
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading skill bar slot for profile: accId = " + accId + ", profileName = " + profName == null ? "(NULL)" : profName);
					Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Loading skill bar slot: slot = " + slot + ", skill = " + skillName == null ? "(NULL)" : skillName);
				}
				
				pp.profiles.get(accId).profiles.get(profName).skillBar.put(slot, skillName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			playerProfiles.remove(uuid);
			return;
		}
		
		if(debug) {
			Bukkit.getLogger().log(Level.INFO, "[FoPzlSkillProfiles] Finished loading player " + p.getName() + "(uuid=" + uuid + ")");
		}
	}
	
	@Override
	public void autosavePlayer(Player p, Statement insert, Statement delete) {
		UUID uuid = p.getUniqueId();
		if(!playerProfiles.containsKey(uuid)) return;
		
		try {
			Statement baseStmt = NeoCore.getStatement();
			baseStmt.executeUpdate("delete from skillprofiles_profile where uuid = '" + uuid + "';");
			// FK constraint ensures rows in other tables are deleted as well
			
			for(Map.Entry<Integer, AccountProfiles> ppEntry : playerProfiles.get(uuid).profiles.entrySet()) {
				for(Map.Entry<String, Profile> apEntry : ppEntry.getValue().profiles.entrySet()) {
					String keyString = "'" + uuid + "', " + ppEntry.getKey() + ", '" + apEntry.getKey() + "'";
					
					baseStmt.executeUpdate("replace into skillprofiles_profile values (" + keyString + ");");
					
					Profile prof = apEntry.getValue();
					/* attributes */
					//delete.addBatch("delete from skillprofiles_attribute where prof_uuid = '" + uuid + "';");
					for(Map.Entry<String, Integer> attrEntry : prof.attributes.entrySet()) {
						insert.addBatch("replace into skillprofiles_attribute values (" + keyString + ", '" + attrEntry.getKey() + "', " + attrEntry.getValue() + ");");
					}
					
					/* research attributes */
					//delete.addBatch("delete from skillprofiles_researchattribute where prof_uuid = '" + uuid + "';");
					for(Map.Entry<String, Integer> rattrEntry : prof.researchAttributes.entrySet()) {
						insert.addBatch("replace into skillprofiles_researchattribute values (" + keyString + ", '" + rattrEntry.getKey() + "', " + rattrEntry.getValue() + ");");
					}
					
					/* skill levels */
					//delete.addBatch("delete from skillprofiles_skillLevel where prof_uuid = '" + uuid + "';");
					for(Map.Entry<String, Integer> skillEntry : prof.skillLevels.entrySet()) {
						insert.addBatch("replace into skillprofiles_skillLevel values (" + keyString + ", '" + Util.sqlEscape(skillEntry.getKey()) + "', " + skillEntry.getValue() + ");");
					}
					
					/* skill binds */
					//delete.addBatch("delete from skillprofiles_skillBind where prof_uuid = '" + uuid + "';");
					for(Map.Entry<String, Material> bindEntry : prof.skillBinds.entrySet()) {
						insert.addBatch("replace into skillprofiles_skillBind values (" + keyString + ", '" + Util.sqlEscape(bindEntry.getKey()) + "', '" + bindEntry.getValue().toString() + "');");
					}
					
					/* skill bar */
					//delete.addBatch("delete from skillprofiles_skillBarSlot where prof_uuid = '" + uuid + "';");
					for(Map.Entry<Integer, String> slotEntry : prof.skillBar.entrySet()) {
						insert.addBatch("replace into skillprofiles_skillBarSlot values (" + keyString + ", " + slotEntry.getKey() + ", '" + Util.sqlEscape(slotEntry.getValue()) + "');");
					}
				}
			}
			
			baseStmt.close();
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to save skill profiles for user " + p.getName());
			e.printStackTrace();
		}
	}
	
	@Override
	public void cleanup(Statement insert, Statement delete) {}
	
	@Override
	public String getKey() {
		return "SkillProfiles";
	}

	@Override
	public void preloadPlayer(OfflinePlayer arg0, Statement arg1) {}
	
	public boolean save(Player player, String profileName) {
		PlayerData data = SkillAPI.getPlayerData(player);
		
		if(!data.hasClass()) {
			player.sendMessage("§4[§c§lMLMC§4] §cError: §7You have no class!");
			return true;
		}
		
		if(!profileName.matches("[A-z0-9_]{1,16}")) {
			player.sendMessage("§4[§c§lMLMC§4] §cError: §7Invalid profile name");
			return true;
		}
		
		int accId = SkillAPI.getPlayerAccountData(player).getActiveId();
		UUID uuid = player.getUniqueId();
		
		PlayerProfiles profs;
		if(!playerProfiles.containsKey(uuid)) {
			profs = new PlayerProfiles();
			playerProfiles.put(uuid, profs);
		} else {
			profs = playerProfiles.get(uuid);
		}
		
		if(!profs.save(data, accId, profileName)) {
			//player.sendMessage("§4[§c§lMLMC§4] §cError: §7Profile not saved");
		} else {
			player.sendMessage("§4[§c§lMLMC§4] §6Saved profile: §e" + profileName);
		}
		return true;
	}
	
	public boolean load(Player player, String profileName) {
		PlayerData data = SkillAPI.getPlayerData(player);
		
		if(!data.hasClass()) {
			player.sendMessage("§4[§c§lMLMC§4] §cError: §7You have no class!");
			return true;
		}
		
		int accId = SkillAPI.getPlayerAccountData(player).getActiveId();
		UUID uuid = player.getUniqueId();
		
		/* handle cooldowns */
		Long lastUsed = cooldowns.get(uuid);
		if(lastUsed != null && System.currentTimeMillis() - lastUsed <= COOLDOWN) {
			player.sendMessage("§4[§c§lMLMC§4] §cError: §7Profile load on cooldown");
			return true;
		}
		
		/* actual load */
		if(!playerProfiles.containsKey(uuid) || !playerProfiles.get(uuid).load(data, accId, profileName)) {
			player.sendMessage("§4[§c§lMLMC§4] §cError: §7Profile §e" + profileName + " §7doesn't exist");
		} else {
			player.sendMessage("§4[§c§lMLMC§4] §6Loaded profile: §e" + profileName);
			cooldowns.put(uuid, System.currentTimeMillis());
		}
		return true;
	}
	
	public boolean list(Player player) {
		int accId = SkillAPI.getPlayerAccountData(player).getActiveId();
		UUID uuid = player.getUniqueId();
		
		if(!playerProfiles.containsKey(uuid) || !playerProfiles.get(uuid).list(player, accId)) {
			player.sendMessage("§4[§c§lMLMC§4] §cError: §7No profiles to list");
		}
		return true;
	}
	
	public boolean delete(Player player, String profileName) {
		PlayerData data = SkillAPI.getPlayerData(player);
		int accId = SkillAPI.getPlayerAccountData(player).getActiveId();
		UUID uuid = player.getUniqueId();
		
		if(!playerProfiles.containsKey(uuid) || !playerProfiles.get(uuid).delete(data, accId, profileName)) {
			player.sendMessage("§4[§c§lMLMC§4] §cError: §7Profile §e" + profileName + " §7doesn't exist");
		} else {
			player.sendMessage("§4[§c§lMLMC§4] §6Profile deleted: §e" + profileName);
		}
		return true;
	}
}

class PlayerProfiles {
	HashMap<Integer, AccountProfiles> profiles = new HashMap<Integer, AccountProfiles>();
	
	boolean save(PlayerData data, int accId, String profileName) {
		AccountProfiles profs;
		if(!profiles.containsKey(accId)) {
			profs = new AccountProfiles();
			profiles.put(accId, profs);
		} else {
			profs = profiles.get(accId);
		}
		
		return profs.save(data, profileName);
	}
	
	boolean load(PlayerData data, int accId, String profileName) {
		if(!profiles.containsKey(accId)) {
			return false;
		}
		
		return profiles.get(accId).load(data, profileName);
	}
	
	boolean list(Player player, int accId) {
		if(!profiles.containsKey(accId)) {
			return false;
		}
		
		return profiles.get(accId).list(player);
	}
	
	boolean delete(PlayerData data, int accId, String profileName) {
		if(!profiles.containsKey(accId)) {
			return false;
		}
		
		return profiles.get(accId).delete(profileName);
	}
}

class AccountProfiles {
	HashMap<String, Profile> profiles = new HashMap<String, Profile>();
	
	boolean save(PlayerData data, String profileName) {
		if(profiles.size() >= 3 &&
		   !profiles.containsKey(profileName) &&
		   !data.getPlayer().hasPermission("fopzlskillprofiles.poger")) {
			data.getPlayer().sendMessage("§4[§c§lMLMC§4] §cError: §7Can't save more than 3 profiles");
			return false;
		}
		
		profiles.put(profileName, new Profile(data));
		return true;
	}
	
	boolean load(PlayerData data, String profileName) {
		if(!profiles.containsKey(profileName)) {
			return false;
		}
		
		profiles.get(profileName).Load(data);
		return true;
	}
	
	boolean list(Player player) {
		if(profiles.size() == 0) {
			return false;
		}
		
		String msg = "";
		for(String s : profiles.keySet()) {
			msg += s + ", ";
		}
		msg = msg.substring(0, msg.length() - 2);
		
		player.sendMessage("§4[§c§lMLMC§4] §6Profiles: §e" + msg);
		return true;
	}
	
	boolean delete(String profileName) {
		if(!profiles.containsKey(profileName)) {
			return false;
		}
		
		profiles.remove(profileName);
		return true;
	}
}

class Profile {
	HashMap<String, Integer> attributes;
	HashMap<String, Integer> researchAttributes;
	HashMap<String, Integer> skillLevels; // TODO: research skills?
	HashMap<String, Material> skillBinds;
	HashMap<Integer, String> skillBar;
	
	public Profile() {
		attributes = new HashMap<String, Integer>();
		researchAttributes = new HashMap<String, Integer>();
		skillLevels = new HashMap<String, Integer>();
		skillBinds = new HashMap<String, Material>();
		skillBar = new HashMap<Integer, String>();
	}
	
	@SuppressWarnings("unchecked") // annoying
	public Profile(PlayerData data) {
		/* attributes */
		attributes = data.getInvestedAttributes();
		
		/* research attributes */
		researchAttributes = (HashMap<String, Integer>)Research.getPlayerAttributes(data.getPlayer()).getActiveAttrs().clone();
		researchAttributes.remove("unused");
		
		/* skills */
		Collection<PlayerSkill> skills = data.getSkills();
		skillLevels = new HashMap<String, Integer>();
		skillBinds = new HashMap<String, Material>();
		for(PlayerSkill ps : skills) {
			String name = ps.getData().getName();
			if(name.startsWith("Hidden")) {
				continue;
			}
			
			skillLevels.put(name, ps.getLevel());
			
			if(ps.getBind() != null) {
				skillBinds.put(name, ps.getBind());
			}
		}
		
		/* skill bar */
		skillBar = new HashMap<Integer, String>();
		skillBar.putAll(data.getSkillBar().getData());
	}
	
	public void Load(PlayerData data) {
		/* attributes */
		data.refundAttributes();
		for (Map.Entry<String, Integer> kvp : attributes.entrySet()) {
			for(int i = 0; i < kvp.getValue().intValue(); i++) {
				data.upAttribute(kvp.getKey());
			}
		}
		
		/* research attributes */
		StoredAttributes sAttr = Research.getPlayerAttributes(data.getPlayer());
		sAttr.unvestAll();
		for(Map.Entry<String, Integer> entry : researchAttributes.entrySet()) {
			sAttr.investAttribute(entry.getKey(), entry.getValue());
		}
		sAttr.applyAttributes(data.getPlayer());
		
		/* skills */
		data.clearAllBinds();
		
		Collection<PlayerSkill> skills = data.getSkills();
		PlayerSkill essence = null;
		
		for(PlayerSkill ps : skills) {
			if(ps.getData().getName().contains("'s Essence")){
				essence = ps;
			} else {
				int level = ps.getLevel();
				for(int i = 0; i < level; i++) {
					data.downgradeSkill(ps.getData());
				}
			}
		}
		
		// need to de-level essence last, and re-level first
		int eLevel = essence.getLevel();
		Skill eData = essence.getData();
		for(int i = 0; i < eLevel; i++) {
			data.downgradeSkill(eData);
		}
		for(int i = 0; i < skillLevels.getOrDefault(eData.getName(), 0); i++) {
			data.upgradeSkill(eData, true);
		}
		essence.setBind(skillBinds.get(eData.getName()));

		// now the rest		
		for(PlayerSkill ps : skills) {
			Skill s = ps.getData();
			if(!s.getName().contains("'s Essence")) {
				for(int i = 0; i < skillLevels.getOrDefault(s.getName(), 0); i++) {
					data.upgradeSkill(s, true);
				}
				ps.setBind(skillBinds.get(s.getName()));
			}
		}
		
		/* skill bar */
		PlayerSkillBar bar = data.getSkillBar();
		
		bar.clear(data.getPlayer());
		bar.getData().clear();
		bar.getData().putAll(skillBar);
		bar.setup(data.getPlayer());
	}
}

class Util {
	public static String sqlEscape(String str) {
		return str.replace("'", "''");
	}
}