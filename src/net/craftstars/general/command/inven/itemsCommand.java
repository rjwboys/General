
package net.craftstars.general.command.inven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class itemsCommand extends CommandBase {
	public itemsCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.give.mass"))
			return Messaging.lacksPermission(sender, "general.give.mass");
		Player toWhom = (Player) args.get("player");
		String[] items = (String[]) args.get("items");
		//StringBuilder text = new StringBuilder("Giving &f");
		ArrayList<String> display = new ArrayList<String>();
		for(String item : items) {
			ItemID what = Items.validate(item);
			if(what == null || !what.isValid()) continue;
			if(!what.canGive(sender)) continue;
			if(!Toolbox.canPay(sender, 1, "economy.give.item" + item.toString())) continue;
			display.add(what.getName());
			Items.giveItem(toWhom, what, 1);
		}
		String text = Toolbox.join(display.toArray(new String[0]), "&2, &f");
		if(toWhom == sender) {
			Messaging.send(sender, LanguageText.ITEMS_ENJOY.value("items", text));
		} else {
			Messaging.send(toWhom, LanguageText.ITEMS_GIFTED.value("items", text));
			Messaging.send(sender, LanguageText.ITEMS_GIFT.value("items", text, "player", toWhom.getName()));
		}
		return true;
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length < (isPlayer ? 1 : 2)) return null;
		Player target = null;
		if(args.length > 1) {
			String name = args[args.length-1];
			target = Toolbox.matchPlayer(name);
			if(target != null) args = dropLastArg(args);
		}
		if(target == null && isPlayer) target = (Player) sender;
		if(target == null) return null;
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("player", target);
		params.put("items", args);
		return params;
	}
}
