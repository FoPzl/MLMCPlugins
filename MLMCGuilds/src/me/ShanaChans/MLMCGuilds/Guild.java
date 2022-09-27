package me.ShanaChans.MLMCGuilds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Guild 
{
	private String guildName;
	private HashMap<UUID, GuildMember> members = new HashMap<UUID, GuildMember>();
	private HashMap<String, GuildRank> ranks = new HashMap<String, GuildRank>();
	private HashSet<UUID> players = new HashSet<UUID>();
	private boolean open;
	private HashSet<UUID> invitedPlayers = new HashSet<UUID>();
	private UUID guildUUID;
	
	public Guild(String guildName, Player player, HashMap<String, GuildRank> ranks)
	{
		this.guildName = guildName;
		this.players.add(player.getUniqueId()); // remove
		GuildMember gm = new GuildMember(this, ranks.get("Owner")); // remove
		members.put(player.getUniqueId(), gm); // remove
		GuildManager.getPlayers().put(player.getUniqueId(), gm); // remove 
		this.setOpen(false);
		this.guildUUID = UUID.randomUUID();
	}

	public String getGuildName() 
	{
		return guildName;
	}

	public void setGuildName(String guildName) 
	{
		this.guildName = guildName;
	}
	
	public HashMap<UUID, GuildMember> getMembers() 
	{
		return members;
	}
	
	public HashSet<UUID> getGuildPlayers()
	{
		return players;
	}
	
	public void addMember(Player player, String guildName, HashMap<String, GuildRank> ranks)
	{
		GuildMember nm = new GuildMember(this, ranks.get("Member"));
		members.put(player.getUniqueId(), nm);
		players.add(player.getUniqueId());
		GuildManager.getPlayers().put(player.getUniqueId(), nm);
	}
	
	public void removeMember(Player player)
	{
		members.remove(player.getUniqueId());
		players.remove(player.getUniqueId());
		GuildManager.getPlayers().remove(player.getUniqueId());
	}

	public boolean isOpen() 
	{
		return open;
	}

	public void setOpen(boolean open) 
	{
		this.open = open;
	}

	public HashMap<String, GuildRank> getRanks() 
	{
		return ranks;
	}

	public void setRanks(HashMap<String, GuildRank> ranks) 
	{
		this.ranks = ranks;
	}

	public HashSet<UUID> getInvitedPlayers() 
	{
		return invitedPlayers;
	}

	public void addInvitedPlayers(Player player) 
	{
		this.invitedPlayers.add(player.getUniqueId());
	}
	
	public void removeInvitedPlayers(Player player) 
	{
		this.invitedPlayers.remove(player.getUniqueId());
	}

}
