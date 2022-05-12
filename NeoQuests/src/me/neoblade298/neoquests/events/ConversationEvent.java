package me.neoblade298.neoquests.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.neoblade298.neoquests.conversations.Conversation;
import me.neoblade298.neoquests.conversations.ConversationResponse;
import me.neoblade298.neoquests.conversations.ConversationStage;

public class ConversationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
	private Conversation conv;
	private ConversationStage stage;
	private ConversationResponse resp;
	public ConversationEvent(Conversation conv, ConversationStage stage, ConversationResponse resp) {
		this.conv = conv;
		this.stage = stage;
		this.resp = resp;
	}
	public Conversation getConv() {
		return conv;
	}
	public ConversationStage getStage() {
		return stage;
	}
	public ConversationResponse getResp() {
		return resp;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
