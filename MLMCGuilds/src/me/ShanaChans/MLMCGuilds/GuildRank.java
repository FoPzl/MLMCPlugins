package me.ShanaChans.MLMCGuilds;

import java.util.HashSet;

public class GuildRank 
{
	private HashSet<GuildPermission> perms;
	private char rankIcon;
	private String rankName;
	
	public GuildRank(HashSet<GuildPermission> perms, char rankIcon, String rankName)
	{
		this.setPerms(perms);
		this.setRankIcon(rankIcon);
		this.setRankName(rankName);
	}

	public String getRankName() 
	{
		return rankName;
	}

	public void setRankName(String rankName) 
	{
		this.rankName = rankName;
	}

	public char getRankIcon() 
	{
		return rankIcon;
	}

	public void setRankIcon(char rankIcon) 
	{
		this.rankIcon = rankIcon;
	}

	public HashSet<GuildPermission> getPerms() 
	{
		return perms;
	}

	public void setPerms(HashSet<GuildPermission> perms) 
	{
		this.perms = perms;
	}
	
	

}
