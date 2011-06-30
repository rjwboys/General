
package net.craftstars.general.command.inven;

import java.util.ArrayList;
import java.util.Arrays;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;

public class masstakeCommand extends CommandBase {
	private Player who;
	private ArrayList<ItemID> items = new ArrayList<ItemID>();
	
	public masstakeCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(args.length < 2) return SHOW_USAGE;
		
		who = Toolbox.matchPlayer(args[args.length-1]);
		if(who == null) return Messaging.invalidPlayer(sender, args[args.length-1]);
		else args[args.length-1] = null;
		for (String item : args) {
			if(item!=null){
				ItemID itemid = Items.validate(item);
				if(itemid != null && itemid.isIdValid() && itemid.isDataValid()) {
					this.items.add(itemid);
				}
			}
		}
		
		int amount = doTake();
		Messaging.send(sender, "&2Took &f" + amount + "&2 of &fvarious items&2 from &f" + who.getName()
				+ "&2!");
		return true;
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.take.mass"))
			return Messaging.lacksPermission(sender, "massively remove items from your inventory");
		if(args.length < 1) return SHOW_USAGE;
		
		who = Toolbox.matchPlayer(args[args.length-1]);
		if(who == null) who = sender;
		else args[args.length-1] = null;
		for (String item : args) {
			if(item!=null){
				ItemID itemid = Items.validate(item);
				if(itemid != null && itemid.isIdValid() && itemid.isDataValid()) {
					this.items.add(itemid);
				}
			}
		}
		
		if(!sender.equals(who) && (Toolbox.lacksPermission(sender, "general.take.other") || Toolbox.lacksPermission(sender, "general.take.mass")))
			return Messaging.lacksPermission(sender, "massively take items from someone else's inventory");
		
		int amount = doTake();
		if(!sender.getName().equalsIgnoreCase(who.getName()))
			Messaging.send(sender,
					"&2Took &f" + amount + "&2 of &fvarious items&2 from &f" + who.getName());
		return true;
	}
	
	private int doTake() {
		int removed = 0;
		PlayerInventory i = who.getInventory();
		ItemStack[] invenItems = i.getContents();
		for(int j = 0; j < invenItems.length; j++) {
			if(invenItems[j] == null) continue;
			int d = invenItems[j].getDurability();
			for(ItemID id : items)
				if(id.getId() == invenItems[j].getTypeId() && Items.dataEquiv(id, d))
					i.setItem(j, null);
		}
		Messaging.send(who, "&f" + (removed == 0 ? "All" : removed) + "&2 of &fvarious items&2 was taken from you.");
		return removed;
	}
}
