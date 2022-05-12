package me.neoblade298.neoquests.actions;

import org.bukkit.entity.Player;

import me.neoblade298.neoquests.util.LineConfig;
import net.citizensnpcs.api.CitizensAPI;
import net.md_5.bungee.api.ChatColor;

public class PlayerDialogueAction implements Action, DialogueAction {
	private static final String key;
	private String dialogue;
	
	static { 
		key = "desc";
		Action.register(key, new PlayerDialogueAction());
	}
	
	public PlayerDialogueAction() {}
	
	public PlayerDialogueAction(String dialogue) {
		this.dialogue = dialogue;
	}

	@Override
	public void run(Player p) {
		p.sendMessage(this.dialogue);
	}

	@Override
	public Action newInstance(LineConfig cfg) {
		return new PlayerDialogueAction(parseDialogue(cfg));
	}
	
	@Override
	public String parseDialogue(LineConfig cfg) {
		return "�7�o" + cfg.getLine();
	}
	
	@Override
	public int getDelay() {
		return ChatColor.stripColor(this.dialogue).length() / 20;
	}
	
	@Override
	public String getKey() {
		return key;
	}
}
