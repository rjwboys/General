
package net.craftstars.general.command.inven;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.*;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
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
			kit = Kits.get(args[0]);
			if(kit == null) {
				Messaging.send(sender, LanguageText.KIT_INVALID.value("kit", args[0]));
				return null;
			}
			params.put("player", sender);
		break;
		case 2:
			kit = Kits.get(args[0]);
			if(kit == null) {
				Messaging.send(sender, LanguageText.KIT_INVALID.value("kit", args[0]));
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
		if(command.equals("listkits")) {
			String msg = LanguageText.KIT_LIST.value();
			boolean foundKit = false;
			for(Kit thisKit : Kits.all()) {
				if(thisKit.canGet(sender)) {
					msg += thisKit + " ";
					foundKit = true;
				}
			}
			if(foundKit) Messaging.send(sender, msg);
			else return Messaging.lacksPermission(sender, "general.kit");
		} else {
			Player who = (Player) args.get("player");
			Kit kit = (Kit) args.get("kit");
			if(!kit.canGet(who)) return true;
			if(!kit.canAfford(who)) return true;
			
			// Initiate cooldown unless they have bypass permission
			String cooldownPerm = kit.getPermission();
			if(Toolbox.inCooldown(sender, cooldownPerm))
				return Messaging.inCooldown(sender, cooldownPerm, LanguageText.COOLDOWN_KIT, "kit", kit.getName());
			Toolbox.cooldown(sender, cooldownPerm, cooldownPerm + ".instant", kit.delay);
			
			// Receive the kit
			getKit(who, kit);
		}
		return true;
	}
	
	private void getKit(Player sender, Kit kit) {
		for(ItemID x : kit) {
			Items.giveItem(sender, x, kit.get(x));
		}
		Messaging.send(sender, LanguageText.KIT_GIVE.value("kit", kit.getName()));
	}
}
