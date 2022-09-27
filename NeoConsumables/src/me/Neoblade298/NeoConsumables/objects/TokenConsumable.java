package me.Neoblade298.NeoConsumables.objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import de.tr7zw.nbtapi.NBTItem;
import me.Neoblade298.NeoConsumables.Consumables;
import me.neoblade298.neocore.util.Util;

public class TokenConsumable extends Consumable implements GeneratableConsumable {
	ArrayList<String> commands, negatePerms, baselore;
	String display;
	Material material;
	boolean boundToPlayer;
	long millisToExpire;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd hh:mm a z");
	
	public TokenConsumable(Consumables main, String key) {
		super(main, key);
		
		commands = new ArrayList<String>();
		negatePerms = new ArrayList<String>();
		baselore = new ArrayList<String>();
		material = Material.GOLD_INGOT;
	}
	
	public void setHoursToExpire(int hours) {
		if (hours == -1) {
			this.millisToExpire = -1;
		}
		else {
			this.millisToExpire = hours * 60 * 60 * 1000;
		}
	}
	
	public void setBoundToPlayer(boolean bound) {
		this.boundToPlayer = bound;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public void setCommands(ArrayList<String> commands) {
		this.commands = commands;
	}
	
	public void setNegatedPerms(ArrayList<String> negatedPerms) {
		this.negatePerms = negatedPerms;
	}
	
	public void setDisplay(String display) {
		this.display = correctColors(display);
	}
	
	public void setLore(ArrayList<String> lore) {
		for (String line : lore) {
			this.baselore.add(correctColors(line));
		}
	}

	public boolean canUse(Player p, ItemStack item) {
		// negate perms
		for (String perm : negatePerms) {
			if (p.hasPermission(perm)) {
				Util.msg(p, "&cThis token is currently active.");
				return false;
			}
		}
		
		
		NBTItem nbti = new NBTItem(item);
		long timestamp = nbti.getLong("timestamp");
		String player = nbti.getString("player");
		if (boundToPlayer && !player.equalsIgnoreCase(p.getName())) {
			Util.msg(p, "&cThis token is bound to " + player + "!");
			return false;
		}
		
		if (millisToExpire != -1 && timestamp + millisToExpire < System.currentTimeMillis()) {
			Util.msg(p, "&cThis token has already expired!");
			p.getInventory().removeItem(item);
			return false;
		}
		
		return true;
	}

	public void use(final Player p, ItemStack item) {
		// First get rid of any existing effects
		for (Sound s : this.getSounds()) {
			p.playSound(p.getLocation(), s, 1.0F, 1.0F);
		}
		p.sendMessage("§4[§c§lMLMC§4] §7You used " + item.getItemMeta().getDisplayName() + "§7. Check §c/boosts §7if applicable!");
		executeCommands(p);
		item.setAmount(item.getAmount() - 1);
	}

	public void executeCommands(Player player) {
		for (String cmd : this.commands) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%p", player.getName()));
		}
	}
	
	@Override
	public ItemStack getItem(Player p, int amount) {
		ItemStack item = new ItemStack(material);
		item.setAmount(amount);
		long timestamp = System.currentTimeMillis();
		
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>(baselore);
		if (boundToPlayer) {
			lore.add("§6Bound to " + p.getName());
		}
		if (millisToExpire != -1) {
			Calendar inst = Calendar.getInstance();
			inst.setTimeInMillis(timestamp + millisToExpire);
			lore.add("§cExpires " + sdf.format(inst.getTime()));
		}
		meta.setLore(lore);
		meta.setDisplayName(display);
		item.setItemMeta(meta);
		NBTItem nbti = new NBTItem(item);
		nbti.setString("consumable", key);
		nbti.setLong("timestamp", timestamp);
		if (boundToPlayer) {
			nbti.setString("player", p.getName());
		}
		return nbti.getItem();
	}
	
	private String correctColors(String line) {
		return line.replaceAll("\\\\&", "@").replaceAll("&", "§").replaceAll("@", "&");
	}

	public String getDisplay() {
		return display;
	}
}
