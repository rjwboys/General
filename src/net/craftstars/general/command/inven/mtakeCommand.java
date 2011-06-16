
package net.craftstars.general.command.inven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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

public class mtakeCommand extends CommandBase {
	private Player who;
	private ArrayList<ItemID> items = new ArrayList<ItemID>();
	
	public mtakeCommand(General instance) {
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
		ArrayList<ItemStack> inventItems = new ArrayList<ItemStack>(Arrays.asList(i.getContents()));
		for (ItemStack stk : inventItems) {
			int n, d;
			n = stk.getAmount();
			d = stk.getDurability();
			if(!items.contains(d)) continue;
			// if(!Items.isDamageable(item.getId()) && item.getData() != d) continue;
			i.setItem(inventItems.indexOf(stk), null);
		}
		/*for (ItemID item : items) {
			if(item.getData() != null){
				Messaging.broadcast("Data:"+item.getId()+":"+item.getData().toString());
				byte data = item.getData().byteValue();
				i.removeItem(new ItemStack(item.getId(), 1, (short)0, data));
			}else{
				i.removeItem(new ItemStack(item.getId(), 1, (short)0, null));
			}
		}*/
		Messaging.send(who, "&f" + (removed == 0 ? "All" : removed) + "&2 of &fvarious items&2 was taken from you.");
		return removed;
	}
}
