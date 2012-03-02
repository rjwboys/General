package net.craftstars.general.util;

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;
import com.fernferret.allpay.ItemBank;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.option.Options;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;

public class EconomyManager {
	private final static double[] lastPrice = new double[1];
	private static GenericBank economy;
	private static AllPay allpay;
	private enum AccountStatus {FROZEN, INSUFFICIENT, SUFFICIENT, BYPASS}
	
	private EconomyManager() {}
	
	public static void setup() {
		try {
			allpay = new AllPay(General.plugin, "General [" + General.codename + "] ");
			economy = allpay.loadEconPlugin();
		} catch(Exception e) {
			General.logger.warn(LanguageText.LOG_ECONOMY_ERROR.value("msg", e.getMessage()), e);
			return;
		}
		if(Options.NO_ECONOMY.get()) General.logger.info(LanguageText.LOG_NO_ECONOMY.value());
		else if(economy instanceof ItemBank && Options.ECONOMY_ITEM.get() <= 0)
			General.logger.warn(LanguageText.LOG_MISSING_ECONOMY.value());
	}
	
	private static AccountStatus hasFunds(CommandSender sender, int quantity, String... permissions) {
		if(sender.hasPermission("general.no-money") || sender instanceof ConsoleCommandSender)
			return AccountStatus.BYPASS;
		Player player = (Player) sender;
		lastPrice[0] = 0.0;
		for(String permission : permissions)
			if(permission.startsWith("$"))
				lastPrice[0] += Double.parseDouble(permission.substring(1));
			else if(permission.startsWith("%"))
				lastPrice[0] *= Double.parseDouble(permission.substring(1)) / 100.0;
			else lastPrice[0] += Options.ECONOMY_COST(permission).get() * quantity;
		if(CommandBase.isFrozen(player)) return AccountStatus.FROZEN;
		if(economy.hasEnough(player, lastPrice[0], Options.ECONOMY_ITEM.get()))
			return AccountStatus.SUFFICIENT;
		return AccountStatus.INSUFFICIENT;
	}
	
	public static boolean canPay(CommandSender sender, int quantity, String... permissions) {
		if(Options.NO_ECONOMY.get()) return true;
		if(sender instanceof ConsoleCommandSender) return true;
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		// Don't want the price to change between checking for funds and removing them!
		synchronized(lastPrice) {
			AccountStatus canPay = hasFunds(player, quantity, permissions);
			switch(canPay) {
			case BYPASS:
				break;
			case FROZEN:
				Messaging.showCost(player);
				return false;
			case INSUFFICIENT:
				Messaging.send(sender, LanguageText.ECONOMY_INSUFFICIENT);
				return false;
			case SUFFICIENT: // TODO: I think take() prints its own message, so this may cause double messages
				Messaging.showPayment(player);
				economy.take(player, lastPrice[0], Options.ECONOMY_ITEM.get());
			}
			return true;
		}
	}

	public static double sellItem(ItemID item, int amount) {
		if(Options.NO_ECONOMY.get()) return 0;
		String node = "economy.give.item" + item.econName();
		double percent = Options.ECONOMY_SELL.get() / 100.0;
		return Options.ECONOMY_COST(node).get() * amount * percent;
	}

	public static void giveMoney(Player who, double revenue) {
		if(Options.NO_ECONOMY.get()) return;
		economy.give(who, revenue, Options.ECONOMY_ITEM.get());
	}
	
	public static double getLastPrice() {
		return lastPrice[0];
	}
	
	public static String formatCost(Player player, double price) {
		return economy.getFormattedAmount(player, price, Options.ECONOMY_ITEM.get());
	}

	public static double getBalance(Player sender) {
		return economy.getBalance(sender, Options.ECONOMY_ITEM.get());
	}
}
