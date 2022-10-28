package me.fopzl.stockslogger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.tchristofferson.stocks.api.events.PlayerBuySharesEvent;
import com.tchristofferson.stocks.api.events.PlayerSellAllSharesEvent;
import com.tchristofferson.stocks.api.events.PlayerSellSharesEvent;

public class StocksLogger extends JavaPlugin implements Listener {
	public void onEnable() {
		super.onEnable();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onStockBuy(PlayerBuySharesEvent e) {
		String player = e.getOwner().getName();
		double amt = e.getShares();
		String symbol = e.getStock().getSymbol();
		double price = e.getPrice();
		getServer().getLogger().info("[StocksLogger] " + player + " bought " + amt + " shares of " + symbol + " at " + price + "g each.");
	}
	
	@EventHandler
	public void onStockSell(PlayerSellSharesEvent e) {
		String player = e.getOwner().getName();
		double amt = e.getShares();
		String symbol = e.getStock().getSymbol();
		double price = e.getPrice();
		getServer().getLogger().info("[StocksLogger] " + player + " sold " + amt + " shares of " + symbol + " at " + price + "g each.");
	}
	
	@EventHandler
	public void onStockSellAll(PlayerSellAllSharesEvent e) {
		String player = e.getOwner().getName();
		for(String symbol : e.getShares().keySet()) {
			double amt = e.getShares().get(symbol);
			double price = e.getPrices().get(symbol);
			getServer().getLogger().info("[StocksLogger] " + player + " sold " + amt + " shares of " + symbol + " at " + price + "g each.");
		}
	}
}
