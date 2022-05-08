package me.neoblade298.neomythicextension.drops;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IIntangibleDrop;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.Neoblade298.NeoProfessions.Managers.StorageManager;

public class StoredItemDrop implements IIntangibleDrop {

	protected final int id;
	protected final String mob;

	public StoredItemDrop(MythicLineConfig config) {
        this.mob = config.getString(new String[] {"mob", "m"}, "Ratface");
        this.id = config.getInteger(new String[] {"id", "i"}, 0);
        
        try {
            if (MythicBukkit.inst().getMobManager().getMythicMob(this.mob) == null) {
            	Bukkit.getLogger().log(Level.WARNING, "[NeoMythicExtension] Failed to load mob " + this.mob + " for GiveStoredItem " + this.id);
            	return;
            }
            StorageManager.addSource(this.id, this.mob, true);
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
	}

	@Override
	public void giveDrop(AbstractPlayer p, DropMetadata meta, double amount) {
		StorageManager.givePlayer((Player) p.getBukkitEntity(), this.id, (int) amount);
	}
}
