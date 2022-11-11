package me.Neoblade298.NeoConsumables.bosschests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.Sound;

public class ChestStage {
	private static Random gen = new Random();
	private double chance;
	private Sound sound;
	private float pitch;
	private String effect;
	private ArrayList<ChestReward> rewards;
	private int totalWeight;
	
	public ChestStage(double chance, Sound sound, float pitch, String effect, ArrayList<ChestReward> rewards, int totalWeight) {
		this.chance = chance;
		this.sound = sound;
		this.pitch = pitch;
		this.effect = effect;
		this.rewards = rewards;
		this.totalWeight = totalWeight;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}

	public Sound getSound() {
		return sound;
	}

	public void setSound(Sound sound) {
		this.sound = sound;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public ArrayList<ChestReward> getRewards() {
		return rewards;
	}

	public void setRewards(ArrayList<ChestReward> rewards) {
		this.rewards = rewards;
	}

	public double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(int totalWeight) {
		this.totalWeight = totalWeight;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public ChestReward chooseReward() {
		int rand = gen.nextInt(totalWeight);
		Iterator<ChestReward> iter = rewards.iterator();
		while (iter.hasNext()) {
			ChestReward reward = iter.next();
			rand -= reward.getWeight();
			if (rand < 0) return reward;
		}
		return null;
	}
}
