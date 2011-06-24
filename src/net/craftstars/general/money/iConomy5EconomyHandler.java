package net.craftstars.general.money;

import net.craftstars.general.General;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;

public class iConomy5EconomyHandler implements EconomyBase {
	private boolean wasLoaded;
	private String version;
	
	public iConomy5EconomyHandler() {
		Plugin test = General.plugin.getServer().getPluginManager().getPlugin("iConomy");
		
		if(test != null) {
			if(!wasLoaded) {
				General.plugin.getServer().getPluginManager().enablePlugin(test);
				this.wasLoaded = true;
			}
			this.version = test.getDescription().getVersion();
			String majorRev = version.substring(0, version.indexOf('.'));
			if(Integer.valueOf(majorRev) < 5) {
				wasLoaded = false;
				General.logger.warn("Was looking for iConomy 5 but found iConomy 4 instead. Please either update iConomy, " +
						"or edit your config.yml to specify iConomy4.");
			}
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
		return "iConomy";
	}
	
	@Override
	public double getBalance(Player who) {
		String player = who.getName();
		if(!iConomy.hasAccount(player)) return 0;
		return iConomy.getAccount(player).getHoldings().balance();
		
	}
	
	@Override
	public String getBalanceForDisplay(Player who) {
		String player = who.getName();
		String balance = iConomy.format(player);
		if(balance == null) return "0";
		return balance;
		
	}
	
	@Override
	public String formatCost(double amount) {
		return iConomy.format(amount);
	}
	
	@Override
	public boolean takePayment(Player fromWhom, double amount) {
		String player = fromWhom.getName();
		if(!iConomy.hasAccount(player)) return false;
		Account acc = iConomy.getAccount(player);
		if(acc == null) return false;
		Holdings money = acc.getHoldings();
		if(!money.hasEnough(amount)) return false;
		money.subtract(amount);
		return true;
	}
	
	@Override
	public void givePayment(Player toWhom, double amount) {
		String player = toWhom.getName();
		Account acc = iConomy.getAccount(player);
		if(acc == null) return;
		Holdings money = acc.getHoldings();
		money.add(amount);
	}
	
	@Override
	public String getCurrency() {
		return formatCost(0.0).substring(3).trim();
	}
	
}
