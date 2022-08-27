package me.fopzl.vote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VoteRewards {
	private Reward dailyReward;
	private HashMap<Integer, Reward> streakRewards;
	
	public void loadConfig() {
		// TODO
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

// first authorized reward is given
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