package me.neoblade298.neopvp;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.interfaces.Manager;
import me.neoblade298.neopvp.generators.GeneratorManager;

public class NeoPvp extends JavaPlugin {
	private static NeoPvp inst;
	private static ArrayList<Manager> mngrs = new ArrayList<Manager>();
	public static StateFlag PROTECTION_ALLOWED_FLAG;
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("NeoPvp Enabled");
		mngrs.add(new GeneratorManager());
		
		NeoCore.registerIOComponent(this, new PvpManager());
		// WorldGuard
	    //SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
	    //sessionManager.registerHandler(RequiredTagFlagHandler.FACTORY, null);
	}
	
	@Override
	public void onLoad() {
		// WorldGuard
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
	    try {
	        // create a flag with the name "my-custom-flag"
	        StateFlag flag = new StateFlag("protection-allowed", false);
	        registry.register(flag);
	        PROTECTION_ALLOWED_FLAG = flag; // only set our field if there was no error
	    } catch (FlagConflictException e) {
	        // some other plugin registered a flag by the same name already.
	        // you can use the existing flag, but this may cause conflicts - be sure to check type
	    	e.printStackTrace();
	    	
	    } catch (IllegalStateException e) {
	    	e.printStackTrace();
	    }
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoPvp Disabled");
	    super.onDisable();
	}
	
	public static NeoPvp inst() {
		return inst;
	}
}
