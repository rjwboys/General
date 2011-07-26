
package net.craftstars.general.command.inven;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Kit kit;
		switch(args.length) {
		case 0:
			setCommand("listkits");
			return params;
		case 1:
			if(!isPlayer) return null;
			kit = Kits.kits.get(args[0]);
			if(kit == null) {
				Messaging.send(sender, "&cKit by the name of &e" + args[0] + "&c does not exist!");
				return null;
			}
			params.put("player", sender);
		break;
		case 2:
			kit = Kits.kits.get(args[0]);
			if(kit == null) {
				Messaging.send(sender, "&cKit by the name of &e" + args[0] + "&c does not exist!");
				return null;
			}
			Player who = Toolbox.matchPlayer(args[1]);
			if(who == null) {
				Messaging.invalidPlayer(sender, args[1]);
				return null;
			}
			params.put("player", who);
		break;
		default:
			return null;
		}
		params.put("kit", kit);
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.kit"))
			return Messaging.lacksPermission(sender, "get kits");
		if(command.equals("listkits")) {
			String msg = "&cKits available: ";
			for(String thisKit : Kits.kits.keySet()) {
				if(Kits.kits.get(thisKit).canGet(sender)) {
					msg += thisKit + " ";
				}
			}
			Messaging.send(sender, msg);
		} else {
			Player who = (Player) args.get("player");
			Kit kit = (Kit) args.get("kit");
			if(!kit.canGet(who)) {
				Messaging.send(sender, "&rose;You do not have permission for that kit.");
				return true;
			}
			
			if(!kit.canAfford(who)) return true;
			
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
			getKit(who, kit);
		}
		return true;
	}
	
	private boolean canBypassDelay(CommandSender sender) {
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
