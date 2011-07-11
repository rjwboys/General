
package net.craftstars.general.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.items.Kit;
import net.craftstars.general.items.Kits;
import net.craftstars.general.util.HelpHandler;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class generalCommand extends CommandBase {
	public generalCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(commandLabel.equalsIgnoreCase("help")) {
			args = prependArg(args, "help");
			commandLabel = "general";
		} else if(commandLabel.equalsIgnoreCase("motd")) {
			args = prependArg(args, "motd");
			commandLabel = "general";
		}
		if(args.length < 1) return SHOW_USAGE;
		if(args[0].equalsIgnoreCase("reload")) {
			doReload(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("help")) {
			if(args.length == 1) {
				HelpHandler.showHelp(sender, "console.help");
				return true;
			} else if(args.length == 2) {
				HelpHandler.showHelp(sender, args[1] + ".help");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("motd")) {
			MessageOfTheDay.showMotD(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("item")) {
			if(args.length < 3) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return itemEdit(sender, Arrays.copyOfRange(args, 1, args.length));
		} else if(args[0].equalsIgnoreCase("cost")) {
			sender.sendMessage("Currently you need to be a player to check the cost of a command.");
		} else if(args[0].equalsIgnoreCase("restrict")) {
			if(args.length < 2) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return doPermissions(sender, args[1], true);
		} else if(args[0].equalsIgnoreCase("release")) {
			if(args.length < 2) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return doPermissions(sender, args[1], false);
		} else if(args[0].equalsIgnoreCase("kit")) {
			if(args.length < 3) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return kitEdit(sender, Arrays.copyOfRange(args, 1, args.length));
		} else if(args[0].equalsIgnoreCase("economy")) {
			if(args.length < 3) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return setEconomy(sender, Arrays.copyOfRange(args, 1, args.length));
		} else if(args[0].equalsIgnoreCase("set")) {
			if(args.length < 3) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return setVar(sender, Arrays.copyOfRange(args, 1, args.length));
		}
		return SHOW_USAGE;
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(commandLabel.equalsIgnoreCase("help")) {
			args = prependArg(args, "help");
			commandLabel = "general";
		} else if(commandLabel.equalsIgnoreCase("motd")) {
			args = prependArg(args, "motd");
			commandLabel = "general";
		}
		if(args.length < 1) return SHOW_USAGE;
		if(args[0].equalsIgnoreCase("reload")) {
			if(Toolbox.lacksPermission(sender, "general.admin.reload"))
				return Messaging.lacksPermission(sender, "administrate the General plugin");
			doReload(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("help")) {
			if(args.length == 1) {
				HelpHandler.showHelp(sender, "player.help");
				return true;
			} else if(args.length == 2) {
				HelpHandler.showHelp(sender, args[1] + ".help");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("motd")) {
			MessageOfTheDay.showMotD(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("item")) {
			if(Toolbox.lacksPermission(sender, "general.admin.item"))
				return Messaging.lacksPermission(sender, "administrate the General plugin");
			if(args.length < 3) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return itemEdit(sender, Arrays.copyOfRange(args, 2, args.length));
		} else if(args[0].equalsIgnoreCase("save")) {
			if(Toolbox.lacksPermission(sender, "general.admin.save"))
				return Messaging.lacksPermission(sender, "administrate the General plugin");
			doSave(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("cost")) {
			String check = Toolbox.combineSplit(args, 1);
			if(check.charAt(0) == '/') check = check.substring(1);
			General.plugin.freeze(sender);
			Bukkit.getServer().dispatchCommand(sender, check);
			return true;
		} else if(args[0].equalsIgnoreCase("restrict")) {
			if(Toolbox.lacksPermission(sender, "general.admin.restrict"))
				return Messaging.lacksPermission(sender, "administrate the General plugin");
			if(args.length < 2) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return doPermissions(sender, args[1], true);
		} else if(args[0].equalsIgnoreCase("release")) {
			if(Toolbox.lacksPermission(sender, "general.admin.release"))
				return Messaging.lacksPermission(sender, "administrate the General plugin");
			if(args.length < 2) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return doPermissions(sender, args[1], false);
		} else if(args[0].equalsIgnoreCase("kit")) {
			if(Toolbox.lacksPermission(sender, "general.admin.kit"))
				return Messaging.lacksPermission(sender, "administrate the General plugin");
			if(args.length < 3) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return kitEdit(sender, Arrays.copyOfRange(args, 1, args.length));
		} else if(args[0].equalsIgnoreCase("economy")) {
			if(Toolbox.lacksPermission(sender, "general.admin.economy"))
				return Messaging.lacksPermission(sender, "administrate the General plugin");
			if(args.length < 3) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return setEconomy(sender, Arrays.copyOfRange(args, 1, args.length));
		} else if(args[0].equalsIgnoreCase("set")) {
			if(Toolbox.lacksPermission(sender, "general.admin.set"))
				return Messaging.lacksPermission(sender, "administrate the General plugin");
			if(args.length < 3) {
				Messaging.send(sender, "&cNot enough arguments.");
				return SHOW_USAGE;
			}
			return setVar(sender, Arrays.copyOfRange(args, 1, args.length));
		}
		return SHOW_USAGE;
	}

	private boolean doPermissions(CommandSender sender, String node, boolean opsOnly) {
		List<String> restricted = plugin.config.getStringList("permissions.ops-only", null);
		if(opsOnly) restricted.add(node);
		else restricted.remove(node);
		plugin.config.setProperty("permissions.ops-only", restricted);
		Messaging.send(sender, "Permission '" + node + "' has been " + (opsOnly ? "added to" : "removed from") + 
			" the list of permissions restricted to ops.");
		return SHOW_USAGE;
	}

	private void doReload(CommandSender sender) {
		plugin.loadAllConfigs();
		Messaging.send(sender, "&5General config reloaded.");
	}
	
	private void doSave(CommandSender sender) {
		plugin.config.save();
		Items.save();
		Kits.save();
		Messaging.send(sender, "&5General config saved.");
	}
	
	private boolean kitEdit(CommandSender sender, String[] args) {
		if(args.length < 2) return SHOW_USAGE;
		String kitName = args[0];
		if(!Kits.kits.containsKey(kitName) && !args[1].equals("add")) {
			Messaging.send(sender, "There is no kit by the name of '" + kitName + "'.");
			return true;
		}
		if(args[1].equalsIgnoreCase("add")) {
			Kit kit;
			if(Kits.kits.containsKey(kitName))
				kit = Kits.kits.get(kitName);
			else {
				Messaging.send(sender, "New kit '" + kitName + "' created.");
				kit = new Kit(kitName, 0, 0);
				Kits.kits.put(kitName, kit);
				if(args.length < 3) return true;
			}
			if(args.length < 3) return SHOW_USAGE;
			ItemID item = Items.validate(args[2]);
			int amount = 1;
			if(args.length >= 4) try {
				amount = Integer.parseInt(args[3]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cInvalid amount.");
				return true;
			}
			kit.add(item, amount);
			Messaging.send(sender, amount + " of " + item.getName() + " added to kit '" + kitName + "'.");
			return true;
		} else if(args[1].equalsIgnoreCase("remove")) {
			Kit kit = Kits.kits.get(kitName);
			if(args.length < 3) return SHOW_USAGE;
			ItemID item = Items.validate(args[2]);
			if(!kit.contains(item)) {
				Messaging.send(sender, "The kit '" + kitName + "' does not include " + item.getName());
				return true;
			}
			int amount = Integer.MAX_VALUE;
			if(args.length >= 4) try {
				amount = Integer.parseInt(args[3]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cInvalid amount.");
				return true;
			}
			kit.add(item, -amount);
			Messaging.send(sender, (kit.contains(item) ? amount : "All") + " of " + item.getName() +
				" removed from kit '" + kitName + "'.");
			return true;
		} else if(args[1].equalsIgnoreCase("delay")) {
			Kit kit = Kits.kits.get(kitName);
			int delay;
			try {
				delay = Integer.parseInt(args[2]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cInvalid delay.");
				return true;
			}
			kit.delay = delay;
			Messaging.send(sender, "Delay of kit '" + kitName + "' set to " + delay + " milliseconds.");
			return true;
		} else if(args[1].equalsIgnoreCase("cost")) {
			Kit kit = Kits.kits.get(kitName);
			double cost;
			try {
				cost = Double.parseDouble(args[2]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cInvalid cost.");
				return true;
			}
			kit.setSavedCost(cost);
			String displayCost = Double.toString(cost);
			if(plugin.economy != null) displayCost = plugin.economy.formatCost(cost);
			Messaging.send(sender, "Cost of kit '" + kitName + "' set to " + displayCost);
			return true;
		} else if(args[1].equalsIgnoreCase("trash")) {
			Kits.kits.remove(kitName);
			Messaging.send(sender, "Kit '" + kitName + "' has been deleted.");
			return true;
		} else if(args[1].equalsIgnoreCase("list")) {
			Kit kit = Kits.kits.get(kitName);
			StringBuilder items = new StringBuilder();
			for(ItemID item : kit) {
				items.append(kit.get(item));
				items.append(" of ");
				items.append(item.getName());
				items.append(", ");
			}
			int lastComma = items.lastIndexOf(", ");
			if(lastComma >= 0) items.delete(lastComma, items.length());
			Messaging.send(sender, "Kit '" + kitName + "' contains: " + items.toString());
		}
		return SHOW_USAGE;
	}

	private boolean setEconomy(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return SHOW_USAGE;
	}

	private boolean setVar(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return SHOW_USAGE;
	}
	
	private boolean itemEdit(CommandSender sender, String[] args) {
		if(args.length < 2 || args.length > 3) {
			return SHOW_USAGE;
		} else if(args[0].equalsIgnoreCase("alias")) {
			switch(args.length) {
			case 2:
				if(args[1].charAt(0) == '-') {
					Items.removeAlias(args[1].substring(1));
					Messaging.send(sender, "Alias " + args[1].substring(1) + " removed.");
				} else {
					Messaging.send(sender, "The alias " + args[1] + " refers to " + Items.getAlias(args[1]));
				}
				return true;
			case 3:
				ItemID id = Items.validate(args[2]);
				Items.addAlias(args[1], id);
				Messaging.send(sender, "Alias " + args[1] + " added for " + id);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("variant")) {
			ItemID id = Items.validate(args[1]);
			if(id == null) {
				Messaging.send(sender, "&cNo such item.");
				return true;
			}
			switch(args.length) {
			case 2:
				Messaging.send(sender, "Variant names for " + id + ": " + Items.variantNames(id));
				return true;
			case 3:
				switch(args[2].charAt(0)) {
				default:
					if(args[2].contains(",")) Items.setVariantNames(id, Arrays.asList(args[2].split(",")));
					else Items.addVariantName(id, args[2]);
				break;
				case '+':
					Items.addVariantName(id, args[2].substring(1));
				break;
				case '-':
					Items.removeVariantName(id, args[2].substring(1));
				break;
				case '=':
					Items.setVariantNames(id, Arrays.asList(args[2].substring(1).split(",")));
				break;
				}
				Messaging.send(sender, "Variant names for " + id + " are now: " + Items.variantNames(id));
				return true;
			}
		} else if(args[0].equalsIgnoreCase("name")) {
			ItemID id = Items.validate(args[1]);
			switch(args.length) {
			case 2:
				String name = Items.name(id);
				Messaging.send(sender, "The name of item ID " + id + " is " + name);
				return true;
			case 3:
				Items.setItemName(id, args[2].replace("_", " "));
				Messaging.send(sender, "Item ID " + id + " is now called " + args[2].replace("_", " "));
				return true;
			}
		} else if(args[0].equalsIgnoreCase("hook")) {
			String hook[] = args[1].split("[:/,.|]");
			switch(args.length) {
			case 2:
				Messaging.send(sender,
						"The hook " + hook[0] + ":" + hook[1] + " refers to " + Items.getHook(hook[0], hook[1]));
				return true;
			case 3:
				ItemID id = Items.validate(args[2]);
				Items.setHook(hook[0], hook[1], id);
				Messaging.send(sender, "The hook " + hook[0] + ":" + hook[1] + " now refers to " + id);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("group")) {
			// TODO
		}
		return SHOW_USAGE;
	}
	
}
