package me.fopzl.stockslogger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.tchristofferson.stocks.api.events.PlayerBuySharesEvent;
import com.tchristofferson.stocks.api.events.PlayerSellAllSharesEvent;
import com.tchristofferson.stocks.api.events.PlayerSellSharesEvent;

import net.milkbowl.vault.economy.Economy;

public class StocksLogger extends JavaPlugin implements Listener {
	Economy econ;
	
	public void onEnable() {
		super.onEnable();
		econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onStockBuy(PlayerBuySharesEvent e) {
		String player = e.getOwner().getName();
		double amt = e.getShares();
		String symbol = e.getStock().getSymbol();
		double totPrice = e.getPrice();
		
		double initBal = econ.getBalance(e.getOwner());
		
		new BukkitRunnable() {
			public void run() {
				double finalBal = econ.getBalance(e.getOwner());
				
				getServer().getLogger().info("[StocksLogger] " + player + " bought " + amt + " shares of " + symbol + " for " + totPrice + "g total.");
				getServer().getLogger().info("               Initial balance = " + initBal + "");
				getServer().getLogger().info("               Final balance = " + finalBal + "");
			}
		}.runTaskLater(this, 10);
	}
	
	@EventHandler
	public void onStockSell(PlayerSellSharesEvent e) {
		String player = e.getOwner().getName();
		double amt = e.getShares();
		String symbol = e.getStock().getSymbol();
		double totPrice = e.getPrice();
		
		double initBal = econ.getBalance(e.getOwner());
		
		new BukkitRunnable() {
			public void run() {
				double finalBal = econ.getBalance(e.getOwner());
				
				getServer().getLogger().info("[StocksLogger] " + player + " sold " + amt + " shares of " + symbol + " for " + totPrice + "g total.");
				getServer().getLogger().info("               Initial balance = " + initBal + "");
				getServer().getLogger().info("               Final balance = " + finalBal + "");
			}
		}.runTaskLater(this, 10);
	}
	
	@EventHandler
	public void onStockSellAll(PlayerSellAllSharesEvent e) {
		String player = e.getOwner().getName();
		
		double initBal = econ.getBalance(e.getOwner());
		
		for(String symbol : e.getShares().keySet()) {
			double amt = e.getShares().get(symbol);
			double totPrice = e.getPrices().get(symbol);
			getServer().getLogger().info("[StocksLogger] " + player + " sold " + amt + " shares of " + symbol + " for " + totPrice + "g total as part of a sellall.");
		}
		
		new BukkitRunnable() {
			public void run() {
				double finalBal = econ.getBalance(e.getOwner());
				
				getServer().getLogger().info("               Initial balance = " + initBal + "");
				getServer().getLogger().info("               Final balance = " + finalBal + "");
			}
		}.runTaskLater(this, 10);
	}
}
