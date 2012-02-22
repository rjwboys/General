
package net.craftstars.general.command.inven;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.items.InvalidItemException;
import net.craftstars.general.items.ItemData;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

public class giveCommand extends CommandBase {
	public giveCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String, Object> parse(CommandSender sender, Command cmd, String label, String[] args, boolean isPlayer) {
		HashMap<String,Object> params = new HashMap<String,Object>();
		Player who = null;
		ItemID item;
		int amount = 1;
		
		// Split the arguments into two parts based on whether they contain an equals sign.
		int splitAt = args.length;
		while(args[--splitAt].contains("="));
		splitAt++;
		int enchLen = args.length - splitAt;
		String[] enchArgs = new String[enchLen];
		Toolbox.arrayCopy(args, splitAt, enchArgs, 0, enchLen);
		
		switch(splitAt) {
		case 1: // /give <item>[:<data>]
			if(!isPlayer) return null;
			item = Items.validate(args[0]);
			who = (Player) sender;
		break;
		case 2: // /give <item>[:<data>] <amount> OR /give <item>[:<data>] <player>
			item = Items.validate(args[0]);
			if(isPlayer) try {
				amount = Integer.valueOf(args[1]);
				who = (Player) sender;
			} catch(NumberFormatException x) {
				Messaging.send(sender, LanguageText.GIVE_BAD_AMOUNT);
			}
			if(who == null) {
				who = Toolbox.matchPlayer(args[1]);
				if(who == null) {
					Messaging.invalidPlayer(sender, args[1]);
					return null;
				}
			}
		break;
		case 3: // /give <item>[:<data>] <amount> <player> OR /give <player> <item>[:<data>] <amount>
			try {
				amount = Integer.valueOf(args[2]);
				who = Toolbox.matchPlayer(args[0]);
				if(who == null) {
					Messaging.invalidPlayer(sender, args[0]);
					return null;
				}
				item = Items.validate(args[1]);
			} catch(NumberFormatException ex) {
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
			}
		break;
		default:
			return null;
		}
		Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		ItemData data = ItemData.enchanting(item.getMaterial());
		for(String ench : enchArgs) {
			String[] split = ench.split("=");
			int id = data.fromName(split[0]);
			Enchantment magic = Enchantment.getById(id);
			if(!data.validate(id)) throw new InvalidItemException(LanguageText.GIVE_BAD_ENCH,
				"item", item.getName(null), "ench", split[0]);
			int power;
			try {
				power = Integer.parseInt(split[1]);
			} catch(IndexOutOfBoundsException e) {
				power = magic.getMaxLevel();
			} catch(NumberFormatException e) {
				throw new InvalidItemException(e, LanguageText.GIVE_BAD_LEVEL, "level", split[1], ench, magic.getName());
			}
			if(power == 0) power = magic.getMaxLevel();
			enchantments.put(magic, power);
		}
		// Fill params and go!
		params.put("player", who);
		params.put("item", item);
		params.put("amount", amount);
		params.put("ench", enchantments);
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(!sender.hasPermission("general.give"))
			return Messaging.lacksPermission(sender, "general.give");
		Player who = (Player) args.get("player");
		if(!who.equals(sender) && !sender.hasPermission("general.give.other"))
			return Messaging.lacksPermission(sender, "general.give.other");
		int amount = (Integer) args.get("amount");
		if(amount < 0 && !sender.hasPermission("general.give.infinite"))
			return Messaging.lacksPermission(sender, "general.give.infinite");
		int maxAmount = Option.GIVE_MASS.get();
		if(amount > maxAmount && !sender.hasPermission("general.give.mass"))
			return Messaging.lacksPermission(sender, "general.give.mass");
		ItemID item = (ItemID) args.get("item");
		// Make sure this player is allowed this particular item
		if(!item.canGive(sender)) return true;
		// Make sure the player has enough money for this item
		if(!EconomyManager.canPay(sender, amount, "economy.give.item" + item.toString())) return true;
		@SuppressWarnings("unchecked")
		Map<Enchantment, Integer> enchantments = (Map<Enchantment,Integer>)args.get("ench");
		
		boolean isGift = !who.equals(sender);
		doGive(who, item, amount, isGift, enchantments);
		if(isGift) {
			Messaging.send(sender, LanguageText.GIVE_GIFT.value("amount", amount, "item", item.getName(enchantments),
				"player", who.getName()));
		}
		return true;
	}
	
	private void doGive(Player who, ItemID item, int amount, boolean isGift, Map<Enchantment,Integer> ench) {
		if(amount == 0) { // give one stack
			amount = Items.maxStackSize(item.getId());
		}
		
		Items.giveItem(who, item, amount, ench);
		LanguageText format = isGift ? LanguageText.GIVE_GIFTED : LanguageText.GIVE_ENJOY;
		Messaging.send(who, format.value("item", item.getName(ench), "amount", amount));
	}
}
