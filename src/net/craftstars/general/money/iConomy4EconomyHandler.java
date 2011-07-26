
package net.craftstars.general.money;

import net.craftstars.general.General;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import com.nijiko.coelho.iConomy.system.Bank;

import net.craftstars.general.money.EconomyBase;
import net.craftstars.general.util.LanguageText;

@Deprecated
public class iConomy4EconomyHandler implements EconomyBase {
	Bank econ;
	private boolean wasLoaded;
	private String version;
	
	@SuppressWarnings("static-access")
	public iConomy4EconomyHandler() {
		Plugin test = General.plugin.getServer().getPluginManager().getPlugin("iConomy");
		
		if(test != null) {
			if(this.econ == null) {
				General.plugin.getServer().getPluginManager().enablePlugin(test);
				this.econ = ((iConomy) test).getBank();
				this.wasLoaded = true;
			}
			this.version = test.getDescription().getVersion();
			String majorRev = version.substring(0, version.indexOf('.'));
			if(Integer.valueOf(majorRev) > 4) {
				wasLoaded = false;
				General.logger.warn(LanguageText.LOG_ICONOMY_5NOT4.value());
			}
		} else this.version = "0.0";
	}
	
	@Override
	public String formatCost(double amount) {
		return econ.format(amount);
	}
	
	@Override
	public double getBalance(Player who) {
		String player = who.getName();
		if(!econ.hasAccount(player)) return 0;
		return econ.getAccount(player).getBalance();
	}
	
	@Override
	public String getBalanceForDisplay(Player who) {
		String player = who.getName();
		if(!econ.hasAccount(player)) return formatCost(0);
		return econ.format(who.getName());
	}
	
	@Override
	public String getName() {
		return "iConomy 4";
	}
	
	@Override
	public String getVersion() {
		return version;
	}
	
	@Override
	public void givePayment(Player toWhom, double amount) {
		String player = toWhom.getName();
		if(!econ.hasAccount(player)) return;
		Account bank = econ.getAccount(player);
		bank.add(amount);
	}
	
	@Override
	public boolean takePayment(Player fromWhom, double amount) {
		String player = fromWhom.getName();
		if(!econ.hasAccount(player)) return false;
		Account bank = econ.getAccount(player);
		if(!bank.hasEnough(amount)) return false;
		bank.subtract(amount);
		return true;
	}
	
	@Override
	public boolean wasLoaded() {
		return wasLoaded;
	}
	
	@Override
	public String getCurrency() {
		return econ.getCurrency();
	}
	
}
