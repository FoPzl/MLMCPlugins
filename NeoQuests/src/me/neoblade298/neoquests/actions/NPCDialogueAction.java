package me.neoblade298.neoquests.actions;

import java.util.HashMap;

import org.bukkit.entity.Player;

import me.neoblade298.neoquests.io.LineConfig;
import net.citizensnpcs.api.CitizensAPI;

public class NPCDialogueAction implements Action, DialogueAction {
	private static final String key = "npc";
	private String dialogue;
	
	public static void register(HashMap<String, Action> actions, HashMap<String, DialogueAction> dialogueActions) {
		actions.put(key, new NPCDialogueAction());
		dialogueActions.put(key, new NPCDialogueAction());
	}
	
	public NPCDialogueAction() {}
	
	public NPCDialogueAction(LineConfig cfg) {
		this.dialogue = parseDialogue(cfg);
	}

	@Override
	public void run(Player p) {
		p.sendMessage(this.dialogue);
	}

	@Override
	public Action newInstance(LineConfig cfg) {
		return new NPCDialogueAction(cfg);
	}
	
	@Override
	public String parseDialogue(LineConfig cfg) {
		String name = CitizensAPI.getNPCRegistry().getById(cfg.getInt("id", -1)).getFullName();
		String text = cfg.getLine();
		return name + "�7: " + text;
	}
	
	@Override
	public int getDelay() {
		return DialogueAction.getDelay(this.dialogue);
	}
	
	@Override
	public String getKey() {
		return key;
	}
}
