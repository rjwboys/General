package net.craftstars.general.money;

import net.craftstars.general.General;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import cosine.boseconomy.BOSEconomy;

public class BOSEconomyEconomyHandler implements EconomyBase {
	private BOSEconomy econ;
	private String version;
	private boolean wasLoaded;

	public BOSEconomyEconomyHandler() {
		Plugin test = General.plugin.getServer().getPluginManager().getPlugin("BOSEconomy");
		
		if(test != null) {
			if(this.econ == null) {
				General.plugin.getServer().getPluginManager().enablePlugin(test);
				this.econ = ((BOSEconomy) test);
				this.wasLoaded = true;
			}
			this.version = test.getDescription().getVersion();
		} else this.version = "0.0";
	}
	
	@Override
	public boolean wasLoaded() {
		return wasLoaded;
	}
	
	@Override
	public String getVersion() {
		return version;
	}
	
	@Override
	public String getName() {
		return "BOSEconomy";
	}
	
	@Override
	public double getBalance(Player who) {
		return econ.getPlayerMoney(who.getName());
	}
	
	@Override
	public String getBalanceForDisplay(Player who) {
		return getBalance(who) + " " + getCurrency();
	}
	
	@Override
	public String formatCost(double amount) {
		return amount + " " + getCurrency();
	}
	
	@Override
	public boolean takePayment(Player fromWhom, double amount) {
		String player = fromWhom.getName();
		if(!econ.playerRegistered(player, false)) return false;
		econ.addPlayerMoney(player, (int) -amount, false);
		return true;
	}
	
	@Override
	public void givePayment(Player toWhom, double amount) {
		econ.addPlayerMoney(toWhom.getName(), (int) amount, false);
	}
	
	@Override
	public String getCurrency() {
		return econ.getMoneyName();
	}
	
}
