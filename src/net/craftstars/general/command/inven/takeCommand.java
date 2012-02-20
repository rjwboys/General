
package net.craftstars.general.command.inven;

import java.util.HashMap;
import java.util.Map;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class takeCommand extends CommandBase {
	public takeCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String,Object> params = new HashMap<String,Object>();
		Player who = null;
		ItemID item = null;
		int amount = 0;
		switch(args.length) {
		case 1: // /take <item>[:<data>]
			if(!isPlayer) return null;
			who = (Player) sender;
			item = Items.validate(args[0]);
		break;
		case 2: // /take <item>[:<data>] <amount> OR /take <item>[:<data>] <player>
			item = Items.validate(args[0]);
			who = Toolbox.matchPlayer(args[1]);
			if(isPlayer) try {
				amount = Integer.valueOf(args[1]);
				who = (Player) sender;
			} catch(NumberFormatException x) {
				Messaging.send(sender, LanguageText.GIVE_BAD_AMOUNT);
			}
			if(who == null) {
				Messaging.invalidPlayer(sender, args[1]);
				return null;
			}
		break;
		case 3: // /take <item>[:<data>] <amount> <player>
			who = Toolbox.matchPlayer(args[2]);
			if(who == null) {
				Messaging.invalidPlayer(sender, args[2]);
				return null;
			}
			item = Items.validate(args[0]);
			try {
				amount = Integer.valueOf(args[1]);
			} catch(NumberFormatException x) {
				Messaging.send(sender, LanguageText.GIVE_BAD_AMOUNT);
				return null;
			}
		break;
		default:
			return null;
		}
		// Fill in params and go!
		params.put("player", who);
		params.put("item", item);
		params.put("amount", amount);
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(!sender.hasPermission("general.take"))
			return Messaging.lacksPermission(sender, "general.take");
		Player who = (Player) args.get("player");
		ItemID item = (ItemID) args.get("item");
		int amount = (Integer) args.get("amount");
		boolean sell = who.equals(sender);
		if(!sell && !sender.hasPermission("general.take.other"))
			return Messaging.lacksPermission(sender, "general.take.other");
		sell = sell && Option.NO_ECONOMY.get();
		sell = sell && Option.ECONOMY_TAKE_SELL.get().equalsIgnoreCase("sell");
		amount = doTake(who, item, amount, sell);
		if(!sender.equals(who)) Messaging.send(sender, LanguageText.TAKE_THEFT.value("item", item.getName(null),
			"amount", amount, "player", who.getName()));
		return true;
	}
	
	private int doTake(Player who, ItemID item, int amount, boolean sell) {
		int removed = 0;
		PlayerInventory i = who.getInventory();
		Map<Integer, ? extends ItemStack> items = i.all(item.getId());
		for(int x : items.keySet()) {
			ItemStack stk = items.get(x);
			int n, d;
			n = stk.getAmount();
			d = stk.getDurability();
			if(!Items.dataEquiv(item, d)) continue;
			if(amount > 0 && n > amount) {
				stk.setAmount(n - amount);
				removed += amount;
				amount = 0;
				break;
			} else if(n <= amount) {
				amount -= n;
				removed += n;
				i.setItem(x, null);
			} else if(amount <= 0) i.setItem(x, null);
		}
		Messaging.send(who, LanguageText.TAKE_TOOK.value("item", item.getName(null),
			"amount", removed <= amount ? removed : 0));
		if(sell) {
			double revenue = EconomyManager.sellItem(item, removed);
			EconomyManager.giveMoney(who, revenue);
			Messaging.earned(who, revenue);
		}
		return removed;
	}
}
