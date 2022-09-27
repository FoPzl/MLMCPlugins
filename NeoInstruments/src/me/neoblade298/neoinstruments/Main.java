package me.neoblade298.neoinstruments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
	
	// State of music play
	static Set<Player> bookPlaying = new HashSet<Player>();
	Set<Player> freePlaying = new HashSet<Player>();
	Set<Player> playingMusic = new HashSet<Player>();
	
	// To do with sounds
	HashMap<Player, String> instrument = new HashMap<Player, String>();
	Set<Player> upperRegister = new HashSet<Player>();
	Map<Player, Long> noteDelays = new HashMap<Player, Long>();
	HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	HashMap<String, Float> pitches = new HashMap<String, Float>();
	
	// Sync data
	Set<HashSet<Player>> syncSets = new HashSet<HashSet<Player>>();
	Set<Player> syncedPlayers = new HashSet<Player>();
	Map<Player, String[]> noteArrs = new HashMap<Player, String[]>();
	HashMap<Player, ArrayList<Player>> syncRequests = new HashMap<Player, ArrayList<Player>>();
	

	public void onEnable() {
		super.onEnable();
		Bukkit.getServer().getLogger().info("NeoInstruments Enabled");
		getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("music").setExecutor(new Commands(this));
		
		// Set up sounds hashmap
		sounds.put("Piano", Sound.BLOCK_NOTE_BLOCK_HARP);
		sounds.put("Ocarina", Sound.BLOCK_NOTE_BLOCK_FLUTE);
		sounds.put("Bell", Sound.BLOCK_NOTE_BLOCK_BELL);
		sounds.put("Chime", Sound.BLOCK_NOTE_BLOCK_CHIME);
		sounds.put("Bass Drum", Sound.BLOCK_NOTE_BLOCK_BASEDRUM);
		sounds.put("Guitar Pick", Sound.BLOCK_NOTE_BLOCK_GUITAR);
		sounds.put("Xylophone", Sound.BLOCK_NOTE_BLOCK_XYLOPHONE);
		sounds.put("Clicks & Sticks", Sound.BLOCK_NOTE_BLOCK_HAT);
		sounds.put("Harp", Sound.BLOCK_NOTE_BLOCK_PLING);
		sounds.put("Snare Drum", Sound.BLOCK_NOTE_BLOCK_SNARE);
		sounds.put("Double Bass", Sound.BLOCK_NOTE_BLOCK_BASS);
		sounds.put("Banjo", Sound.BLOCK_NOTE_BLOCK_BANJO);
		sounds.put("Didgeridoo", Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO);
		sounds.put("Cow Bell", Sound.BLOCK_NOTE_BLOCK_COW_BELL);
		sounds.put("Iron Xylophone", Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE);
		sounds.put("Musical Device", Sound.BLOCK_NOTE_BLOCK_BIT);
		
		// Set up pitches hashmap
		pitches.put(".", 0F);
		pitches.put("c", 0.5F);
		pitches.put("c#", 0.5297F);
		pitches.put("d", 0.5612F);
		pitches.put("d#", 0.5946F);
		pitches.put("e", 0.63F);
		pitches.put("f", 0.6674F);
		pitches.put("f#", 0.7071F);
		pitches.put("g", 0.7492F);
		pitches.put("g#", 0.7937F);
		pitches.put("a", 0.8409F);
		pitches.put("a#", 0.8909F);
		pitches.put("b", 0.9439F);
		pitches.put("C", 1.0F);
		pitches.put("C#", 1.0595F);
		pitches.put("D", 1.1125F);
		pitches.put("D#", 1.1892F);
		pitches.put("E", 1.2599F);
		pitches.put("F", 1.3348F);
		pitches.put("F#", 1.4142F);
		pitches.put("G", 1.4983F);
		pitches.put("G#", 1.5874F);
		pitches.put("A", 1.6818F);
		pitches.put("A#", 1.7818F);
		pitches.put("B", 1.8877F);
		pitches.put("##", 2.0F);
	}

	public void onDisable() {
		Bukkit.getServer().getLogger().info("NeoInstruments Disabled");
		super.onDisable();
	}

	@EventHandler(ignoreCancelled = false)
	public void rightClickEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getHand() == EquipmentSlot.OFF_HAND) {
			ItemStack offHandItem = p.getInventory().getItemInOffHand();
			if ((offHandItem != null) && (offHandItem.getType() == Material.WRITTEN_BOOK)
					&& (offHandItem.hasItemMeta()) && (offHandItem.getItemMeta().hasLore())
					&& offHandItem.getItemMeta().getLore().get(0).contains("Sheet Music")) {
				e.setCancelled(true);
			}
			return;
		}
		
		// Right click
		if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			
			// If not playing instrument
			if ((e.getItem() != null) && (e.getItem().hasItemMeta()) && (e.getItem().getItemMeta().hasLore())
					&& (!this.freePlaying.contains(p)) && (!bookPlaying.contains(p))) {
				List<String> lore = e.getItem().getItemMeta().getLore();
				
				if (lore.get(0).contains("Instrument")) {
					lore.set(0, ChatColor.stripColor(lore.get(0)));
					int tempSplit = lore.get(0).lastIndexOf(" ");
					String temp = lore.get(0).substring(0, tempSplit);
					instrument.put(p, temp);

					ItemStack offHandItem = p.getInventory().getItemInOffHand();
					if ((offHandItem != null) && (offHandItem.getType() == Material.WRITTEN_BOOK)
							&& (offHandItem.hasItemMeta()) && (offHandItem.getItemMeta().hasLore())
							&& offHandItem.getItemMeta().getLore().get(0).contains("Sheet Music")) {
						playBook(p, offHandItem);
					}
					else {
						this.freePlaying.add(p);
						PlayerInventory inv = e.getPlayer().getInventory();
						int held = inv.getHeldItemSlot();
						inv.setHeldItemSlot(8);
						ItemStack[] contents = inv.getContents();
						ItemStack instrm = contents[held];
						contents[held] = contents[8];
						contents[8] = instrm;
						inv.setContents(contents);
						e.getPlayer().sendMessage(
								"§4[§c§lMLMC§4] §7You begin playing your instrument freehand! Right click again to stop!");
					}
				}
			}
			
			// If playing instrument
			else if (this.freePlaying.contains(e.getPlayer()) || bookPlaying.contains(e.getPlayer())) {
				stopPlaying(e.getPlayer());
				e.getPlayer().sendMessage("§4[§c§lMLMC§4] §7You stopped playing your instrument!");
			}
		}
		
		// Left click
		else if (((e.getAction() == Action.LEFT_CLICK_AIR) || (e.getAction() == Action.LEFT_CLICK_BLOCK))
				&& (this.freePlaying.contains(e.getPlayer()))) {
			if (this.upperRegister.contains(e.getPlayer())) {
				this.upperRegister.remove(e.getPlayer());
			}
			else {
				this.upperRegister.add(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void changeSlotEvent(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		if (e.getNewSlot() == 8) {
			return;
		}
		if (this.freePlaying.contains(p)) {
			e.setCancelled(true);
			Sound sound = sounds.get(instrument.get(p));

			String[] lowNoSneakNoteArr = { "c", "d", "e", "f", "g", "a", "b", "C" };
			String[] lowSneakNoteArr = { "c#", "d#", "f", "f#", "g#", "a#", "C", "C#" };
			String[] highNoSneakNoteArr = { "C", "D", "E", "F", "G", "A", "B", "##" };
			String[] highSneakNoteArr = { "C#", "D#", "F", "F#", "G#", "A#", "##", "##" };
			float pitch = 0.0F;
			int slot = e.getNewSlot();

			if (!this.upperRegister.contains(p)) {
				if (!p.isSneaking()) {
					pitch = pitches.get(lowNoSneakNoteArr[slot]);
				}
				else {
					pitch = pitches.get(lowSneakNoteArr[slot]);
				}
			}
			else {
				if (!p.isSneaking()) {
					pitch = pitches.get(highNoSneakNoteArr[slot]);
				}
				else {
					pitch = pitches.get(highSneakNoteArr[slot]);
				}
			}

			p.getWorld().playSound(p.getLocation(), sound, 3.0F, pitch);
			p.getWorld().spawnParticle(Particle.NOTE, p.getLocation().add(0, 2, 0), 0, pitch, 0.0, 0.0);
		}
	}

	@EventHandler
	public void onSignedEvent(PlayerEditBookEvent e) {
		if (e.isSigning() && e.getPreviousBookMeta().hasLore()
				&& e.getPreviousBookMeta().getLore().get(0).contains("Sheet Music")) {
			List<String> lore = new ArrayList<String>();
			lore.add("§eSheet Music");
			BookMeta newMeta = e.getNewBookMeta();
			newMeta.setLore(lore);
			e.setNewBookMeta(newMeta);
		}
	}

	public class MusicRunnable extends BukkitRunnable {
		private final Player player;
		private final String[] chords;
		private final Sound sound;
		private int cnt = 0;

		public MusicRunnable(Player player, String[] chords, Sound sound) {
			this.player = player;
			this.chords = chords;
			this.sound = sound;
		}

		@Override
		public void run() {
			if (!Main.bookPlaying.contains(player)) {
				cancel();
			}
			if (cnt < chords.length) {
				while (chords[cnt].equalsIgnoreCase("<br>")) {
					cnt++;
				}
				String[] notes = chords[cnt].split(",");
				Set<Float> notePitches = new HashSet<Float>();
				for (String note : notes) {
					if (pitches.get(note) == null) {
						cancel();
						stopPlaying(player);
						player.sendMessage("§4[§c§lMLMC§4] §7There was a syntax error in your music at note: §e" + chords[cnt] + "§7! Playing stopped.");
					}
					else
					{
						notePitches.add(pitches.get(note));
					}
				}
				playChord(player, notePitches, sound);
				cnt++;
			}
			else {
				cancel();
				stopPlaying(player);
				player.sendMessage("§4[§c§lMLMC§4] §7You finished playing your sheet music!");
			}
		}
	}

	public void playChord(Player player, Set<Float> pitches, Sound sound) {
		for (float pitch : pitches) {
			if (pitch != 0.0F) {
				player.getWorld().playSound(player.getLocation(), sound, 3.0F, pitch);
				player.getWorld().spawnParticle(Particle.NOTE, player.getLocation().add(0, 2, 0), 0, pitch, 0.0, 0.0);
			}
		}
	}

	public void playBook(Player p, ItemStack book) {
		List<String> pages = ((BookMeta) book.getItemMeta()).getPages();
		List<String> notes = new ArrayList<String>();
		for (String page : pages) {
			page = ChatColor.stripColor(page.replaceAll("\n", " "));
			notes.addAll(Arrays.asList(page.split(" ")));
		}
		String[] notesArr = Arrays.copyOf(notes.toArray(), notes.toArray().length, String[].class);

		bookPlaying.add(p);

		if (!this.syncedPlayers.contains(p)) { // if playing solo
			p.sendMessage("§4[§c§lMLMC§4] §7You begin playing your sheet music! Right click again to stop!");
			playingMusic.add(p);
			new MusicRunnable(p, notesArr, sounds.get(instrument.get(p))).runTaskTimer(this, 0L, getNoteDelay(p));
		}
		else { // if playing synced (2+ people)
			HashSet<Player> syncSet = getsyncSet(p);
			this.noteArrs.put(p, notesArr);
			if (bookPlaying.containsAll(syncSet) && canPlaySync(syncSet)) {
				for (Player currPlayer : syncSet) {
					currPlayer.sendMessage(
							"§4[§c§lMLMC§4] §7You are playing your sheet music synced! Right click again to stop!");
					playingMusic.add(currPlayer);
					new MusicRunnable(currPlayer, this.noteArrs.get(currPlayer), sounds.get(instrument.get(currPlayer))).runTaskTimer(this, 0L, getNoteDelay(p));
				}
			}
			else {
				p.sendMessage("§4[§c§lMLMC§4] §7Waiting for synced players to begin playing.");
				for (Player syncer : syncSet) {
					if (!bookPlaying.contains(syncer)) {
						syncer.sendMessage("§4[§c§lMLMC§4] §7" + p.getName() + " wants to start playing! Waiting for you to begin playing.");
					}
				}
			}
		}
	}
	
	public boolean canPlaySync(HashSet<Player> syncSet) {
		for (Player p : syncSet) {
			if (playingMusic.contains(p)) {
				return false;
			}
		}
		return true;
	}

	public void editBook(Player player) {
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() == Material.WRITTEN_BOOK
				&& ((BookMeta) item.getItemMeta()).getAuthor().equals(player.getName())
				&& ((BookMeta) item.getItemMeta()).getGeneration() == null
				&& item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).contains("Sheet Music")) {
			ItemStack newItem = new ItemStack(item);
			newItem.setType(Material.WRITABLE_BOOK);
			player.getInventory().setItemInMainHand(newItem);
		}
		else {
			player.sendMessage("§4[§c§lMLMC§4] §7You cannot edit that!");
		}
	}

	public void getBook(Player player) {
		ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
		List<String> lore = new ArrayList<String>();
		lore.add("§eSheet Music");
		ItemMeta meta = book.getItemMeta();
		meta.setLore(lore);
		book.setItemMeta(meta);
		player.getInventory().addItem(book);
	}

	public void setTempo(Player player, int bpm) {
		if (bpm < 1) {
			bpm = 1;
		}
		long noteDelay = bpm;
		if (!this.noteDelays.containsKey(player)) {
			this.noteDelays.put(player, noteDelay);
		}
		else {
			this.noteDelays.replace(player, noteDelay);
		}
		player.sendMessage("§4[§c§lMLMC§4] §7Tempo set to " + bpm + ".");
	}

	public void askSync(Player asker, Player asked) {
		if (asked == null) {
			asker.sendMessage("§4[§c§lMLMC§4] §7" + asked + " unavailable.");
			return;
		}

		asker.sendMessage("§4[§c§lMLMC§4] §7Sync with " + asked.getName() + " requested.");
		asked.sendMessage("§4[§c§lMLMC§4] §7" + asker.getName()
				+ " is requesting to sync. Confirm with §c/music confirm " + asker.getName() + ".");
		if (syncRequests.containsKey(asker)) {
			ArrayList<Player> list = syncRequests.get(asker);
			list.add(asked);
		}
		else {
			ArrayList<Player> list = new ArrayList<Player>();
			list.add(asked);
			syncRequests.put(asker, list);
		}
	}

	public void confirmSync(Player asked, Player asker) {
		if (syncRequests.containsKey(asker)) {
			ArrayList<Player> list = syncRequests.get(asker);
			if (list.contains(asked)) {
				asked.sendMessage("§4[§c§lMLMC§4] §7Confirmed sync with "
						+ asker.getName() + ".");
				list.remove(asked);
				sync(asker, asked);
			}
		}
	}

	public void denySync(Player asked, Player asker) {
		if (syncRequests.containsKey(asker)) {
			ArrayList<Player> list = syncRequests.get(asker);
			if (list.contains(asked)) {
				list.remove(asked);
				asked.sendMessage("§4[§c§lMLMC§4] §7Denied sync with "
						+ asker.getName() + ".");
				asker.sendMessage("§4[§c§lMLMC§4] §7Sync with "
						+ asker.getName() + " denied.");
			}
		}
	}

	public void sync(Player player, Player syncTo) {
		if (this.syncedPlayers.contains(player)) {
			if (this.syncedPlayers.contains(syncTo)) {
				// if player and syncTo are in separate syncSets, combine them
				// otherwise, no need to do anything
				HashSet<Player> syncSet = getsyncSet(player);
				if (!syncSet.contains(syncTo)) {
					HashSet<Player> syncTossyncSet = getsyncSet(syncTo);
					syncSet.addAll(syncTossyncSet);
					this.syncSets.remove(syncTossyncSet);
				}
				else {
					player.sendMessage("§4[§c§lMLMC§4] §7You are already synced with " + syncTo.getName() + "!");
					return;
				}
			}
			else {
				this.syncedPlayers.add(syncTo);
				// add syncTo to player's syncSet
				getsyncSet(player).add(syncTo);
			}
		}
		else {
			this.syncedPlayers.add(player);
			if (this.syncedPlayers.contains(syncTo)) {
				// add player to syncTo's syncSet
				getsyncSet(syncTo).add(player);
			}
			else {
				this.syncedPlayers.add(syncTo);
				// add new syncSet with both player and syncTo
				HashSet<Player> syncSet = new HashSet<Player>();
				syncSet.add(player);
				syncSet.add(syncTo);
				this.syncSets.add(syncSet);
			}
		}

		player.sendMessage("§4[§c§lMLMC§4] §7Synced with " + syncTo.getName() + ".");
	}

	public void unsync(Player player) {

		stopPlaying(player);

		if (!this.syncedPlayers.contains(player)) {
			return;
		}
		player.sendMessage("§4[§c§lMLMC§4] §7Unsynced.");
		syncedPlayers.remove(player);

		HashSet<Player> syncSet = getsyncSet(player);
		if (syncSet.size() < 3) { // if updated syncSet will have nobody synced to each other
			for (Player p : syncSet) {
				if (syncedPlayers.contains(p)) {
					p.sendMessage("§4[§c§lMLMC§4] §7Unsynced.");
				}
				syncedPlayers.remove(p);
			}
			syncSets.remove(syncSet);
		}
		else {
			syncSet.remove(player);
		}
	}

	public void stopPlaying(Player player) {
		this.upperRegister.remove(player);
		this.freePlaying.remove(player);
		playingMusic.remove(player);
		bookPlaying.remove(player);
		instrument.remove(player);
	}

	// Helper Methods

	private HashSet<Player> getsyncSet(Player player) {
		HashSet<Player> syncSet = new HashSet<Player>();
		for (HashSet<Player> checkList : this.syncSets) {
			if (checkList.contains(player)) {
				syncSet = checkList;
				break;
			}
		}
		return syncSet;
	}

	private long getNoteDelay(Player player) {
		if (this.noteDelays.containsKey(player)) {
			return this.noteDelays.get(player);
		}
		return 4L;
	}
}