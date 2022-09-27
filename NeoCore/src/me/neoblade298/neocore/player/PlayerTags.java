package me.neoblade298.neocore.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.events.PlayerTagChangedEvent;
import me.neoblade298.neocore.events.ValueChangeType;

public class PlayerTags {
	private final String key;
	private HashMap<UUID, HashMap<String, Value>> values;
	private HashMap<UUID, HashSet<String>> changedValues;
	private final boolean hidden;
	
	public PlayerTags(String key, boolean hidden) {
		this.key = key;
		this.values = new HashMap<UUID, HashMap<String, Value>>();
		this.changedValues = new HashMap<UUID, HashSet<String>>();
		this.hidden = hidden;
	}
	
	public String getKey() {
		return key;
	}
	
	public Set<String> getAllKeys(UUID uuid) {
		if (!values.containsKey(uuid)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to get all keys of " + this.getKey() + " for " + uuid + ". UUID not initialized. Returning default.");
			return null;
		}
		return this.values.get(uuid).keySet();
	}
	
	public boolean exists(String tag, UUID uuid) {
		if (!values.containsKey(uuid)) {
			return false;
		}
		HashMap<String, Value> pValues = values.get(uuid);
		if (pValues.containsKey(tag)) {
			// If value has expired, remove it
			if (pValues.get(tag).isExpired()) {
				Value v = pValues.remove(tag);
				changedValues.get(uuid).add(key);
				Bukkit.getPluginManager().callEvent(new PlayerTagChangedEvent(Bukkit.getPlayer(uuid), this.key, key, v, ValueChangeType.EXPIRED));
				return false;
			}
			return true;
		}
		return false;
	}
	
	// Only happens on logout
	public void save(Statement insert, Statement delete, UUID uuid) {
		if (changedValues.containsKey(uuid) && !changedValues.get(uuid).isEmpty()) {
			HashMap<String, Value> pValues = values.get(uuid);
			if (NeoCore.isDebug()) {
				Bukkit.getLogger().log(Level.INFO, "[NeoCore] Debug: Changed values: " + this.getKey() + " " + changedValues.get(uuid) + " for " + uuid + ".");
			}
			
			// Only save changed values
			if (changedValues.size() > 0) Bukkit.getLogger().info("[NeoCore] Saving " + changedValues.size() + " changed tags for key " + this.getKey());
			for (String key : changedValues.get(uuid)) {
				
				// If value was set
				if (pValues.containsKey(key)) {
					// Skip and remove expired values
					if (pValues.get(key).isExpired()) {
						pValues.remove(key);
						continue;
					}
					long expiration = pValues.get(key).getExpiration();
					
					
					try {
						Bukkit.getLogger().log(Level.INFO, "[NeoCore] Saving tag " + this.getKey() + "." + key + " for " + uuid + ".");
						insert.addBatch("REPLACE INTO neocore_tags VALUES ('" + uuid + "','" + this.getKey()
						+ "','" + key + "'," + expiration + ");");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// If value was unset
				else {
					Bukkit.getLogger().log(Level.INFO, "[NeoCore] Removing tag " + this.getKey() + "." + key + " for " + uuid + ".");
					try {
						delete.addBatch("DELETE FROM neocore_tags WHERE `key` = '" + this.getKey() + "' AND tag = '" + key +
						"' AND uuid = '" + uuid + "';");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		values.remove(uuid);
		changedValues.remove(uuid);
	}
	
	public void load(Statement stmt, UUID uuid) {
		HashMap<String, Value> pSettings = new HashMap<String, Value>();
		this.values.put(uuid, pSettings);
		this.changedValues.put(uuid, new HashSet<String>());
		try {
			ResultSet rs = stmt.executeQuery("SELECT * FROM neocore_tags WHERE uuid = '" + uuid + "' AND `key` = '" + this.getKey() + "';");
			int count = 0;
			int expired = 0;
			while (rs.next()) {
				String tag = rs.getString(3);
				long expiration = rs.getLong(4);
				
				if (tag == null) {
					Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to load tag of " + this.getKey() + "." + tag + " for " + uuid + ". Value is null.");
				}
				if (NeoCore.isDebug()) {
					Bukkit.getLogger().log(Level.INFO, "[NeoCore] Debug: Loading tag: " + this.getKey() + "." + tag + " for " + uuid + ".");
				}
				
				if (expiration == -1 || expiration < System.currentTimeMillis()) {
					pSettings.put(tag, new Value(tag, expiration));
				}
				else {
					expired++;
				}
				count++;
			}
			if (count > 0) Bukkit.getLogger().info("[NeoCore] Loaded " + count + " tags, " + expired + " expired for key " + this.getKey() + " for player " + uuid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean set(String key, UUID uuid) {
		return set(key, uuid, -1);
	}
	
	// If expiration is 0, the original expiration is kept
	public boolean set(String key, UUID uuid, long expiration) {
		String value = key;
		
		if (!values.containsKey(uuid)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to set tag of " + this.getKey() + "." + key + " for " + uuid + ". UUID not initialized.");
			return false;
		}

		changedValues.get(uuid).add(key);
		
		Value curr = null;
		if (values.get(uuid).containsKey(key)) {
			curr = values.get(uuid).get(key);
			curr.setValue(value);
			if (expiration != 0) {
				curr.setExpiration(expiration);
			}
			Bukkit.getPluginManager().callEvent(new PlayerTagChangedEvent(Bukkit.getPlayer(uuid), this.key, key, curr, ValueChangeType.CHANGED));
		}
		else {
			curr = new Value(value, expiration);
			values.get(uuid).put(key, curr);
			Bukkit.getPluginManager().callEvent(new PlayerTagChangedEvent(Bukkit.getPlayer(uuid), this.key, key, curr, ValueChangeType.ADDED));
		}
		
		Bukkit.getLogger().log(Level.INFO, "[NeoCore] Set tag of " + this.getKey() + "." + key + " for " + uuid + " to " +
				curr.getValue() + ".");
		return true;
	}
	
	public boolean resetAllTags(UUID uuid) {
		ArrayList<String> fields = new ArrayList<String>(values.get(uuid).keySet());
		Bukkit.getLogger().log(Level.INFO, "[NeoCore] Resetting all tags of " + this.getKey() + " for " + uuid + ".");
		for (String key : fields) {
			reset(key, uuid);
		}
		return true;
	}
	
	public boolean reset(String key, UUID uuid) {
		if (!values.containsKey(uuid)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to reset tag of " + this.getKey() + "." + key + " for " + uuid + ". UUID not initialized.");
			return false;
		}
		changedValues.get(uuid).add(key);
		Value removed = values.get(uuid).remove(key);
		Bukkit.getPluginManager().callEvent(new PlayerTagChangedEvent(Bukkit.getPlayer(uuid), this.key, key, removed, ValueChangeType.REMOVED));
		Bukkit.getLogger().log(Level.INFO, "[NeoCore] Reset tag of " + this.getKey() + "." + key + " for " + uuid + ".");
		return true;
	}
	
	public boolean isHidden() {
		return hidden;
	}
}
