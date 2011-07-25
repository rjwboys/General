
package net.craftstars.general.command.inven;

import java.util.HashMap;
import java.util.Map;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class clearCommand extends CommandBase {
	private enum CleanType {
		FULL("inventory"), QUICKBAR("quick-bar"), PACK("pack"), ARMOUR("armour"), EXCEPTARMOUR("inventory");
		private String name;
		
		private CleanType(String nm) {
			name = nm;
		}
		
		public String getName() {
			return name;
		}
		
		public static CleanType fromName(String name) {
			if(name.equalsIgnoreCase("pack")) {
				return CleanType.PACK;
			} else if(name.equalsIgnoreCase("quickbar")) {
				return CleanType.QUICKBAR;
			} else if(Toolbox.equalsOne(name, "armour", "armor")) {
				return CleanType.ARMOUR;
			} else if(Toolbox.equalsOne(name, "exceptarmour", "exceptarmor", "pack&quickbar", "inven", "inventory")) {
				return CleanType.ARMOUR;
			} else if(Toolbox.equalsOne(name, "all", "full")) {
				return CleanType.FULL;
			}
			return null;
		}
	}

	public clearCommand(General instance) {
		super(instance);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		switch(args.length) {
		case 0:
			if(!isPlayer) return null;
			params.put("player", sender);
			params.put("option", CleanType.EXCEPTARMOUR);
		break;
		case 1:
			CleanType type = null;
			if(isPlayer) {
				type = CleanType.fromName(args[0]);
				if(type != null) {
					params.put("option", type);
					params.put("player", sender);
				}
			}
			if(type == null && !parseOther(sender, args[0], CleanType.EXCEPTARMOUR, params)) return null;
		break;
		case 2:
			if(!parseOther(sender, args[0], CleanType.fromName(args[1]), params)) return null;
		break;
		default:
			return null;
		}
		return params;
	}
	
	private boolean parseOther(CommandSender sender, String name, CleanType option, HashMap<String,Object> params) {
		if(option == null) {
			Messaging.send(sender, "Invalid option.");
			return false;
		} 
		Player who = Toolbox.matchPlayer(name);
		if(who == null) {
			Messaging.invalidPlayer(sender, name);
			return false;
		}
		params.put("player", who);
		params.put("option", option);
		return true;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.clear"))
			return Messaging.lacksPermission(sender, "clear your inventory");
		boolean sell = General.plugin.economy != null;
		sell = sell && General.plugin.config.getString("economy.give.sell", "sell").equalsIgnoreCase("sell");
		Player player = (Player) args.get("player");
		CleanType option = (CleanType) args.get("option");
		doClean(player, sender, option, sell);
		return true;
	}
	
	private boolean doClean(Player who, CommandSender fromWhom, CleanType howMuch, boolean sell) {
		boolean selfClear = false;
		double revenue = 0.0;
		if(fromWhom instanceof Player) {
			if(((Player) fromWhom).getName().equalsIgnoreCase(who.getName())) selfClear = true;
		}
		if(!selfClear && Toolbox.lacksPermission(fromWhom, "general.clear.other"))
			return Messaging.lacksPermission(fromWhom, "clear someone else's inventory");
		sell = sell && selfClear;
		PlayerInventory i = who.getInventory();
		switch(howMuch) {
		case FULL:
		case EXCEPTARMOUR:
			if(sell) for(ItemStack item : i.getContents())
				if(item != null) revenue += Toolbox.sellItem(new ItemID(item), item.getAmount());
			i.clear();
			if(howMuch == CleanType.EXCEPTARMOUR) break;
			// Case fallthrough intentional
		case ARMOUR:
			if(sell) for(ItemStack item : i.getArmorContents())
				if(item != null) revenue += Toolbox.sellItem(new ItemID(item), item.getAmount());
			clearArmour(i);
		break;
		case QUICKBAR:
			if(sell) for(int j = 0; j < 9; j++)
				if(i.getItem(j) != null) revenue += Toolbox.sellItem(new ItemID(i.getItem(j)), i.getItem(j).getAmount());
			clearQuickbar(i);
		break;
		case PACK:
			if(sell) for(int j = 9; j < i.getSize(); j++)
				if(i.getItem(j) != null) revenue += Toolbox.sellItem(new ItemID(i.getItem(j)), i.getItem(j).getAmount());
			clearPack(i);
		break;
		}
		if(selfClear) {
			Messaging.send(who, "&2You have cleared your " + howMuch.getName() + ".");
		} else {
			Messaging.send(who, "&2Your " + howMuch.getName() + " has been cleared.");
			Messaging.send(fromWhom, "&f" + who.getName() + "&2's " + howMuch.getName() + " has been cleared.");
		}
		if(sell) Messaging.earned(who, revenue);
		return true;
	}
	
	private void clearArmour(PlayerInventory i) {
		i.setBoots(null);
		i.setLeggings(null);
		i.setChestplate(null);
		i.setHelmet(null);
	}
	
	private void clearQuickbar(PlayerInventory i) {
		for(int j = 0; j < 9; j++)
			i.setItem(j, null);
	}
	
	private void clearPack(PlayerInventory i) {
		for(int j = 9; j < i.getSize(); j++)
			i.setItem(j, null);
	}
}
