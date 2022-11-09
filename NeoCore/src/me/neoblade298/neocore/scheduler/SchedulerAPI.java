package me.neoblade298.neocore.scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.util.Util;

public class SchedulerAPI {
	private static final int DAYS_AVAILABLE_IN_SCHEDULER = 3;
	private static final int startupTime = getDateKey(Calendar.getInstance());
	private static ArrayList<HashMap<Integer, ArrayList<CoreRunnable>>> schedule = new ArrayList<HashMap<Integer, ArrayList<CoreRunnable>>>(3);
	private static HashMap<ScheduleInterval, ArrayList<CoreRunnable>> repeaters = new HashMap<ScheduleInterval, ArrayList<CoreRunnable>>();
	
	private static final int MINUTES_PER_SEGMENT = 15;
	
	public static void initialize() {
		for (int i = 0; i < DAYS_AVAILABLE_IN_SCHEDULER; i++) {
			schedule.add(new HashMap<Integer, ArrayList<CoreRunnable>>());
		}
		
		for (ScheduleInterval interval : ScheduleInterval.values()) {
			repeaters.put(interval, new ArrayList<CoreRunnable>());
		}

		scheduleTimekeeper();
	}
	
	// Every 15 minutes
	private static void scheduleTimekeeper() {
		long ticks = getTicksToNextSegment(MINUTES_PER_SEGMENT);
		new BukkitRunnable() {
			public void run() {
				runScheduledItems();
				scheduleTimekeeper();
			}
		}.runTaskLater(NeoCore.inst(), ticks);
	}
	
	private static void runScheduledItems() {
		Calendar inst = Calendar.getInstance();
		int diff = getDateKey(inst) - startupTime;
		int hour = inst.get(Calendar.HOUR_OF_DAY);
		int minute = inst.get(Calendar.MINUTE);
		minute -= minute % 15;
		int timeKey = (hour * 100) + minute;
		
		// Scheduled items first
		HashMap<Integer, ArrayList<CoreRunnable>> day = schedule.get(diff);
		if (day != null) {
			if (day.containsKey(timeKey)) {
				for (CoreRunnable cr : day.get(timeKey)) {
					cr.runOrSchedule();
				}
				day.remove(timeKey);
			}
		}

		// Repeating tasks
		runRepeaters(ScheduleInterval.FIFTEEN_MINUTES);

		if (minute % 30 == 0) {
			runRepeaters(ScheduleInterval.HALF_HOUR);
		}
		
		if (minute == 0) {
			runRepeaters(ScheduleInterval.HOUR);
			
			if (hour == 13) {
				runRepeaters(ScheduleInterval.DAILY);
			}
		}
	}
	
	private static void runRepeaters(ScheduleInterval interval) {
		Iterator<CoreRunnable> iter = repeaters.get(interval).iterator();
		Bukkit.getLogger().info("[NeoCore] Running " + interval + " scheduler, size of " + repeaters.get(interval).size());
		while (iter.hasNext()) {
			CoreRunnable cr = iter.next();
			if (cr.isCancelled) {
				Bukkit.getLogger().info("[NeoCore] Removing cancelled CR " + cr.getKey());
				iter.remove();
			}
			else {
				Bukkit.getLogger().info("[NeoCore] Running CR " + cr.getKey());
				cr.runOrSchedule();
			}
		}
	}
	
	private static CoreRunnable schedule(String key, int date, int hour, int minute, int second, Runnable runnable) {
		int diff = date - startupTime;
		if (diff >= 0 && diff <= 2) {
			int scheduledMinute = minute - (minute % 15); // Round to previous 15
			int offset = ((minute % 15) * 60) + second; // Offset is number of seconds after the scheduled 15 minute interval
			int time = (hour * 100) + (scheduledMinute);
	
			// If the schedule time is within 15 minutes from now, just make it a regular bukkitrunnable
			if (diff == 0 && time == getLastTimeKey()) {
				Calendar now = Calendar.getInstance();
				Calendar scheduledTime = Calendar.getInstance();
				scheduledTime.set(Calendar.MINUTE, minute);
				scheduledTime.set(Calendar.SECOND, second);
				long timeToSchedule = scheduledTime.getTimeInMillis() - now.getTimeInMillis();
				if (timeToSchedule >= 0) {
					CoreRunnable cr = new CoreRunnable(key, runnable, (minute * 60) + second);
					new BukkitRunnable() {
						public void run() {
							cr.runInstantly();
						}
					}.runTaskLater(NeoCore.inst(), timeToSchedule / 50);
				}
			}
			// If schedule time is > 15 minutes from now, put it in the schedule
			else {
				HashMap<Integer, ArrayList<CoreRunnable>> day = schedule.get(diff);
				
				ArrayList<CoreRunnable> runnables = day.getOrDefault(time, new ArrayList<CoreRunnable>());
				CoreRunnable cr = new CoreRunnable(key, runnable, offset);
				runnables.add(cr);
				day.putIfAbsent(time, runnables);
				return cr;
			}
		}
		return null;
	}
	
	public static CoreRunnable schedule(String key, int year, int month, int day, int hour, int minute, int second, Runnable runnable) {
		int date = (year * 10000) + (month * 100) + day;
		return schedule(key, date, hour, minute, second, runnable);
	}
	
	public static CoreRunnable schedule(String key, int hour, int minute, int second, Runnable runnable) {
		Calendar c = Calendar.getInstance();
		return schedule(key, getDateKey(c), hour, minute, second, runnable);
	}
	
	public static CoreRunnable schedule(String key, int hour, int minute, Runnable runnable) {
		return schedule(key, hour, minute, 0, runnable);
	}
	
	public static CoreRunnable scheduleRepeating(String key, ScheduleInterval interval, Runnable runnable) {
		return scheduleRepeating(key, interval, 0, runnable);
	}
	
	public static CoreRunnable scheduleRepeating(String key, ScheduleInterval interval, int offsetSeconds, Runnable runnable) {
		CoreRunnable cr = new CoreRunnable(key, runnable, offsetSeconds);
		repeaters.get(interval).add(cr);

		// If the schedule time is within 15 minutes from now, just make it a regular bukkitrunnable
		Calendar now = Calendar.getInstance();
		int minute = now.get(Calendar.MINUTE);
		int scheduledMinute = minute - (minute % 15); // Round to previous 15
		
		if (interval.getDivisor() != -1 && scheduledMinute % interval.getDivisor() == 0) {
			Calendar scheduledTime = Calendar.getInstance();
			scheduledTime.set(Calendar.SECOND, 0);
			scheduledTime.add(Calendar.SECOND, offsetSeconds);
			long timeToSchedule = scheduledTime.getTimeInMillis() - now.getTimeInMillis();
			if (timeToSchedule >= 0) {
				new BukkitRunnable() {
					public void run() {
						cr.runInstantly();
					}
				}.runTaskLater(NeoCore.inst(), timeToSchedule / 50);
			}
		}
		
		return cr;
	}
	
	public static CoreRunnable schedule(String key, long time, Runnable runnable) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return schedule(key, getDateKey(c), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), runnable);
	}
	
	private static int getDateKey(Calendar c) {
		return (c.get(Calendar.YEAR) * 10000) + ((c.get(Calendar.MONTH) + 1) * 100) + c.get(Calendar.DAY_OF_MONTH);
	}
	
	public static class CoreRunnable {
		private String key;
		private Runnable runnable;
		private boolean isCancelled;
		private int offset;
		public CoreRunnable(String key, Runnable runnable, int offset) {
			this.key = key;
			this.runnable = runnable;
			this.offset = offset;
			this.isCancelled = false;
		}
		
		public String getKey() {
			return key;
		}
		
		public boolean isCancelled() {
			return isCancelled;
		}
		
		public int getOffset() {
			return offset;
		}
		
		public void setCancelled(boolean cancelled) {
			this.isCancelled = cancelled;
		}
		
		public void runOrSchedule() {
			if (offset == 0) {
				if (!isCancelled) {
					runnable.run();
				}
			}
			else {
				new BukkitRunnable() {
					public void run() {
						if (!isCancelled) {
							runnable.run();
						}
					}
				}.runTaskLater(NeoCore.inst(), offset * 20);
			}
		}
		
		public void runInstantly() {
			if (!isCancelled) {
				runnable.run();
			}
		}
	}
	
	private static int getLastTimeKey() {
		Calendar c = Calendar.getInstance();
		int minute = c.get(Calendar.MINUTE);
		minute -= minute % MINUTES_PER_SEGMENT;
		return (c.get(Calendar.HOUR) * 100) + minute;
	}

	public static long getTicksToHour(int hour) {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.withHour(hour);
		return Duration.between(start, end).toMillis() / 50;
	}

	public static long getTicksToNextSegment(int segmentLength) {
		LocalDateTime start = LocalDateTime.now();
		int totalSegments = (60 / segmentLength); // Segment Length must cleanly divide 60
		int segmentsPassed = start.getMinute() / segmentLength;
		LocalDateTime end;
		if (segmentsPassed + 1 == totalSegments) {
			end = start.plusHours(1).truncatedTo(ChronoUnit.HOURS); // Next segment starts on the hour
		}
		else {
			end = start.truncatedTo(ChronoUnit.HOURS).plusMinutes((segmentsPassed + 1) * segmentLength).truncatedTo(ChronoUnit.MINUTES);
		}

		// Get Duration
		long between = (Duration.between(start, end).toMillis() / 50) + 20; // Add 20 because truncate will round it down 1 tick
		return between <= 20 ? segmentLength * 60 * 20 : between; // If between is 0, get the next segment
	}

	public static long getTicksPastPreviousSegment(int segmentLength) {
		LocalDateTime end = LocalDateTime.now();
		int segmentsPassed = end.getMinute() / segmentLength;
		LocalDateTime start;
		if (segmentsPassed == 0) {
			start = end.truncatedTo(ChronoUnit.HOURS); // Previous segment was top of the hour
		}
		else {
			start = end.truncatedTo(ChronoUnit.HOURS).plusMinutes((segmentsPassed - 1) * segmentLength).truncatedTo(ChronoUnit.MINUTES);
		}

		// Get Duration
		long between = (Duration.between(start, end).toMillis() / 50) + 20;
		return between <= 20 ? segmentLength * 60 * 20 : between; // If between is 0, get the previous segment
	}
	
	public static void display(CommandSender s) {
		int diff = getDateKey(Calendar.getInstance()) - startupTime;
		
		Util.msg(s, "&6-- Scheduled Runnables --");
		for (Entry<Integer, ArrayList<CoreRunnable>> e : schedule.get(diff).entrySet()) {
			ArrayList<CoreRunnable> crs = e.getValue();
			String msg = "&e" + e.getKey() + "&f: &6" + crs.get(0).getKey();
			for (int i = 1; i < crs.size(); i++) {
				msg += "&7, &6" + crs.get(i).getKey();
			}
			Util.msg(s, msg);
		}

		Util.msg(s, "&6-- Repeating Runnables --");
		ArrayList<CoreRunnable> list = repeaters.get(ScheduleInterval.DAILY);
		if (list.size() != 0) {
			String msg = "&eDaily&f: &6" + list.get(0).getKey();
			for (int i = 1; i < list.size(); i++) {
				msg += "&7, &6" + list.get(i).getKey();
			}
			Util.msg(s, msg);
		}
		list = repeaters.get(ScheduleInterval.HOUR);
		if (list.size() != 0) {
			String msg = "&eHour&f: &6" + list.get(0).getKey();
			for (int i = 1; i < list.size(); i++) {
				msg += "&7, &6" + list.get(i).getKey();
			}
			Util.msg(s, msg);
		}
		list = repeaters.get(ScheduleInterval.HALF_HOUR);
		if (list.size() != 0) {
			String msg = "&eHalf Hour&f: &6" + list.get(0).getKey();
			for (int i = 1; i < list.size(); i++) {
				msg += "&7, &6" + list.get(i).getKey();
			}
			Util.msg(s, msg);
		}
		list = repeaters.get(ScheduleInterval.FIFTEEN_MINUTES);
		if (list.size() != 0) {
			String msg = "&eFifteen Minutes&f: &6" + list.get(0).getKey();
			for (int i = 1; i < list.size(); i++) {
				msg += "&7, &6" + list.get(i).getKey();
			}
			Util.msg(s, msg);
		}
	}
}
