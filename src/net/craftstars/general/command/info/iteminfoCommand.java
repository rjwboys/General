package net.craftstars.general.command.info;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

public class iteminfoCommand extends CommandBase {
	public iteminfoCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String,Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		Map<String,Object> params = new HashMap<String,Object>();
		switch(args.length) {
		case 0:
			if(!isPlayer) return null;
			params.put("item", ((Player)sender).getItemInHand());
			break;
		case 1:
			try {
				int slot = Integer.parseInt(args[0]);
				if(!isPlayer) return null;
				if(slot < 0 || slot >= 40) {
					// TODO: Print error
					return null;
				}
				params.put("item", ((Player)sender).getInventory().getItem(slot));
			} catch(NumberFormatException e) {
				Player player = Toolbox.matchPlayer(args[0]);
				if(player == null) {
					Messaging.invalidPlayer(sender, args[0]);
					return null;
				}
				params.put("item", player.getItemInHand());
			}
			break;
		case 2:
			try {
				int slot = Integer.parseInt(args[0]);
				if(!isPlayer) return null;
				if(slot < 0 || slot >= 40) {
					// TODO: Print error
					return null;
				}
				Player player = Toolbox.matchPlayer(args[1]);
				if(player == null) {
					Messaging.invalidPlayer(sender, args[1]);
					return null;
				}
				params.put("item", player.getInventory().getItem(slot));
			} catch(NumberFormatException e) {
				Messaging.invalidNumber(sender, args[0]);
				return null;
			}
			break;
		default:
			return null;
		}
		return params;
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String,Object> args) {
		if(!sender.hasPermission("general.iteminfo"))
			return Messaging.lacksPermission(sender, "general.iteminfo");
		ItemStack item = (ItemStack)args.get("item");
		Messaging.send(sender, item.getType().toString() + "@" + item.getDurability() + " x " + item.getAmount());
		return true;
	}
	
}
