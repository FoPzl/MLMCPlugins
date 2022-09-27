package me.ShanaChans.MLMCGuilds;

public class GuildMember 
{
	private Guild currentGuild;
	private GuildRank rank;
	private boolean chat;
	
	public GuildMember(Guild currentGuild, GuildRank rank)
	{
		this.setCurrentGuild(currentGuild);
		this.setRank(rank);
		this.setChatting(false);
	}


	public Guild getCurrentGuild() 
	{
		return currentGuild;
	}


	public void setCurrentGuild(Guild currentGuild) 
	{
		this.currentGuild = currentGuild;
	}


	public GuildRank getRank() 
	{
		return rank;
	}


	public void setRank(GuildRank rank) 
	{
		this.rank = rank;
	}


	public boolean isChatting() 
	{
		return chat;
	}


	public void setChatting(boolean chat) 
	{
		this.chat = chat;
	}
}
