package me.ShanaChans.MLMCGuilds;

import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.io.IOComponent;

public class GuildManager implements IOComponent
{
	private static HashMap<String, Guild> guilds = new HashMap<String, Guild>();
	private static HashMap<UUID, Guild> guildsUUID = new HashMap<UUID, Guild>();
	private static HashMap<UUID, GuildMember> players = new HashMap<UUID, GuildMember>();
	private static HashMap<String, GuildRank> ranks = new HashMap<String, GuildRank>();
	private static HashSet<GuildPermission> perms = new HashSet<GuildPermission>();
	
	public GuildManager()
	{
		ranks.put("Owner", new GuildRank(perms, 'x', "Owner"));
		ranks.put("Member", new GuildRank(perms, 'x', "Member")); 
	}
	
	/**
	 * Creates a guild given a name if it is not taken
	 * @param guildName
	 * @param player
	 */
	public static void createGuild(String guildName, Player player)
	{
		if(players.containsKey(player.getUniqueId()))
		{
			player.getPlayer().sendMessage("You are already in a guild!");		
			return;
		}
		if(guildName.length() >= 12)
		{
			player.getPlayer().sendMessage("Your guild name must be 12 characters or less!");	
			return;
		}
		if(guilds.containsKey(guildName.toUpperCase()))
		{
			player.getPlayer().sendMessage("That guild name already exists!");
			return;
		}
		
		player.getPlayer().sendMessage("You have successfully created a guild!");
		// make guild variable and do adding player to it
		
		guilds.put(guildName.toUpperCase(), new Guild(guildName, player, ranks));

	}
	
	/**
	 * Deletes guild if you are the owner
	 * @param player
	 */
	public static void deleteGuild(Player player)
	{
		if(!players.containsKey(player.getUniqueId()))
		{
			player.getPlayer().sendMessage("You are not in a guild!");
			return;
		}
		
		if(!players.get(player.getUniqueId()).getRank().equals(ranks.get("Owner")))
		{
			player.getPlayer().sendMessage("You are not the owner of the guild!");
			return;
		}
		
		player.getPlayer().sendMessage("Guild deleted!");
		
		String tempGuildName = getGuildName(player);
		
		for(UUID members : guilds.get(getGuildName(player)).getGuildPlayers())
		{
			players.get(members).setChatting(false);
			players.remove(members);
		}
		guilds.remove(tempGuildName);
	}
	
	/**
	 * Adds player to guild given guild name
	 * @param guildName
	 * @param player
	 */
	public static void joinGuild(String guildName, Player player)
	{
		if(players.containsKey(player.getUniqueId()))
		{
			player.getPlayer().sendMessage("You are already in a guild!");
			return;
		}
		
		if(!guilds.containsKey(guildName.toUpperCase()))
		{
			player.getPlayer().sendMessage("Guild does not exist!");
			return;
		}
		
		if(!guilds.get(guildName.toUpperCase()).getInvitedPlayers().contains(player.getUniqueId()))
		{
			player.getPlayer().sendMessage("You have not been invited to that guild!");
			return;
		}
		
		guilds.get(guildName.toUpperCase()).addMember(player, guildName, ranks);
		
		for(UUID members : guilds.get(guildName.toUpperCase()).getGuildPlayers())
		{
			if(Bukkit.getPlayer(members).isOnline())
			{
				Bukkit.getPlayer(members).sendMessage(player.getName() + " has joined the guild!");
			}
		}
		
		if(guilds.get(guildName.toUpperCase()).getInvitedPlayers().contains(player.getUniqueId())) 
		{
			guilds.get(guildName.toUpperCase()).removeInvitedPlayers(player);
		}
	}
	
	/**
	 * Removes player from guild
	 * @param player
	 */
	public static void leaveGuild(Player player)
	{
		if(!players.containsKey(player.getUniqueId()))
		{
			player.getPlayer().sendMessage("You are not in a guild!");
			return;
		}
		
		for(UUID members : guilds.get(getGuildName(player)).getGuildPlayers())
		{
			if(Bukkit.getPlayer(members).isOnline())
			{
				Bukkit.getPlayer(members).sendMessage(player.getName() + " has left the guild!");
			}
		}
		players.get(player.getPlayer().getUniqueId()).setChatting(false);
		guilds.get(getGuildName(player)).removeMember(player);
		players.remove(player.getUniqueId());
	}
	
	/**
	 * Toggles the chat of a player
	 * @param player
	 */
	public static void toggleChat(Player player)
	{
		if(players.get(player.getPlayer().getUniqueId()).isChatting() == true)
		{
			players.get(player.getPlayer().getUniqueId()).setChatting(false);
			return;
		}
		players.get(player.getPlayer().getUniqueId()).setChatting(true);
	}
	
	/**
	 * Sends guild chat a message
	 * @param player
	 */
	public static void sendChat(Player player, String[] args)
	{
		String msg = "";
		for(int i = 1; i < args.length; i++)
		{
			msg = msg.concat(args[i] + " ");
		}
		
		for(UUID players : (GuildManager.getGuilds().get(GuildManager.getGuildName(player.getPlayer())).getGuildPlayers()))
		{
			if(Bukkit.getPlayer(players).isOnline())
			{
				String coloredMsg = (ChatColor.DARK_GRAY +"[&#0041e9" + "GC" + ChatColor.DARK_GRAY + "] " + "&#0034ba" + player.getPlayer().getName() + ": " + "&#0041e9" + msg);
				Bukkit.getPlayer(players).sendMessage(MLMCGuilds.translateHexCodes(coloredMsg));
			}
		}
	}
	
	/**
	 * Displays guild in chat to player
	 * @param player
	 */
	public static void displayGuild(Player player)
	{
		if(!players.containsKey(player.getUniqueId()))
		{
			player.getPlayer().sendMessage("You are not in a guild!");
			return;
		}
		String coloredMsg = ("&#0034ba.o0o._______________.[&#0041e9" + guilds.get(getGuildName(player)).getGuildName() + "&#0034ba]._______________.o0o.");
		player.getPlayer().sendMessage(MLMCGuilds.translateHexCodes(coloredMsg));
	}
	
	/**
	 * Displays the list of guilds to player
	 * @param player
	 */
	public static void listGuild(Player player)
	{
		if(guilds.isEmpty())
		{
			String coloredMsg = ("&#0034ba.o0o._______________.(&#0041e9 Guild List &#0034ba)._______________.o0o.");
			player.getPlayer().sendMessage(MLMCGuilds.translateHexCodes(coloredMsg));
			player.getPlayer().sendMessage("There are no guilds yet!");
			return;
		}
		String coloredMsg = ("&#0034ba.o0o._______________.(&#0041e9 Guild List &#0034ba)._______________.o0o.");
		player.getPlayer().sendMessage(MLMCGuilds.translateHexCodes(coloredMsg));
		
		TreeMap<String, Guild> sortedGuilds = new TreeMap<>();
		sortedGuilds.putAll(guilds);
		for(Guild guild : sortedGuilds.values())
		{
			player.getPlayer().sendMessage(guild.getGuildName());
		}
		
	}
	
	/**
	 * Invites a player to your guild
	 * @param player
	 * @param args
	 */
	public static void inviteGuild(Player player, String args)
	{
		Player invitedPlayer = Bukkit.getPlayer(args);
		if(!players.containsKey(player.getUniqueId()))
		{
			player.getPlayer().sendMessage("You are not in a guild!");
			return;
		}
		if(!invitedPlayer.isOnline())
		{
			player.getPlayer().sendMessage("Invalid Player.");
			return;
		}
		if(players.containsKey(invitedPlayer.getUniqueId()))
		{
			player.getPlayer().sendMessage("Player is already in a guild!");
			return;
		}
		if(guilds.get(getGuildName(player)).getInvitedPlayers().contains(invitedPlayer.getUniqueId()))
		{
			player.getPlayer().sendMessage("Player has already been invited!");
			return;
		}
		
		guilds.get(getGuildName(player)).addInvitedPlayers(invitedPlayer);
		player.getPlayer().sendMessage("Player has been invited.");
		invitedPlayer.getPlayer().sendMessage("You have been invited to: " + guilds.get(getGuildName(player)).getGuildName());
		Bukkit.getScheduler().runTaskLater(MLMCGuilds.getPlugin(),() -> {
			if(invitedPlayer.isOnline() && guilds.get(getGuildName(player)).getInvitedPlayers().contains(invitedPlayer.getUniqueId())) 
			{
				invitedPlayer.getPlayer().sendMessage("You have been invite for " + getGuildName(player) + " has expired.");
			}
		}, 20L * 30L);
		
		Bukkit.getScheduler().runTaskLater(MLMCGuilds.getPlugin(),() -> {
			if(guilds.get(getGuildName(player)).getInvitedPlayers().contains(invitedPlayer.getUniqueId())) 
			{
				guilds.get(getGuildName(player)).removeInvitedPlayers(invitedPlayer);
			}
		}, 20L * 30L);
	}
	
	public static HashMap<UUID, GuildMember> getPlayers()
	{
		return players;
	}
	
	public static HashMap<String, Guild> getGuilds()
	{
		return guilds;
	}
	
	public static String getGuildName(Player player)
	{
		return players.get(player.getUniqueId()).getCurrentGuild().getGuildName().toUpperCase();
	}

	@Override
	public void cleanup(Statement stm1, Statement stm2) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return "Guild Manager";
	}

	@Override
	public void loadPlayer(Player player, Statement stm) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preloadPlayer(OfflinePlayer player, Statement stm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void savePlayer(Player player, Statement stm1, Statement stm2) {
		// TODO Auto-generated method stub
		
	}
}
