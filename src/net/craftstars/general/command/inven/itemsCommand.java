
package net.craftstars.general.command.inven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.InvalidItemException;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Toolbox;

public class itemsCommand extends CommandBase {
	public itemsCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(!sender.hasPermission("general.give.mass"))
			return Messaging.lacksPermission(sender, "general.give.mass");
		Player toWhom = (Player) args.get("player");
		ArrayList<String> display = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		ArrayList<ItemID> items = (ArrayList<ItemID>)args.get("items");
		@SuppressWarnings("unchecked")
		ArrayList<String> bad = (ArrayList<String>)args.get("bad");
		for(ItemID item : items) {
			if(!item.canGive(sender)) continue;
			if(!EconomyManager.canPay(sender, 1, "economy.give.item" + item.getMaterial().toString())) continue;
			display.add(item.getName(null));
			Items.giveItem(toWhom, item, item.getId() == ItemID.EXP ? Toolbox.toNextLevel(toWhom) : 1, null);
		}
		String text = Toolbox.join(display.toArray(new String[0]), LanguageText.ITEMS_JOINER.value());
		if(toWhom == sender) {
			Messaging.send(sender, LanguageText.ITEMS_ENJOY.value("items", text));
		} else {
			Messaging.send(toWhom, LanguageText.ITEMS_GIFTED.value("items", text));
			Messaging.send(sender, LanguageText.ITEMS_GIFT.value("items", text, "player", toWhom.getName()));
		}
		if(!bad.isEmpty()) Messaging.send(sender, LanguageText.LIST_BAD_ITEMS.value("items", bad.toString()));
		return true;
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length < (isPlayer ? 1 : 2)) return null;
		Player target = null;
		if(args.length > 2 && args[args.length-2].equals("->")) {
			String name = args[args.length-1];
			target = Toolbox.matchPlayer(name);
			if(target == null) {
				Messaging.invalidPlayer(sender, name);
				return null;
			}
			args = dropLastArg(dropLastArg(args));
		}
		if(target == null && isPlayer) target = (Player) sender;
		if(target == null) return null;
		ArrayList<ItemID> items = new ArrayList<ItemID>();
		ArrayList<String> bad = new ArrayList<String>();
		for(String item : args) {
			if(item != null){
				ItemID itemid;
				try {
					itemid = Items.validate(item);
					items.add(itemid);
				} catch(InvalidItemException e) {
					bad.add(item);
				}
			}
		}
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("player", target);
		params.put("items", items);
		params.put("bad", bad);
		return params;
	}
}
