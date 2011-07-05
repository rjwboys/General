
package net.craftstars.general.command.inven;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class itemsCommand extends CommandBase {
	public itemsCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(args.length < 2) return SHOW_USAGE;
		Player toWhom = Toolbox.matchPlayer(args[0]);
		if(toWhom == null) return Messaging.invalidPlayer(sender, args[0]);
		doGive(toWhom, sender, Arrays.copyOfRange(args, 1, args.length));
		return true;
	}
	
	private void doGive(Player toWhom, CommandSender sender, String[] items) {
		StringBuilder text = new StringBuilder("Giving &f");
		for(String item : items) {
			ItemID what = Items.validate(item);
			if(what == null || !what.isValid()) continue;
			if(!what.canGive(sender)) continue;
			if(!Toolbox.canPay(sender, 1, "economy.give.item" + item.toString())) continue;
			text.append(what.getName());
			text.append("&2, &f");
			Items.giveItem(toWhom, what, 1);
		}
		int lastComma = text.lastIndexOf("&2, &f");
		if(lastComma >= 0 && lastComma < text.length())
			text.delete(lastComma, text.length());
		if(toWhom == sender) {
			Messaging.send(sender, "&2Enjoy! " + text + "&f!");
		} else {
			Messaging.send(toWhom, "&2Enjoy the gift! " + text + "&f!");
			Messaging.send(sender, text + "&2 to &f" + toWhom.getName() + "&f!");
		}
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.give.mass"))
			return Messaging.lacksPermission(sender, "give many items at once");
		doGive(sender, sender, args);
		return true;
	}
	
}
