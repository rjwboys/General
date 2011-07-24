
package net.craftstars.general.command.inven;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.*;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class kitCommand extends CommandBase {
	public kitCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel, String[] args) {
		// TODO: Implement for console
		Messaging.send(sender, "Sorry, kits from the console are not available at the moment.");
		return true;
	}

	@Override
	public boolean fromUnknown(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.hasPermission(sender, "general.kit") || sender.isOp()) {
			// TODO: Implement for console
			Messaging.send(sender, "Sorry, kits from the console are not available at the moment.");
		}
		return true;
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.kit"))
			return Messaging.lacksPermission(sender, "get kits");
		if(args.length == 0) {
			String msg = "&cKits available: ";
			for(String thisKit : Kits.kits.keySet()) {
				if(Kits.kits.get(thisKit).canGet(sender)) {
					msg += thisKit + " ";
				}
			}
			Messaging.send(sender, msg);
		} else if(args.length >= 1) {
			Kit kit = Kits.kits.get(args[0]);
			if(kit == null)
				Messaging.send(sender, "&cKit by the name of &e" + args[0] + "&c does not exist!");
			else {
				if(!kit.canGet(sender)) {
					Messaging.send(sender, "&rose;You do not have permission for that kit.");
					return true;
				}
				
				if(!kit.canAfford(sender)) return true;
				
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
	
	private boolean canBypassDelay(Player sender) {
		return Toolbox.hasPermission(sender, "general.kit-now");
	}
	
	private void insertIntoPlayerList(GotKit what) {
		long time = System.currentTimeMillis() / 1000;
		Kits.players.put(what, time);
	}
	
	private void getKit(Player sender, Kit kit) {
		for(ItemID x : kit) {
			Items.giveItem(sender, x, kit.get(x));
		}
		Messaging.send(sender, "&2Here you go!");
	}
}
