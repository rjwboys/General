
package net.craftstars.general.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.items.Kit;
import net.craftstars.general.items.Kits;
import net.craftstars.general.mobs.MobData;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.teleport.DestinationType;
import net.craftstars.general.teleport.TargetType;
import net.craftstars.general.util.HelpHandler;
import net.craftstars.general.util.MessageOfTheDay;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class generalCommand extends CommandBase {
	public generalCommand(General instance) {
		super(instance);
	}
	
	private boolean isHelp(String help) {
		return help.equalsIgnoreCase("help");
	}
	
	@Override
	protected boolean isHelpCommand(Command command, String commandLabel, String[] args) {
		if(isHelp(args[0])) return true;
		if(args[0] == "economy") {
			String[] ecoArgs = Toolbox.join(args, 0).split("\\s*=\\s*");
			return ecoArgs.length == 1;
		}
		switch(args.length) {
		case 0:
			return true;
		case 1:
			return isHelp(args[0]);
		case 2:
			return isHelp(args[1]) || (args[0]+args[1]).equalsIgnoreCase("setlist");
		case 3:
			if(Toolbox.equalsOne(args[0], "kit", "set", "item"))
				return isHelp(args[1]);
			return isHelp(args[2]);
		}
		return false;
	}
	
	@Override
	protected String getHelpTopic(Command command, String commandLabel, String[] args) {
		if(isHelp(args[0])) return Toolbox.join(args, "_", 1);
		if(args[0] == "economy") {
			String[] ecoArgs = Toolbox.join(args, 0).split("\\s*=\\s*");
			return "general_economy_" + ecoArgs[0].replace(' ', '_');
		}
		switch(args.length) {
		case 0:
			return "about";
		case 1:
			return "toc";
		case 2:
			if((args[0]+args[1]).equalsIgnoreCase("setlist")) return "general_set_list";
			return command.getName() + "_" + args[0];
		case 3:
			if(isHelp(args[2])) return command.getName() + "_" + args[0] + "_" + args[1];
			else if(Toolbox.equalsOne(args[0], "kit", "set", "item")) return Toolbox.join(args, "_");
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String,Object> params) {
		String[] args = (String[]) params.get("args");
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
			Player who = sender instanceof Player ? (Player) sender : Toolbox.matchPlayer(args[0]);
			if(who == null) {
				Messaging.send(sender, "Please specify the player you would like to run the command as.");
				return true;
			}
			String check = Toolbox.join(args, 1);
			if(check.charAt(0) == '/') check = check.substring(1);
			freeze(who);
			plugin.getServer().dispatchCommand(who, check);
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
			Messaging.send(sender, "Its delay is " + kit.delay + " and its recorded cost is " + kit.getCost() + ".");
		}
		return SHOW_USAGE;
	}

	private boolean setEconomy(CommandSender sender, String[] args) {
		args = Toolbox.join(args, 0).split("\\s*=\\s*");
		if(args.length == 2) {
			double value;
			try {
				value = Double.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be an integer.");
				return true;
			}
			args = args[0].trim().split("\\s+");
			if(args.length == 0) return SHOW_USAGE;
			String path = "economy.";
			if(Toolbox.equalsOne(args[0], "heal", "hurt")) path += args[0];
			else if(args[0].equalsIgnoreCase("time")) {
				if(args.length == 1) {
					Messaging.send(sender, "&cWhich time?");
					return true;
				} else if(Toolbox.equalsOne(args[1], "day", "night", "dawn", "dusk", "noon", "midnight", "set"))
					path += args[1];
				else {
					Messaging.send(sender, "&cInvalid time.");
					return true;
				}
			} else if(args[0].equalsIgnoreCase("weather")) {
				if(args.length == 1) {
					Messaging.send(sender, "&cWhich weather?");
					return true;
				} else if(Toolbox.equalsOne(args[1], "storm", "thunder", "zap"))
					path += args[1];
				else {
					Messaging.send(sender, "&cInvalid weather.");
					return true;
				}
			} else if(args[0].equalsIgnoreCase("mobspawn")) {
				if(args.length == 1) {
					Messaging.send(sender, "&cWhich mob?");
					return true;
				} else {
					path += "mobspawn.";
					// Validate mob name
					MobType mob = MobType.getMob(args[1]);
					if(mob == null) {
						Messaging.send(sender, "&cInvalid mob.");
						return true;
					}
					path += mob.toString().toLowerCase().replace('_', '-');
					// Validate mob data
					String data = null, mountName = null, dataMount = null;
					switch(args.length) {
					case 2: // mobspawn <mob>
						break;
					case 3: // mobspawn <mob> free|<data>
						if(!args[2].equalsIgnoreCase("free"))
							data = args[2];
						break;
					case 4:
						if(!args[2].equalsIgnoreCase("riding")) {
							// mobspawn <mob> <data> free
							if(!args[3].equalsIgnoreCase("free")) {
								Messaging.send(sender, "Invalid mob specification.");
								return true;
							}
							data = args[2];
							break;
						} // mobspawn <mob> riding <mob>
						mountName = args[3];
						break;
					case 5:
						if(args[2].equalsIgnoreCase("riding")) {
							// mobspawn <mob> riding <mob> <data>
							mountName = args[3];
							dataMount = args[4];
						} else if(args[3].equalsIgnoreCase("riding")) {
							// mobspawn <mob> <data> riding <mob>
							data = args[2];
							mountName = args[4];
						} else {
							Messaging.send(sender, "Invalid mob specification.");
							return true;
						}
						break;
					case 6: // mobspawn <mob> <data> riding <mob> <data>
						if(!args[3].equalsIgnoreCase("riding")) {
							Messaging.send(sender, "Invalid mob specification.");
							return true;
						}
						data = args[2];
						mountName = args[4];
						dataMount = args[5];
						break;
					default:
						Messaging.send(sender, "Invalid mob specification.");
						return true;
					}
					// Validate basic data, if present
					MobData mobData = null, mountData = null;
					if(data != null) {
						mobData = MobData.parse(mob, null, data);
						if(mobData == null) {
							Messaging.send(sender, "Invalid mob data.");
							return true;
						}
					}
					// Validate mount, if present
					MobType mount = null;
					if(mountName != null) {
						mount = MobType.getMob(mountName);
						if(mount == null) {
							Messaging.send(sender, "&cInvalid mob for mount.");
							return true;
						}
						// Validate mount data, if present
						if(dataMount != null) {
							mountData = MobData.parse(mob, null, dataMount);
							if(mountData == null) {
								Messaging.send(sender, "Invalid mob data.");
								return true;
							}
						}
					}
					// Determine the minimal required path
					if(mobData != null) path += mobData.getCostNode(path);
					if(mount != null) {
						path += ".riding.";
						path += mount.toString().toLowerCase().replace('_', '-');
						if(mountData != null) path += mountData.getCostNode(path);
					}
					// Rewrite existing nodes to allow for the new path.
					if(!Toolbox.nodeExists(plugin.config, path)) {
						String checkPath = path;
						do {
							int lastDot = checkPath.lastIndexOf('.');
							checkPath = checkPath.substring(0, lastDot);
							if(checkPath.endsWith("mobspawn")) break;
							if(checkPath.endsWith("riding")) continue;
							if(!Toolbox.nodeExists(plugin.config, checkPath)) continue;
							Object node = plugin.config.getProperty(checkPath);
							if(node instanceof Number) {
								Map<String, Object> map = new HashMap<String,Object>();
								// Okay, we found a leaf, but where is it?
								// There are four possibilities.
								// 1. This is a riding node, and it's the mob name of the mount.
								//  ->If our path specifies a datavalue, we need to rearrange.
								if(checkPath.contains("riding")) {
									if(checkPath.lastIndexOf(".") == checkPath.indexOf("riding") + "riding".length()) {
										if(mount != null && path.replace(checkPath + ".", "").contains(".")) {
											map.put(mount.getNewData().getCostNode("").replace(".", ""), node);
											plugin.config.setProperty(checkPath, map);
										}
									}
								} else if(path.contains("riding")) { // but checkPath doesn't contain "riding"
									// 2. This is a riding node, and it's the mob data of the rider.
									//  ->Put the value in a "free" subnode.
									if(checkPath.replace("economy.mobspawn.", "").contains(".")) {
										map.put("free", node);
										plugin.config.setProperty(checkPath, map);
									}
									// 3. This may or may not be a riding node, and it's the mob name of the rider.
									//  ->If our path specifies a datavalue, we need to rearrange.
									else {
										map.put(mob.getNewData().getCostNode("").replace(".", ""), node);
										plugin.config.setProperty(checkPath, map);
									}
								}
								break;
							} else if(node instanceof Map && path.contains("riding") && !checkPath.contains("riding")) {
								if(((Map<?, ?>)node).containsKey("riding") || ((Map<?, ?>)node).containsKey("free")) {
									path = checkPath + ".free" + path.substring(lastDot);
									break;
								}
							}
						} while(checkPath.contains("."));
					}
				}
			} else if(args[0].equalsIgnoreCase("give")) {
				if(args.length == 1) {
					Messaging.send(sender, "&cWhich item?");
					return true;
				} else {
					ItemID item = Items.validate(args[1]);
					if(!item.isValid()) {
						Messaging.send(sender, "&cInvalid item.");
						return true;
					}
					path += "item" + item.toString();
				}
			} else if(Toolbox.equalsOne(args[0], "teleport", "setspawn")) {
				switch(args.length) {
				case 2:
					String target = null;
					if(args[0].equalsIgnoreCase("teleport")) {
						for(TargetType targetType : TargetType.values()) {
							if(args[1].equalsIgnoreCase(targetType.toString())) {
								target = args[1];
								break;
							}
						}
						if(target == null) {
							Messaging.send(sender, "Invalid teleport target.");
							return true;
						}
					} else if(args[0].equalsIgnoreCase("setspawn")) {
						if(!Toolbox.equalsOne(args[1], "self", "world", "other")) {
							Messaging.send(sender, "Invalid setspawn target.");
							return true;
						}
						target = args[1];
					}
					if(target == null) {
						Messaging.send(sender, "Invalid target.");
						return true;
					}
					path += target;
				case 3:
					if(args[1].equalsIgnoreCase("to")) {
						String dest = null;
						for(DestinationType destType : DestinationType.values()) {
							if(args[1].equalsIgnoreCase(destType.toString())) {
								dest = args[1];
								break;
							}
						}
						if(dest == null) {
							Messaging.send(sender, "Invalid destination.");
							return true;
						}
						path += "to." + dest;
					} else if(Toolbox.equalsOne(args[1], "into", "from")) {
						World world = Toolbox.matchWorld(args[2]);
						if(world == null) {
							Messaging.send(sender, "Invalid world.");
							return true;
						}
						path += args[1] + "." + world.getName();
					}
				}
			}
			plugin.config.setProperty(path, value);
			Messaging.send(sender, "Set economy value '" + path + "' to " + value + "!");
			return true;
		}
		return SHOW_USAGE;
	}

	private boolean setVar(CommandSender sender, String[] args) {
		String path = null;
		Object value = null;
		if(args.length != 2) return SHOW_USAGE;
		if(args[0].equalsIgnoreCase("permissions")) {
			path = "permissions.system";
			if(!Toolbox.equalsOne(args[1], "Basic", "Permissions", "WorldEdit")) {
				Messaging.send(sender, "&cInvalid permissions system.");
				return true;
			}
			String nice = Character.toUpperCase(args[1].charAt(0)) + args[1].substring(1).toLowerCase();
			value = nice;
		} else if(args[0].equalsIgnoreCase("others-for-all")) {
			path = "give.others-for-all";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("give-mass")) {
			path = "give.mass";
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be an integer.");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("show-health")) {
			path = "playerlist.show-health";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-coords")) {
			path = "playerlist.show-coords";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-world")) {
			path = "playerlist.show-world";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-ip")) {
			path = "playerlist.show-ip";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-motd")) {
			path = "show-motd";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("24-hour")) {
			path = "time.format-24-hour";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-ticks")) {
			path = "time.show-ticks";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("economy")) {
			path = "economy.system";
			if(!Toolbox.equalsOne(args[1], "None", "iConomy", "iConomy4", "iConomy5", "BOSEconomy")) {
				Messaging.send(sender, "&cInvalid economy system.");
				return true;
			}
			String nice = Character.toUpperCase(args[1].charAt(0)) + args[1].substring(1).toLowerCase();
			value = nice;
		} else if(args[0].equalsIgnoreCase("economy-take")) {
			path = "economy.give.take";
			if(!Toolbox.equalsOne(args[1], "trash", "sell")) {
				Messaging.send(sender, "&cInvalid economy-take method (must be trash or sell).");
				return true;
			}
			value = args[1];
		} else if(args[0].equalsIgnoreCase("economy-clear")) {
			path = "economy.give.clear";
			if(!Toolbox.equalsOne(args[1], "trash", "sell")) {
				Messaging.send(sender, "&cInvalid economy-clear method (must be trash or sell).");
				return true;
			}
			value = args[1];
		} else if(args[0].equalsIgnoreCase("economy-kits")) {
			path = "economy.give.kits";
			if(!Toolbox.equalsOne(args[1], "individual", "cumulative", "discount")) {
				Messaging.send(sender, "&cInvalid economy-kits method (must be individual, cumulative, or discount).");
				return true;
			}
			value = args[1];
		} else if(args[0].equalsIgnoreCase("economy-sell")) {
			path = "economy.give.sell";
			try {
				value = Double.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be a number.");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("kits-discount")) {
			path = "economy.give.discount";
			try {
				value = Double.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be a number.");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("chat-tag")) {
			path = "tag-fmt";
			value = args[1];
		} else if(args[0].equalsIgnoreCase("log-commands")) {
			path = "log-commands";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("auto-save")) {
			path = "auto-save";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("lightning-range")) {
			path = "lightning-range";
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be an integer.");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("teleport-warmup")) {
			path = "teleport.warmup";
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be an integer.");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("time-cooldown")) {
			path = "cooldown.time";
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be an integer.");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("storm-cooldown")) {
			path = "cooldown.storm";
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be an integer.");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("thunder-cooldown")) {
			path = "cooldown.thunder";
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be an integer.");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("lighting-cooldown")) {
			path = "cooldown.lightning";
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, "&cMust be an integer.");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("show-usage")) {
			path = "show-usage-on-fail";
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, "&cMust be a boolean.");
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else Messaging.send(sender, "&cUnknown variable: " + args[0]);
		if(path != null && value != null) {
			plugin.config.setProperty(path, value);
			Messaging.send(sender, "Variable " + args[0] + " set to " + value + ".");
		}
		return true;
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
			String groupName = args[1];
			//List<Integer> group = plugin.config.getIntList("give.groups." + groupName, null);
			switch(args.length) {
			case 2:
				List<Integer> group = Items.groupItems(groupName);
				if(group.isEmpty())
					Messaging.send(sender, "Group '" + groupName + "' does not exist or is empty.");
				else {
					StringBuilder items = new StringBuilder();
					for(int id : group) {
						items.append(Items.name(new ItemID(id)));
						items.append(", ");
					}
					int lastComma = items.lastIndexOf(", ");
					if(lastComma >= 0) items.delete(lastComma, items.length());
					Messaging.send(sender, "Group '" + groupName + "' contains: " + items.toString());
				}
				return true;
			case 3:
				if(args[2].equalsIgnoreCase("delete")) {
					Map<String, Object> allGroups = plugin.config.getNode("give.groups").getAll();
					if(!allGroups.containsKey(groupName))
						Messaging.send(sender, "Group '" + groupName + "' does not exist or is empty.");
					else {
						allGroups.remove(groupName);
						plugin.config.setProperty("general.groups", allGroups);
						Messaging.send(sender, "Group '" + groupName + "' has been deleted if it existed.");
					}
				} else switch(args[2].charAt(0)) {
				default:
					if(args[2].contains(",")) Items.setGroupItems(groupName, Arrays.asList(args[2].split(",")));
					else Items.addGroupItem(groupName, args[2]);
				break;
				case '+':
					Items.addGroupItem(groupName, args[2].substring(1));
				break;
				case '-':
					Items.removeGroupItem(groupName, args[2].substring(1));
				break;
				case '=':
					Items.setGroupItems(groupName, Arrays.asList(args[2].substring(1).split(",")));
				break;
				}
				Messaging.send(sender, "Group '" + groupName + "' now contains: " + Items.groupItems(groupName));
				return true;
			}
		}
		return SHOW_USAGE;
	}

	@Override // TODO: This is cheating
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		return Collections.singletonMap("args", (Object) args);
	}
	
}
