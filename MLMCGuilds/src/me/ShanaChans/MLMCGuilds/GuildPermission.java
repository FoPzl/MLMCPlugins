package me.ShanaChans.MLMCGuilds;

public enum GuildPermission 
{
	INVITE("guilds.rank.invite"),
	CREATE("guilds.create"),
	KICK("guilds.rank.kick");
	
	private final String perm;
	
	private GuildPermission(final String perm)
	{
		this.perm = perm;
	}

	public String getPerm() 
	{
		return perm;
	}
	
}
