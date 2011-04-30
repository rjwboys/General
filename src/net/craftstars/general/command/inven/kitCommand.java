
package net.craftstars.general.command.inven;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;
import net.craftstars.general.items.Kits;
import net.craftstars.general.items.Kits.Kit;
import net.craftstars.general.items.Kits.GotKit;

public class kitCommand extends CommandBase {
	
	@Override
	public boolean fromConsole(General plugin, CommandSender sender, Command command, String commandLabel,
			String[] args) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(plugin, sender, "get kits", "general.kit")) return true;
		if(args.length == 0) {
			String msg = "&cKits available: ";
			for(String thisKit : Kits.kits.keySet()) {
				if(canGetKit(sender, thisKit)) {
					msg += thisKit + " ";
				}
			}
			Messaging.send(sender, msg);
		} else if(args.length >= 1) {
			Kit kit = Kits.kits.get(args[0]);
			if(kit == null)
				Messaging.send(sender, "&cKit by the name of &e" + args[0] + "&c does not exist!");
			else {
				if(!canGetKit(sender, args[0].toLowerCase())) {
					Messaging.send(sender, "&rose;You do not have permission for that kit.");
					return true;
				}
				
				GotKit check = new GotKit(sender, kit);
				
				// Player did not request any kit previously
				if(!canBypassDelay(sender) && Kits.players.containsKey(check)) {
					long time = System.currentTimeMillis() / 1000;
					long left = kit.delay - (time - Kits.players.get(check));
					
					// Time did not expire yet
					if(left > 0) {
						Messaging.send(sender, "&cYou may not get this kit so soon! Try again in &e" + left
								+ "&c seconds.");
						return true;
					}
				}
				// Add the kit and timestamp into the list
				insertIntoPlayerList(check);
				// Receive the kit
				getKit(sender, kit);
			}
		}
		return true;
	}
	
	private boolean canGetKit(Player sender, String kit) {
		if(General.plugin.permissions.hasPermission(sender, "general.kit." + kit)) return true;
		return false;
	}
	
	private boolean canBypassDelay(Player sender) {
		return General.plugin.permissions.hasPermission(sender, "general.kit-now");
	}
	
	private void insertIntoPlayerList(GotKit what) {
		long time = System.currentTimeMillis() / 1000;
		Kits.players.put(what, time);
	}
	
	private void getKit(Player sender, Kit kit) {
		HashMap<ItemID, Integer> items = kit.items;
		for(ItemID x : items.keySet()) {
			Items.giveItem(sender, x, items.get(x));
		}
		Messaging.send(sender, "&2Here you go!");
	}
}
