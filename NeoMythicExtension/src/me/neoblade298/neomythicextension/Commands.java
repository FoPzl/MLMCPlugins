package me.neoblade298.neomythicextension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.neoblade298.neocore.util.Util;
import me.neoblade298.neomythicextension.objects.SpawnerDefinition;
import me.neoblade298.neomythicextension.objects.SpawnerMaker;


public class Commands implements CommandExecutor{
	MythicExt main;
	
	public Commands (MythicExt main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (sender.isOp() || sender.hasPermission("mycommand.staff")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				UUID uuid = p.getUniqueId();
				if (args.length == 0) { 
					p.sendMessage("§4/nme create §7- Starts spawner maker");
					p.sendMessage("§4/nme cancel §7- Cancels spawner maker");
					p.sendMessage("§4/nme view §7- Views current spawner maker");
					p.sendMessage("§4/nme location [world] [x,y,z pos1] [x,y,z pos2] §7- Sets corners of spawner maker");
					p.sendMessage("§4/nme add [MythicMob] [SpawnerName] [Group] [Block] §7- Sets a spawner definition");
					p.sendMessage("§4/nme remove # §7- Removes a spawner definition");
					p.sendMessage("§4/nme start §7- Completes the setup and generates the spawners");
					return true;
				}
				else if (args[0].equalsIgnoreCase("create")) {
					if (main.spawnermakers.containsKey(uuid)) {
						p.sendMessage("§4[§c§lMLMC§4] §7You are already creating a spawner maker! Do " +
								"§c/nme view §7or §c/nme cancel§7!");
					}
					else {
						SpawnerMaker sm = new SpawnerMaker();
						sm.display(p);
						main.spawnermakers.put(uuid, sm);
					}
					return true;
				}
				else if (args[0].equalsIgnoreCase("cancel")) {
					if (main.spawnermakers.containsKey(uuid)) {
						main.spawnermakers.remove(uuid);
						p.sendMessage("§4[§c§lMLMC§4] §7SpawnerMaker successfully cancelled.");
					}
					else {
						p.sendMessage("§4[§c§lMLMC§4] §7You do not have a spawner maker to cancel!");
					}
					return true;
				}
				else if (args[0].equalsIgnoreCase("view")) {
					if (main.spawnermakers.containsKey(uuid)) {
						main.spawnermakers.get(uuid).display(p);
					}
					else {
						p.sendMessage("§4[§c§lMLMC§4] §7You do not have a spawner maker to view!");
					}
					return true;
				}
				// nme location [world] [x,y,z] [x,y,z]
				else if (args[0].equalsIgnoreCase("location") && args.length == 4) {
					if (main.spawnermakers.containsKey(uuid)) {
						int x1, x2, y1, y2, z1, z2;
						World world = Bukkit.getWorld(args[1]);
						String[] coords1 = args[2].split(",");
						String[] coords2 = args[3].split(",");
						
						// Sort the locations
						x1 = Integer.parseInt(coords1[0]);
						x2 = Integer.parseInt(coords2[0]);
						y1 = Integer.parseInt(coords1[1]);
						y2 = Integer.parseInt(coords2[1]);
						z1 = Integer.parseInt(coords1[2]);
						z2 = Integer.parseInt(coords2[2]);
						
						if (x1 > x2) {
							int temp = x1;
							x1 = x2;
							x2 = temp;
						}
						if (y1 > y2) {
							int temp = y1;
							y1 = y2;
							y2 = temp;
						}
						if (z1 > z2) {
							int temp = z1;
							z1 = z2;
							z2 = temp;
						}
						Location loc1 = new Location(world, x1, y1, z1);
						Location loc2 = new Location(world, x2, y2, z2);
						SpawnerMaker sm = main.spawnermakers.get(uuid);
						sm.setLoc1(loc1);
						sm.setLoc2(loc2);
						sm.display(p);
					}
					else {
						p.sendMessage("§4[§c§lMLMC§4] §7You must first do §c/nme create§7!");
					}
					return true;
				}
				// nme add [mm internal] [spawnername] [groupname] [block]
				else if (args[0].equalsIgnoreCase("add")) {
					if (main.spawnermakers.containsKey(uuid)) {
						MythicBukkit mm = MythicBukkit.inst();
						if (mm.getMobManager().getMythicMob(args[1]) == null) {
							p.sendMessage("§4[§c§lMLMC§4] §cInvalid MythicMob!");
							return true;
						}
						
						if (mm.getSpawnerManager().getSpawnerByName(args[2] + "1") != null) {
							p.sendMessage("§4[§c§lMLMC§4] §cSpawner already exists!");
							return true;
						}
						
						if (Material.getMaterial(args[4].toUpperCase()) == null) {
							p.sendMessage("§4[§c§lMLMC§4] §cInvalid block type!");
							return true;
						}
						
						SpawnerDefinition sd = new SpawnerDefinition();
						sd.setMob(args[1]);
						sd.setName(args[2]);
						sd.setGroup(args[3]);
						sd.setBlock(Material.getMaterial(args[4].toUpperCase()));
						
						main.spawnermakers.get(uuid).getDefinitions().add(sd);
						main.spawnermakers.get(uuid).display(p);
					}
					else {
						p.sendMessage("§4[§c§lMLMC§4] §7You must first do §c/nme create§7!");
					}
					return true;
				}
				else if (args[0].equalsIgnoreCase("remove")) {
					if (main.spawnermakers.containsKey(uuid)) {
						main.spawnermakers.get(uuid).getDefinitions().remove(Integer.parseInt(args[1]));
						main.spawnermakers.get(uuid).display(p);
					}
					else {
						p.sendMessage("§4[§c§lMLMC§4] §7You must first do §c/nme create§7!");
					}
					return true;
				}
				else if (args[0].equalsIgnoreCase("start")) {
					if (main.spawnermakers.containsKey(uuid)) {
						if (main.spawnermakers.get(uuid).generate(p)) {
							main.spawnermakers.remove(uuid);
						}
					}
					else {
						p.sendMessage("§4[§c§lMLMC§4] §7You must first do §c/nme create§7!");
					}
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("debug")) {
				MythicExt.debug = !MythicExt.debug;
				if (MythicExt.debug) {
					Util.msg(sender, "&7Debug set to &etrue");
				}
				else {
					Util.msg(sender, "&7Debug set to &efalse");
				}
		    	return true;
		    }
		    else if (args.length > 1 && args[0].equalsIgnoreCase("nbc")) {
		    	UUID uuid = UUID.fromString(args[1]);
		        Optional<ActiveMob> am = MythicBukkit.inst().getMobManager().getActiveMob(uuid);
	    		ArrayList<Entity> near = (ArrayList<Entity>) am.get().getEntity().getBukkitEntity().getNearbyEntities(40, 40, 40);
	    		String msg = "";
	    		for (int i = 2; i < args.length; i++) {
	    			msg += args[i];
	    			if (i != args.length - 1) {
	    				msg += " ";
	    			}
	    		}
	    		for(Entity e : near) {
	    			if (e instanceof Player) {
	    				Player p = (Player) e;
	    				p.sendMessage(msg);
	    			}
	    		}
		    }
		}
    	return false;
	}
}
