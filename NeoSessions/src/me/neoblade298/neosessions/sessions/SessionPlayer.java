package me.neoblade298.neosessions.sessions;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;

public class SessionPlayer {
	private Player p;
	private PlayerStatus status;
	private String sessionKey, name, playerClass;
	private Session session;
	private int activeId;

	public SessionPlayer(Player p, String sessionKey) {
		this.p = p;
		this.name = p.getName();
		this.sessionKey = sessionKey;
		this.status = PlayerStatus.JOINING;
	}

	public Player getPlayer() {
		return p;
	}
	
	public String getName() {
		return name;
	}
	
	public String getClassName() {
		return playerClass;
	}

	public UUID getUUID() {
		return p.getUniqueId();
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;

		switch (status) {
		case JOINING:
			p.setGameMode(GameMode.ADVENTURE);
			break;
		case AWAITING_PLAYERS:
			activeId = SkillAPI.getPlayerAccountData(p).getActiveId();
			playerClass = SkillAPI.getPlayerData(p).getMainClass().getData().getName();
			break;
		case PARTICIPATING:
			break;
		case SPECTATING:
			SkillAPI.getPlayerAccountData(p).setAccount(13);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "vanish " + p.getName() + " on");
			p.setInvulnerable(true);
			break;
		case LEAVING:
			SkillAPI.getPlayerAccountData(p).setAccount(activeId);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "vanish " + p.getName() + " false");
			p.setInvulnerable(false);
			break;
		default:
			break;
		}
	}
}
