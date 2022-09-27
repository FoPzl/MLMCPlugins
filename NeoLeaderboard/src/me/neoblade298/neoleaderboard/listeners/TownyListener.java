package me.neoblade298.neoleaderboard.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.event.DeleteNationEvent;
import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.event.nation.NationPreMergeEvent;
import com.palmergames.bukkit.towny.event.nation.NationTownLeaveEvent;
import com.palmergames.bukkit.towny.event.town.TownPreMergeEvent;

import me.neoblade298.neoleaderboard.points.PointsManager;

public class TownyListener implements Listener {
	@EventHandler
	public void onCreateNation(NewNationEvent e) {
		PointsManager.initializeNation(e.getNation());
	}
	
	@EventHandler
	public void onDeleteNation(DeleteNationEvent e) {
		PointsManager.deleteNationEntry(e.getNationUUID());
	}
	
	@EventHandler
	public void onTownLeaveNation(NationTownLeaveEvent e) {
		PointsManager.deleteTownEntry(e.getNation().getUUID(), e.getTown().getUUID(), true);
	}
	
	@EventHandler
	public void onPlayerLeaveTown(TownRemoveResidentEvent e) {
		if (e.getTown().getNationOrNull() == null) return;
		
		PointsManager.deletePlayerEntry(e.getResident().getUUID(), true);
	}
	
	@EventHandler
	public void onTownMerge(TownPreMergeEvent e) {
		if (!e.getSuccumbingTown().hasNation()) return;
		
		PointsManager.deleteTownEntry(e.getSuccumbingTown().getNationOrNull().getUUID(), e.getSuccumbingTown().getUUID(), true);
	}
	
	@EventHandler
	public void onNationMerge(NationPreMergeEvent e) {
		PointsManager.deleteNationEntry(e.getNation().getUUID());
	}
}
