package me.neoblade298.neomonopoly.SpaceCards;

import org.bukkit.ChatColor;

import me.neoblade298.neomonopoly.Objects.Game;
import me.neoblade298.neomonopoly.Objects.GamePlayer;

public class Utility implements Property {
	private String name;
	private GamePlayer owner;
	private boolean isMortgaged;
	private int[] rent;
	private ChatColor color;
	private Game game;
	private int price;

	public Utility(String name, int[] rent, ChatColor color, Game game, int price) {
		this.owner = null;
		this.isMortgaged = false;
		this.name = name;
		this.rent = rent;
		this.color = color;
		this.game = game;
		this.price = price;
	}
	
	public GamePlayer getOwner() {
		return owner;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setOwner(GamePlayer owner) {
		this.owner = owner;
	}
	
	public boolean canMortgage() {
		return true;
	}

	public boolean isMortgaged() {
		return isMortgaged;
	}

	public void setMortgaged(boolean isMortgaged) {
		this.isMortgaged = isMortgaged;
	}

	public int[] getRent() {
		return rent;
	}

	public void setRent(int[] rent) {
		this.rent = rent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public char getMapChar() {
		return 'x';
	}

	@Override
	public void onLand(GamePlayer lander, int dice) {
		// Auction or purchase
		if (owner == null) {
			game.requiredActions.get(lander).add(0, "UNOWNED_SPACE");
			game.broadcast("&7This space is unowned! You may buy it with &c/mono buy &7or auction it with &c/mono auction&7.");
		}
		else {
			if (!owner.equals(lander)) {
				if (isMortgaged) {
					game.broadcast("&7This space is mortgaged! No rent needed.");
				}
				else {
					game.billPlayer(lander, calculateRent(dice), owner);
				}
			}
		}
		game.isBusy = false;
	}
	
	public void onRNGLand(GamePlayer lander) {
		// Auction or purchase
		if (owner == null) {
			game.requiredActions.get(lander).add(0, "UNOWNED_SPACE");
			game.broadcast("&7This space is unowned! You may buy it with &c/mono buy &7or auction it with &c/mono auction&7.");
		}
		else {
			if (!owner.equals(lander)) {
				if (isMortgaged) {
					game.broadcast("&7This space is mortgaged! No rent needed.");
				}
				else {
					game.requiredActions.get(lander).add(0, "ROLL_PAY");
				}
			}
		}
		game.isBusy = false;
	}
	
	@Override
	public void onStart(GamePlayer starter) {
		game.requiredActions.get(starter).add("ROLL_MOVE");
	}

	@Override
	public ChatColor getColor() {
		return color;
	}

	@Override
	public void setColor(ChatColor color) {
		this.color = color;
	}

	@Override
	public void onOwned(GamePlayer owner) {
		owner.setNumUtilities(owner.getNumUtilities() + 1);
		game.broadcast("&e" + owner + " &7now owns &e" + owner.getNumUtilities() + " &7utilities.");
	}

	@Override
	public void onUnowned(GamePlayer formerOwner) {
		owner.setNumUtilities(owner.getNumUtilities() - 1);
		game.broadcast("&e" + formerOwner + " &7now owns &e" + owner.getNumUtilities() + " &7utilities.");
	}

	@Override
	public void onBankrupt(GamePlayer formerOwner) {
		return;
	}

	@Override
	public Game getGame() {
		return this.game;
	}

	@Override
	public void setGame(Game game) {
		this.game = game;
	}
	
	@Override
	public void displayProperty(GamePlayer gp) {
		String ownerName = owner == null ? "Unowned" : owner.toString();
		gp.message("&7[" + color + name + "&7 (" + ownerName + "&7)]");
		gp.message("&7Value: &e" + price);
		if (!isMortgaged) {
			String msg = new String();
			for (int i = 1; i <= 2; i++) {
				if (owner != null && owner.getNumUtilities() == i) {
					msg += "&e";
				}
				else {
					msg += "&7";
				}
				if (i == 0) {
					msg += rent[i - 1] + "x dice roll&7, ";
				}
				else {
					msg += rent[i - 1] + "x dice roll";
				}
			}
			gp.message("&eRent&7: " + msg);
		}
		else {
			gp.message("&cCurrently mortgaged. Price to unmortgage: &e" + price);
		}
	}
	
	@Override
	public String getShorthand(GamePlayer gp) {
		String ownerName = owner == null ? "&a$" + price : owner.toString();
		if (isMortgaged) {
			return "&7&m[" + color + "&m" + name + "&7&m (" + ownerName + "&7&m)]&7";
		}
		else {
			return "&7[" + color + name + "&7 (" + ownerName + "&7)]";
		}
	}
	
	@Override
	public String getColoredName() {
		if (isMortgaged) {
			return "&7&m[" + color + "&m" + name + "&7&m)]&7";
		}
		else {
			return "&7[" + color + name + "&7)]";
		}
	}
	
	@Override
	public String listComponent() {
		if (isMortgaged) {
			return "&7&m[" + color + "&m" + name + "&7&m] Rent: &e&m" + rent[owner.getNumUtilities() - 1] + "x dice roll";
		}
		else {
			return "&7[" + color + name + "&7] Rent: &e" + rent[owner.getNumUtilities() - 1] + "x dice roll";
		}
	}

	@Override
	public int calculateRent(int dice) {
		return rent[owner.getNumUtilities() - 1] * dice;
	}
}
