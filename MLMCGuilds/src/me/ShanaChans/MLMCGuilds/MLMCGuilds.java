package me.ShanaChans.MLMCGuilds;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.ShanaChans.MLMCGuilds.commands.GuildChat;
import me.ShanaChans.MLMCGuilds.commands.GuildCreate;
import me.ShanaChans.MLMCGuilds.commands.GuildDelete;
import me.ShanaChans.MLMCGuilds.commands.GuildDisplay;
import me.ShanaChans.MLMCGuilds.commands.GuildInvite;
import me.ShanaChans.MLMCGuilds.commands.GuildJoin;
import me.ShanaChans.MLMCGuilds.commands.GuildLeave;
import me.ShanaChans.MLMCGuilds.commands.GuildList;
import me.neoblade298.neocore.commands.CommandManager;
import net.md_5.bungee.api.ChatColor;



public class MLMCGuilds extends JavaPlugin implements Listener
{
	
	private static Plugin plugin;
	public static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
	
	public void onEnable() 
	{
        Bukkit.getServer().getLogger().info("MLMCGuilds Enabled");
        getServer().getPluginManager().registerEvents(this, this);
        initCommands();
        plugin = this;
        GuildManager gm = new GuildManager();
    }
	
    public void onDisable() 
    {
        org.bukkit.Bukkit.getServer().getLogger().info("MLMCGuilds Disabled");
        super.onDisable();
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e)
    {
    	if(GuildManager.getPlayers().containsKey(e.getPlayer().getUniqueId()))
    	{
    		GuildManager.getPlayers().get(e.getPlayer().getUniqueId()).setChatting(false);
    	}
    }
    
    private void initCommands()
    {
    	CommandManager guild = new CommandManager("guild");
    	CommandManager guildChat = new CommandManager("gc");
    	guild.register(new GuildCreate());
    	guild.register(new GuildLeave());
    	guild.register(new GuildDisplay());
    	guild.register(new GuildJoin());
    	guild.register(new GuildDelete());
    	guild.register(new GuildList());
    	guild.register(new GuildChat("chat"));
    	guildChat.register(new GuildChat(""));
    	guild.register(new GuildInvite());
    	guild.registerCommandList("help");
    	this.getCommand("guild").setExecutor(guild);
    	this.getCommand("gc").setExecutor(guildChat);
    }
    
    @EventHandler
	static void guildChat(AsyncPlayerChatEvent e)
	{
    	if(GuildManager.getPlayers().containsKey(e.getPlayer().getUniqueId()))
		{
    		if(GuildManager.getPlayers().get(e.getPlayer().getUniqueId()).isChatting() == true)
    		{
    			e.setCancelled(true);
    			
    			for(UUID player : (GuildManager.getGuilds().get(GuildManager.getGuildName(e.getPlayer())).getGuildPlayers()))
    			{
    				if(Bukkit.getPlayer(player).isOnline())
    				{
    					String msg = (ChatColor.DARK_GRAY +"[&#0041e9" + "GC" + ChatColor.DARK_GRAY + "] " + "&#0034ba" + e.getPlayer().getName() + ": " + "&#0041e9" + e.getMessage());
    					Bukkit.getPlayer(player).sendMessage(translateHexCodes(msg));
    				}
    			}
    		}
		}
	}
    
    public static String translateHexCodes(String textToTranslate) {

		Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
		StringBuffer buffer = new StringBuffer();

		while (matcher.find()) {
			matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString());
		}
		return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());

	}
    
    public static Plugin getPlugin()
    {
    	return plugin;
    }
}
