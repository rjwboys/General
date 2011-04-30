
package net.craftstars.general.money;

import org.bukkit.entity.Player;

public interface EconomyBase {
	boolean wasLoaded();
	
	String getVersion();
	
	String getName();
	
	double getBalance(Player who);
	
	String getBalanceForDisplay(Player who);
	
	String formatCost(double amount);
	
	boolean takePayment(Player fromWhom, double amount);
	
	void givePayment(Player toWhom, double amount);
	
	String getCurrency();
}
