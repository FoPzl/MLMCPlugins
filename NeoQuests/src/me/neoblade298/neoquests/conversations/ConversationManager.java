package me.neoblade298.neoquests.conversations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.interfaces.Manager;
import me.neoblade298.neocore.io.FileLoader;
import me.neoblade298.neoquests.NeoQuests;
import me.neoblade298.neoquests.conditions.Condition;
import me.neoblade298.neoquests.conditions.ConditionManager;

public class ConversationManager implements Manager, Listener {
	private static HashMap<Integer, ArrayList<Conversation>> npcConvs = new HashMap<Integer, ArrayList<Conversation>>();
	private static HashMap<String, Conversation> convs = new HashMap<String, Conversation>();
	private static HashMap<Player, ConversationInstance> activeConvs = new HashMap<Player, ConversationInstance>();
	private static File data = new File(NeoQuests.inst().getDataFolder(), "conversations");
	private static FileLoader convLoader, npcLoader;
	
	static {
		convLoader = (cfg, file) -> {
			for (String key : cfg.getKeys(false)) {
				try {
					if (convs.containsKey(key)) {
						NeoQuests.showWarning("Duplicate conversation " + key + " in file " + file.getPath() + ", " +
								"the loaded conversation with this key is in " + convs.get(key).getFileLocation());
						continue;
					}
					convs.put(key, new Conversation(file, cfg.getConfigurationSection(key)));
				}
				catch (Exception e) {
					NeoQuests.showWarning("Failed to load conversation " + key + " from file " + file.getPath(), e);
				}
			}
		};
		
		npcLoader = (cfg, file) -> {
			for (String key : cfg.getKeys(false)) {
				try {
					ArrayList<Conversation> clist = new ArrayList<Conversation>();
					int npcid = Integer.parseInt(key);
					if (npcConvs.containsKey(npcid)) {
						NeoQuests.showWarning("Duplicate npc conversation list " + key + " in file " + file.getPath());
						continue;
					}
					for (String line : cfg.getStringList(key)) {
						if (!convs.containsKey(line)) {
							NeoQuests.showWarning("Failed to load conversation for NPC " + key + " in file " + file.getPath() + ": " + line);
							continue;
						}
						clist.add(convs.get(line));
					}
					npcConvs.put(npcid, clist);
				}
				catch (Exception e) {
					NeoQuests.showWarning("Failed to load conversation " + key + " from file " + file.getPath() + " for NPC " + key, e);
				}
			}
		};
	}
	
	public ConversationManager() throws NeoIOException {
		// Load convs folder
		reload();
	}
	
	@Override
	public void reload() {
		convs.clear();
		npcConvs.clear();
		try {
			NeoCore.loadFiles(new File(data, "conversations"), convLoader);
			NeoCore.loadFiles(new File(data, "npcs"), npcLoader);
		}
		catch (Exception e) {
			NeoQuests.showWarning("Failed to reload ConversationManager", e);
		}
	}
	
	public static void endConversation(Player p, boolean runEndActions) {
		if (activeConvs.containsKey(p)) {
			activeConvs.get(p).endConversation(runEndActions);
			activeConvs.remove(p);
		}
	}
	
	public static void startConversation(Player p, Conversation conv, boolean ignoreConditions) {
		if (conv == null) {
			Bukkit.getLogger().log(Level.INFO, "[NeoQuests] Failed to start conversation with " + p.getName() + ", conv was null.");
			return;
		}
		if (!ignoreConditions) {
			Condition block = ConditionManager.getBlockingCondition(p, conv.getConditions());
			if (block != null) {
				Bukkit.getLogger().log(Level.INFO, "[NeoQuests] Failed to start conversation with " + p.getName() + ", key " + conv.getKey() + ". Failed condition " + block.getKey());
				return;
			}
		}
		startConversation(p, conv);
	}
	
	public static void startConversation(Player p, String key, boolean ignoreConditions) {
		Conversation conv = convs.get(key);
		if (conv == null) {
			Bukkit.getLogger().log(Level.INFO, "[NeoQuests] Failed to start conversation with " + p.getName() + ", key " + key + ", no conv found.");
		}
		startConversation(p, conv, ignoreConditions);
	}
	
	public static void startConversation(Player p, int npcid) {
		// Does the npc have any conversations
		if (!npcConvs.containsKey(npcid)) return;

		Conversation conv = null;
		for (Conversation c : npcConvs.get(npcid)) {
			Condition block = ConditionManager.getBlockingCondition(p, c.getConditions());
			if (block == null) {
				conv = c;
				break;
			}
		}
		
		if (conv != null) {
			startConversation(p, conv);
		}
	}
	
	public static Conversation getConversation(String conv) {
		return convs.get(conv);
	}
	
	public static ConversationInstance getActiveConversation(Player p) {
		return activeConvs.get(p);
	}
	
	public static void startConversation(Player p, Conversation conv) {
		Condition cond = ConditionManager.getBlockingCondition(p, conv.getConditions());
		if (cond != null) {
			p.sendMessage("§cYou can't start this conversation: " + cond.getExplanation(p));
			return;
		}
		
		ConversationInstance ci = new ConversationInstance(p, conv);
		activeConvs.put(p, ci);
	}
	
	public String getKey() {
		return "ConversationManager";
	}
	
	public static File getDataFolder() {
		return data;
	}

	@Override
	public void cleanup() {}
}
