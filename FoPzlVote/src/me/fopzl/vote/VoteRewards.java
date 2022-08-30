package me.fopzl.vote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class VoteRewards {
	private Reward dailyReward;
	private HashMap<Integer, Reward> streakRewards;
	
	public void loadConfig(YamlConfiguration cfg) {
		HashMap<String, Reward> rewards = new HashMap<String, Reward>();
		
		HashMap<String, RawReward> rawRewards = new HashMap<String, RawReward>();
		ConfigurationSection sec = cfg.getConfigurationSection("rewards");
		for(String rName : sec.getKeys(false)) {
			rawRewards.put(rName, new RawReward(sec.getString(rName)));
		}
		
		ConfigurationSection groupSec = cfg.getConfigurationSection("groups");
		for(String gName : groupSec.getKeys(false)) {
			rewards.put(gName, new RewardGroup());
		}
		
		ConfigurationSection poolSec = cfg.getConfigurationSection("pools");
		for(String pName : poolSec.getKeys(false)) {
			rewards.put(pName, new RewardPool());
		}
		
		ConfigurationSection restrictSec = cfg.getConfigurationSection("permissioned-groups");
		for(String rName : restrictSec.getKeys(false)) {
			rewards.put(rName, new RestrictedReward());
		}
		
		for(String groupName : groupSec.getKeys(false)) {
			RewardGroup g = (RewardGroup)rewards.get(groupName);
			
			for(String groupItem : groupSec.getStringList(groupName)) {
				g.addReward(rewards.get(groupItem));
			}
		}
		
		for(String poolName : poolSec.getKeys(false)) {
			RewardPool p = (RewardPool)rewards.get(poolName);
			
			ConfigurationSection subSec = poolSec.getConfigurationSection(poolName);
			for(String poolItem : subSec.getKeys(false)) {
				p.addReward(rewards.get(poolItem), subSec.getInt(poolItem));
			}
		}
		
		for(String permGroupName : restrictSec.getKeys(false)) {
			RestrictedReward r = (RestrictedReward)rewards.get(permGroupName);
			
			for(Object o : restrictSec.getList(permGroupName)) {
				ConfigurationSection permGroupItem = (ConfigurationSection)o;
				String permName = permGroupItem.getName();
				String rewardName = permGroupItem.getString(permName);
				r.addReward(rewards.get(rewardName), permName);
			}
		}
		
		streakRewards = new HashMap<Integer, Reward>();
		ConfigurationSection streakSec = cfg.getConfigurationSection("streaks");
		for(String s : streakSec.getKeys(false)) {
			int streakNum = Integer.parseInt(s);
			
			for(String streakItem : streakSec.getStringList(s)) {
				streakRewards.put(streakNum, rewards.get(streakItem));
			}
		}
		
		dailyReward = rewards.get(cfg.getConfigurationSection("daily").getString("daily"));
	}
	
	// streak is in votes, not days
	public void rewardVote(Player p, int streak) {
		dailyReward.giveReward(p);
		
		if(streakRewards.containsKey(streak)) {
			streakRewards.get(streak).giveReward(p);
		}
	}
}

interface Reward {
	void giveReward(Player p);
}

// single reward given
class RawReward implements Reward {
	private String command;
	
	public RawReward(String cmd) {
		command = cmd;
	}

	public void giveReward(Player p) {
		// TODO: placeholder with p.getName()
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
	}
}

// all rewards in group given
class RewardGroup implements Reward {
	private HashSet<Reward> rewards = new HashSet<Reward>();
	
	public void addReward(Reward r) {
		rewards.add(r);
	}
	
	public void giveReward(Player p) {
		for(Reward r : rewards) {
			r.giveReward(p);
		}
	}
}

// one random reward is given
class RewardPool implements Reward {
	private static Random rng = new Random();
	
	private ArrayList<Object[]> lootTable = new ArrayList<Object[]>();
	private int sumWeights = 0;
	
	public void addReward(Reward reward, int weight) {
		lootTable.add(new Object[] { reward, weight }); // thank you java
		sumWeights += weight;
	}
	
	public void giveReward(Player p) {
		int choice = rng.nextInt(sumWeights);
		
		int index = 0;		
		while (choice >= 0) {
			choice -= (int)lootTable.get(++index)[1];
		}
		
		((Reward)lootTable.get(index)[0]).giveReward(p);
	}
}

//first authorized reward is given
class RestrictedReward implements Reward {
	private ArrayList<Object[]> rewards = new ArrayList<Object[]>(); // ordered by decreasing authority
	
	public void addReward(Reward reward, String permission) {
		rewards.add(new Object[]{ reward, permission }); // thank you java
	}
	
	public void giveReward(Player p) {
		for(Object[] tuple : rewards) {
			if(p.hasPermission((String)tuple[1])) {
				((Reward)tuple[0]).giveReward(p);
				return;
			}
		}
	}
}