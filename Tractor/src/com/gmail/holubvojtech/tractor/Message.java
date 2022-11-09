package com.gmail.holubvojtech.tractor;

import org.bukkit.entity.Player;

public enum Message
{
  TRACTOR_ENABLED("§aTractor §7(size %1$d x %2$d) §awas enabled"),  TRACTOR_DISABLED("§cTractor was disabled"),  NO_PERMISSION("§cYou don't have permission for this"),  CMD_USAGE("§c/tractor <size>");
  
  private String message;
  
  private Message(String message)
  {
    this.message = message;
  }
  
  public void send(Player player)
  {
    player.sendMessage(this.message);
  }
  
  public void send(Player player, Object... args)
  {
    player.sendMessage(String.format(this.message, args));
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public void setMessage(String message)
  {
    this.message = message;
  }
}
