package me.neoblade298.neoquests.actions;

import java.util.HashMap;

import org.bukkit.entity.Player;

import me.neoblade298.neocore.io.LineConfig;

public class DescriptionDialogueAction implements Action, DialogueAction {
	private static final String key = "player";
	private String dialogue;
	
	public static void register(HashMap<String, Action> actions, HashMap<String, DialogueAction> dialogueActions) {
		actions.put(key, new DescriptionDialogueAction());
		dialogueActions.put(key, new DescriptionDialogueAction());
	}
	
	public DescriptionDialogueAction() {}
	
	public DescriptionDialogueAction(LineConfig cfg) {
		this.dialogue = parseDialogue(cfg);
	}

	@Override
	public void run(Player p) {
		p.sendMessage("�e" + p.getName() + this.dialogue);
	}

	@Override
	public Action create(LineConfig cfg) {
		return new DescriptionDialogueAction(cfg);
	}
	
	@Override
	public String parseDialogue(LineConfig cfg) {
		return "�7: " + cfg.getLine();
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