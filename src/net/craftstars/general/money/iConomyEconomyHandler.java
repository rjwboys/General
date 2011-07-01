package net.craftstars.general.money;

import net.craftstars.general.General;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class iConomyEconomyHandler implements EconomyBase {
	EconomyBase actualEconomy;
	boolean wasLoaded;
	
	public iConomyEconomyHandler() {
		Plugin test = General.plugin.getServer().getPluginManager().getPlugin("iConomy");
		
		if(test != null) {
			String versionString = test.getDescription().getVersion();
			String majorRev = versionString.substring(0, versionString.indexOf('.'));
			int version = Integer.valueOf(majorRev);
			if(version < 5) {
				actualEconomy = new iConomy4EconomyHandler();
				wasLoaded = actualEconomy.wasLoaded();
			} else if(version < 6) {
				actualEconomy = new iConomy5EconomyHandler();
				wasLoaded = actualEconomy.wasLoaded();
			}
		} else wasLoaded = false;
	}
	
	@Override
	public boolean wasLoaded() {
		return wasLoaded;
	}
	
	@Override
	public String getVersion() {
		return actualEconomy.getVersion();
	}
	
	@Override
	public String getName() {
		return actualEconomy.getName();
	}
	
	@Override
	public double getBalance(Player who) {
		return actualEconomy.getBalance(who);
	}
	
	@Override
	public String getBalanceForDisplay(Player who) {
		return actualEconomy.getBalanceForDisplay(who);
	}
	
	@Override
	public String formatCost(double amount) {
		return actualEconomy.formatCost(amount);
	}
	
	@Override
	public boolean takePayment(Player fromWhom, double amount) {
		return actualEconomy.takePayment(fromWhom, amount);
	}
	
	@Override
	public void givePayment(Player toWhom, double amount) {
		actualEconomy.givePayment(toWhom, amount);
	}
	
	@Override
	public String getCurrency() {
		return actualEconomy.getCurrency();
	}
	
}
