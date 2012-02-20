package net.craftstars.general.command.info;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.text.LanguageText;
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
			params.put("player", sender);
			break;
		case 1:
			try {
				int slot = Integer.parseInt(args[0]);
				if(!isPlayer) return null;
				if(slot < 0 || slot >= 40) {
					Messaging.send(sender, LanguageText.ITEMINFO_BAD_SLOT.value("slot", slot));
					return null;
				}
				params.put("item", ((Player)sender).getInventory().getItem(slot));
				params.put("player", sender);
			} catch(NumberFormatException e) {
				Player player = Toolbox.matchPlayer(args[0]);
				if(player == null) {
					Messaging.invalidPlayer(sender, args[0]);
					return null;
				}
				params.put("item", player.getItemInHand());
				params.put("player", player);
			}
			break;
		case 2:
			try {
				int slot = Integer.parseInt(args[0]);
				if(!isPlayer) return null;
				if(slot < 0 || slot >= 40) {
					Messaging.send(sender, LanguageText.ITEMINFO_BAD_SLOT.value("slot", slot));
					return null;
				}
				Player player = Toolbox.matchPlayer(args[1]);
				if(player == null) {
					Messaging.invalidPlayer(sender, args[1]);
					return null;
				}
				params.put("item", player.getInventory().getItem(slot));
				params.put("player", player);
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
		Player inspected = (Player)args.get("player");
		String permission = "general.iteminfo";
		if(!sender.equals(inspected)) permission = "general.iteminfo.other";
		if(!sender.hasPermission(permission))
			return Messaging.lacksPermission(sender, permission);
		ItemStack item = (ItemStack)args.get("item");
		Map<Enchantment, Integer> ench = item.getEnchantments();
		ItemID itemInfo = new ItemID(item);
		Messaging.send(sender, LanguageText.ITEMINFO_INFO.value("item", Items.name(item.getType()),
			"data", item.getDurability(), "amount", item.getAmount(), "name", itemInfo.getName(ench),
			"dataname", itemInfo.getDataType().getName(item.getDurability())));
		return true;
	}
	
}
